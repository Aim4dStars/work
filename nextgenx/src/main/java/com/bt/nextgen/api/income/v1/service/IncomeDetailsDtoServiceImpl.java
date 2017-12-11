package com.bt.nextgen.api.income.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.income.v1.model.CashIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.CashIncomeDto;
import com.bt.nextgen.api.income.v1.model.DistributionIncomeDto;
import com.bt.nextgen.api.income.v1.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v1.model.FeeRebateIncomeDto;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v1.model.IncomeDto;
import com.bt.nextgen.api.income.v1.model.ManagedFundIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.ManagedPortfolioIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.ShareIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.TermDepositIncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.TermDepositIncomeDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.income.CashIncome;
import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @deprecated use version 2
 */
@Deprecated
@Transactional(value = "springJpaTransactionManager")
@Service("IncomeDetailsDtoServiceV1")
@SuppressWarnings("squid:S1200") // fixed in v2
class IncomeDetailsDtoServiceImpl implements IncomeDetailsDtoService {
    @Autowired
    private IncomeIntegrationService incomeIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    @Override
    public IncomeDetailsDto find(IncomeDetailsKey key, ServiceErrors serviceErrors) {
        List<WrapAccountIncomeDetails> incomes = null;

        if (IncomeDetailsType.RECEIVED.equals(key.getType())) {
            AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
            incomes = incomeIntegrationService.loadIncomeReceivedDetails(accountKey, key.getStartDate(), key.getEndDate(),
                    serviceErrors);
            // Support only one main account for now
            if (incomes != null && !incomes.isEmpty() && incomes.get(0).getSubAccountIncomeDetailsList() != null) {
                return toIncomeDto(key, incomes.get(0).getSubAccountIncomeDetailsList(), serviceErrors);
            }
            return toIncomeDto(key, new ArrayList<SubAccountIncomeDetails>(), serviceErrors);
        } else {
            // load details from wrap-account valuation service
            return getIncomeAccruedDto(key, serviceErrors);
        }
    }

    private IncomeDetailsDto toIncomeDto(IncomeDetailsKey key, List<SubAccountIncomeDetails> accountIncomeDetails,
            ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        CashIncomeDetailsDto cashIncomeDetails = toCashIncomeDetailsDto(
                filterBySubaccountType(accountIncomeDetails, AssetType.CASH));
        TermDepositIncomeDetailsDto tdIncomeDetails = toTdIncomeDetailsDto(accountKey,
                filterBySubaccountType(accountIncomeDetails, AssetType.TERM_DEPOSIT), serviceErrors);
        ManagedPortfolioIncomeDetailsDto mpIncomeDetails = toMpIncomeDetailsDto(
                filterBySubaccountType(accountIncomeDetails, AssetType.MANAGED_PORTFOLIO));
        ManagedFundIncomeDetailsDto mfIncomeDetails = toMfIncomeDetailsDto(
                filterBySubaccountType(accountIncomeDetails, AssetType.MANAGED_FUND));
        ShareIncomeDetailsDto shareIncomeDetails = toShareIncomeDetailsDto(
                filterBySubaccountType(accountIncomeDetails, AssetType.SHARE));

        return new IncomeDetailsDto(key, cashIncomeDetails, tdIncomeDetails, mpIncomeDetails, mfIncomeDetails,
                shareIncomeDetails);
    }

    private CashIncomeDetailsDto toCashIncomeDetailsDto(List<SubAccountIncomeDetails> cashSubAccounts) {
        List<CashIncomeDto> targetIncomes = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        // Note - we only expect a single result here.
        for (SubAccountIncomeDetails subAccountIncome : cashSubAccounts) {
            for (HoldingIncomeDetails cashHolding : subAccountIncome.getIncomes()) {
                Asset cashAsset = cashHolding.getAsset();
                for (Income sourceIncome : cashHolding.getIncomes()) {
                    CashIncome income = (CashIncome) sourceIncome;
                    total = total.add(income.getAmount());
                    targetIncomes.add(new CashIncomeDto(cashAsset == null ? null : cashAsset.getAssetName(),
                            cashAsset == null ? null : cashAsset.getAssetId(), income.getPaymentDate(), income.getAmount()));
                }
            }
        }
        Collections.sort(targetIncomes, new PaymentDateComparator());
        return new CashIncomeDetailsDto(targetIncomes, total);
    }

    private TermDepositIncomeDetailsDto toTdIncomeDetailsDto(AccountKey accountKey, List<SubAccountIncomeDetails> tdSubAccounts,
            ServiceErrors serviceErrors) {
        List<TermDepositIncomeDto> targetIncomes = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (SubAccountIncomeDetails subAccountIncome : tdSubAccounts) {
            for (HoldingIncomeDetails tdHolding : subAccountIncome.getIncomes()) {
                TermDepositAsset tdAsset = (TermDepositAsset) tdHolding.getAsset();
                String assetId = (tdAsset == null) ? null : tdAsset.getAssetId();
                TermDepositPresentation tdPres = termDepositPresentationService.getTermDepositPresentation(accountKey, assetId,
                        serviceErrors);
                DateTime maturityDate = tdAsset == null ? null : tdAsset.getMaturityDate();
                for (Income sourceIncome : tdHolding.getIncomes()) {
                    TermDepositIncome income = (TermDepositIncome) sourceIncome;
                    total = total.add(income.getInterest());

                    targetIncomes
                            .add(new TermDepositIncomeDto(tdPres.getBrandName(), tdPres.getBrandClass(), income.getPaymentDate(),
                                    maturityDate, income.getInterest(), tdPres.getTerm(), tdPres.getPaymentFrequency()));
                }
            }
        }

        Collections.sort(targetIncomes, new PaymentDateComparator());
        return new TermDepositIncomeDetailsDto(targetIncomes, total);
    }

    private ManagedPortfolioIncomeDetailsDto toMpIncomeDetailsDto(List<SubAccountIncomeDetails> mpSubAccounts) {

        List<CashIncomeDto> targetCashIncomes = toMpCashIncomes(mpSubAccounts);
        List<DividendIncomeDto> targetDivIncomes = toMpDividends(mpSubAccounts);
        List<DistributionIncomeDto> targetDisIncomes = toMpDistributions(mpSubAccounts);
        List<FeeRebateIncomeDto> targetFeeRebates = toMpFeeRebates(mpSubAccounts);

        return new ManagedPortfolioIncomeDetailsDto(targetCashIncomes, targetDivIncomes, targetDisIncomes, targetFeeRebates);
    }

    private List<CashIncomeDto> toMpCashIncomes(List<SubAccountIncomeDetails> mpSubAccounts) {
        List<CashIncomeDto> targetCashIncomes = new ArrayList<CashIncomeDto>();

        for (SubAccountIncomeDetails subAccountIncome : mpSubAccounts) {
            for (HoldingIncomeDetails holding : subAccountIncome.getIncomes()) {
                Asset mpAsset = holding.getAsset();
                final AssetType mpAssetType = mpAsset.getAssetType();
                if (mpAssetType == AssetType.CASH) {
                    for (Income sourceIncome : holding.getIncomes()) {
                        CashIncome cashIncome = (CashIncome) sourceIncome;
                        targetCashIncomes.add(new CashIncomeDto(mpAsset.getAssetName(), mpAsset.getAssetCode(),
                                cashIncome.getPaymentDate(), cashIncome.getAmount()));
                    }
                }
            }
        }
        PaymentDateComparator comparator = new PaymentDateComparator();
        Collections.sort(targetCashIncomes, comparator);
        return targetCashIncomes;
    }

    private List<DividendIncomeDto> toMpDividends(List<SubAccountIncomeDetails> mpSubAccounts) {
        List<DividendIncomeDto> targetDivIncomes = new ArrayList<DividendIncomeDto>();

        for (SubAccountIncomeDetails subAccountIncome : mpSubAccounts) {
            for (HoldingIncomeDetails holding : subAccountIncome.getIncomes()) {
                Asset mpAsset = holding.getAsset();
                final AssetType mpAssetType = mpAsset.getAssetType();
                if (mpAssetType == AssetType.SHARE) {
                    for (Income sourceIncome : holding.getIncomes()) {
                        DividendIncome income = (DividendIncome) sourceIncome;

                        targetDivIncomes.add(new DividendIncomeDto(mpAsset.getAssetName(), mpAsset.getAssetCode(),
                                income.getExecutionDate(), income.getPaymentDate(), income.getQuantity(), income.getIncomeRate(),
                                income.getFrankedDividend(), income.getUnfrankedDividend(), income.getFrankingCredit(),
                                income.getAmount()));
                    }
                }
            }
        }
        PaymentDateComparator comparator = new PaymentDateComparator();
        Collections.sort(targetDivIncomes, comparator);
        return targetDivIncomes;
    }

    private List<DistributionIncomeDto> toMpDistributions(List<SubAccountIncomeDetails> mpSubAccounts) {
        List<DistributionIncomeDto> targetDisIncomes = new ArrayList<DistributionIncomeDto>();

        for (SubAccountIncomeDetails subAccountIncome : mpSubAccounts) {
            for (HoldingIncomeDetails holding : subAccountIncome.getIncomes()) {
                Asset mpAsset = holding.getAsset();
                final AssetType mpAssetType = mpAsset.getAssetType();
                if (mpAssetType == AssetType.MANAGED_FUND) {
                    for (Income sourceIncome : holding.getIncomes()) {
                        DistributionIncome mfIncome = (DistributionIncome) sourceIncome;

                        DistributionIncomeDto distributionIncomeDto = new DistributionIncomeDto(mpAsset.getAssetName(),
                                mpAsset.getAssetCode(), mfIncome);

                        if (!distributionIncomeDto.getIsFeeRebate()) {
                            targetDisIncomes.add(distributionIncomeDto);
                        }

                    }

                }
            }
        }
        PaymentDateComparator comparator = new PaymentDateComparator();
        Collections.sort(targetDisIncomes, comparator);
        return targetDisIncomes;
    }

    private List<FeeRebateIncomeDto> toMpFeeRebates(List<SubAccountIncomeDetails> mpSubAccounts) {
        List<FeeRebateIncomeDto> targetFeeRebates = new ArrayList<FeeRebateIncomeDto>();

        for (SubAccountIncomeDetails subAccountIncome : mpSubAccounts) {
            for (HoldingIncomeDetails holding : subAccountIncome.getIncomes()) {
                Asset mpAsset = holding.getAsset();
                final AssetType mpAssetType = mpAsset.getAssetType();
                if (mpAssetType == AssetType.MANAGED_FUND) {
                    for (Income sourceIncome : holding.getIncomes()) {
                        DistributionIncome mfIncome = (DistributionIncome) sourceIncome;

                        DistributionIncomeDto distributionIncomeDto = new DistributionIncomeDto(mpAsset.getAssetName(),
                                mpAsset.getAssetCode(), mfIncome);

                        if (distributionIncomeDto.getIsFeeRebate()) {
                            targetFeeRebates.add(new FeeRebateIncomeDto(distributionIncomeDto));
                        }

                    }

                }
            }
        }
        PaymentDateComparator comparator = new PaymentDateComparator();
        Collections.sort(targetFeeRebates, comparator);
        return targetFeeRebates;
    }

    private ManagedFundIncomeDetailsDto toMfIncomeDetailsDto(List<SubAccountIncomeDetails> mfSubAccounts) {

        List<DistributionIncomeDto> targetDistributions = new ArrayList<DistributionIncomeDto>();
        List<FeeRebateIncomeDto> targetFeeRebates = new ArrayList<FeeRebateIncomeDto>();

        for (SubAccountIncomeDetails subAccountIncome : mfSubAccounts) {
            for (HoldingIncomeDetails disHolding : subAccountIncome.getIncomes()) {
                final Asset disAsset = disHolding.getAsset();
                for (Income sourceIncome : disHolding.getIncomes()) {
                    DistributionIncome income = (DistributionIncome) sourceIncome;

                    DistributionIncomeDto distributionIncomeDto = new DistributionIncomeDto(
                            disAsset == null ? null : disAsset.getAssetName(), disAsset == null ? null : disAsset.getAssetCode(),
                            income);

                    if (distributionIncomeDto.getIsFeeRebate()) {
                        targetFeeRebates.add(new FeeRebateIncomeDto(distributionIncomeDto));
                    } else {
                        targetDistributions.add(distributionIncomeDto);
                    }

                }
            }
        }
        PaymentDateComparator comparator = new PaymentDateComparator();
        Collections.sort(targetDistributions, comparator);
        Collections.sort(targetFeeRebates, comparator);

        return new ManagedFundIncomeDetailsDto(targetDistributions, targetFeeRebates);
    }

    private ShareIncomeDetailsDto toShareIncomeDetailsDto(List<SubAccountIncomeDetails> shareSubAccounts) {
        List<DistributionIncomeDto> targetDisIncomes = new ArrayList<>();
        List<DividendIncomeDto> targetDivIncomes = new ArrayList<>();
        for (SubAccountIncomeDetails subAccountIncome : shareSubAccounts) {
            for (HoldingIncomeDetails disHolding : subAccountIncome.getIncomes()) {
                Asset disAsset = disHolding.getAsset();
                for (Income sourceIncome : disHolding.getIncomes()) {
                    if (sourceIncome instanceof DividendIncome) {
                        DividendIncome income = (DividendIncome) sourceIncome;
                        targetDivIncomes.add(new DividendIncomeDto(disAsset == null ? null : disAsset.getAssetName(),
                                disAsset == null ? null : disAsset.getAssetCode(), income.getExecutionDate(),
                                income.getPaymentDate(), income.getQuantity(), income.getIncomeRate(),
                                income.getFrankedDividend(), income.getUnfrankedDividend(), income.getFrankingCredit(),
                                income.getAmount()));
                    } else {
                        DistributionIncome income = (DistributionIncome) sourceIncome;
                        targetDisIncomes.add(new DistributionIncomeDto(disAsset == null ? null : disAsset.getAssetName(),
                                disAsset == null ? null : disAsset.getAssetCode(), income));
                    }
                }

            }
        }
        PaymentDateComparator comparator = new PaymentDateComparator();
        Collections.sort(targetDisIncomes, comparator);
        return new ShareIncomeDetailsDto(targetDisIncomes, targetDivIncomes);
    }

    private IncomeDetailsDto getIncomeAccruedDto(IncomeDetailsKey key, ServiceErrors serviceErrors) {
        List<WrapAccountIncomeDetails> incomes = null;
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        // load cash details from wrap-account valuation service
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, key.getEndDate(),
                serviceErrors);

        CashIncomeDetailsDto mpCashIncomeDetailsDto = this.buildManagedPortfolioCashIncomeDto(valuation);
        ManagedPortfolioIncomeDetailsDto mpDto = new ManagedPortfolioIncomeDetailsDto(mpCashIncomeDetailsDto.getIncomes(), null,
                null, null);
        ManagedFundIncomeDetailsDto mfDto = null;
        ShareIncomeDetailsDto shareDto = null;

        // load distribution and dividend details from cash-dividend service
        incomes = incomeIntegrationService.loadCashDividendDetails(accountKey, key.getStartDate(), key.getType().getStatus(),
                serviceErrors);

        IncomeDetailsDto dividendDistributionDto = buildDividendsAndDistributions(key, incomes, serviceErrors);

        if (dividendDistributionDto != null) {

            if (dividendDistributionDto.getManagedPortfolioIncomeDetails() != null) {
                ManagedPortfolioIncomeDetailsDto mpIncomeDetails = dividendDistributionDto.getManagedPortfolioIncomeDetails();

                mpDto = new ManagedPortfolioIncomeDetailsDto(mpCashIncomeDetailsDto.getIncomes(), mpIncomeDetails.getDividends(),
                        mpIncomeDetails.getDistributions(), mpIncomeDetails.getFeeRebates());

            }
            if (dividendDistributionDto.getManagedFundIncomeDetails() != null) {
                mfDto = dividendDistributionDto.getManagedFundIncomeDetails();
            }
            if (dividendDistributionDto.getShareIncomeDetails() != null) {
                shareDto = dividendDistributionDto.getShareIncomeDetails();
            }
        }

        CashIncomeDetailsDto cashIncomeDetailsDto = this.buildCashIncomeDto(valuation);
        TermDepositIncomeDetailsDto tdIncomeDetailsDto = this.buildTermDepositIncomeDto(valuation, serviceErrors);
        IncomeDetailsDto detailsDto = new IncomeDetailsDto(key, cashIncomeDetailsDto, tdIncomeDetailsDto, mpDto, mfDto, shareDto);
        return detailsDto;
    }

    /**
     * Retrieve accrued income for Cash accounts.
     *
     * @param valuation
     * @return
     */
    private CashIncomeDetailsDto buildCashIncomeDto(WrapAccountValuation valuation) {
        List<CashIncomeDto> cashIncomeDtoList = new ArrayList<>();
        BigDecimal cashTotal = BigDecimal.ZERO;
        if (valuation != null && valuation.getSubAccountValuations() != null) {
            for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
                AssetType assetType = subAccount.getAssetType();
                if (AssetType.CASH == assetType) {
                    CashHolding holding = (CashHolding) subAccount.getHoldings().get(0);
                    // Process cash
                    CashIncomeDto cashDto = new CashIncomeDto(holding.getAccountName(), "", holding.getNextInterestDate(),
                            holding.getAccruedIncome());
                    cashTotal = cashTotal.add(subAccount.getAccruedIncome());
                    cashIncomeDtoList.add(cashDto);
                }
            }
        }
        CashIncomeDetailsDto cashIncomeDetailsDto = new CashIncomeDetailsDto(cashIncomeDtoList, cashTotal);
        return cashIncomeDetailsDto;
    }

    /**
     * Retrieve accrued income for all term-deposits within the specified
     * WrapAccountValuation.
     *
     * @param valuation
     * @return
     */
    private TermDepositIncomeDetailsDto buildTermDepositIncomeDto(WrapAccountValuation valuation, ServiceErrors serviceErrors) {
        List<TermDepositIncomeDto> tdIncomeDtoList = new ArrayList<>();
        BigDecimal tdTotal = BigDecimal.ZERO;
        if (valuation != null && valuation.getSubAccountValuations() != null) {
            for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
                AssetType assetType = subAccount.getAssetType();
                if (AssetType.TERM_DEPOSIT.equals(assetType)) {
                    TermDepositAccountValuation tdVal = (TermDepositAccountValuation) subAccount;
                    for (AccountHolding holding : tdVal.getHoldings()) {
                        TermDepositHoldingImpl td = (TermDepositHoldingImpl) holding;
                        TermDepositPresentation tdPres = termDepositPresentationService
                                .getTermDepositPresentation(valuation.getAccountKey(), td.getAsset().getAssetId(), serviceErrors);

                        TermDepositIncomeDto tdDto = new TermDepositIncomeDto(tdPres.getBrandName(), tdPres.getBrandClass(),
                                td.getNextInterestDate(), td.getMaturityDate(), td.getAccruedIncome(), tdPres.getTerm(),
                                tdPres.getPaymentFrequency());
                        tdTotal = tdTotal.add(holding.getAccruedIncome());
                        tdIncomeDtoList.add(tdDto);
                    }
                }
            }
        }
        TermDepositIncomeDetailsDto tdIncomeDetailsDto = new TermDepositIncomeDetailsDto(tdIncomeDtoList, tdTotal);
        return tdIncomeDetailsDto;
    }

    /**
     * Retrieve accrued incomes within cash account in all managed-portfolio
     * from the wrapAccountValuation specified.
     *
     * @param valuation
     * @return
     */
    private CashIncomeDetailsDto buildManagedPortfolioCashIncomeDto(WrapAccountValuation valuation) {
        List<CashIncomeDto> mpCashIncomeDtoList = new ArrayList<>();
        BigDecimal mpCashTotal = BigDecimal.ZERO;

        if (valuation != null && valuation.getSubAccountValuations() != null) {
            for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
                AssetType assetType = subAccount.getAssetType();
                if (AssetType.MANAGED_PORTFOLIO.equals(assetType)) {
                    ManagedPortfolioAccountValuation mpVal = (ManagedPortfolioAccountValuation) subAccount;
                    for (AccountHolding holding : mpVal.getHoldings()) {
                        // Only process cash asset within managed portfolio.
                        Asset asset = holding.getAsset();
                        if (AssetType.CASH.equals(asset.getAssetType())) {
                            CashHolding accHolding = (CashHolding) holding;

                            CashIncomeDto cashDto = new CashIncomeDto("Managed portfolio",
                                    mpVal.getAsset() == null ? "" : mpVal.getAsset().getAssetCode(),
                                    accHolding.getNextInterestDate(), accHolding.getAccruedIncome());

                            mpCashTotal = mpCashTotal.add(accHolding.getAccruedIncome());
                            mpCashIncomeDtoList.add(cashDto);
                        }
                    }
                }
            }
        }
        CashIncomeDetailsDto cashIncomeDetailsDto = new CashIncomeDetailsDto(mpCashIncomeDtoList, mpCashTotal);
        return cashIncomeDetailsDto;
    }

    private IncomeDetailsDto buildDividendsAndDistributions(IncomeDetailsKey key, List<WrapAccountIncomeDetails> incomes,
            ServiceErrors serviceErrors) {
        IncomeDetailsDto dto = null;
        if (incomes != null && !incomes.isEmpty() && incomes.get(0).getSubAccountIncomeDetailsList() != null) {
            dto = toIncomeDto(key, incomes.get(0).getSubAccountIncomeDetailsList(), serviceErrors);
        }

        return dto;
    }

    private List<SubAccountIncomeDetails> filterBySubaccountType(List<SubAccountIncomeDetails> unfiltered, AssetType assetType) {
        return Lambda.select(unfiltered,
                Lambda.having(Lambda.on(SubAccountIncomeDetails.class).getAssetType(), Matchers.equalTo(assetType)));
    }

    private static class PaymentDateComparator implements Comparator<IncomeDto>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(IncomeDto o1, IncomeDto o2) {
            DateTime o1Date = o1.getPaymentDate();
            DateTime o2Date = o2.getPaymentDate();

            if (o1Date == null) {
                return o2Date == null ? 0 : 1;
            }

            return o2Date == null ? -1 : o2Date.compareTo(o1Date);
        }
    }
}
