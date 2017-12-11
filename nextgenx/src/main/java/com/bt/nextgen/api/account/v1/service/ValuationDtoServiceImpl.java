package com.bt.nextgen.api.account.v1.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.account.api.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v1.model.CashManagementValuationDto;
import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v1.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v1.model.ManagedFundValuationDto;
import com.bt.nextgen.api.account.v1.model.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.account.v1.model.ParameterisedDatedAccountKey;
import com.bt.nextgen.api.account.v1.model.TermDepositValuationDto;
import com.bt.nextgen.api.account.v1.model.ValuationDto;
import com.bt.nextgen.api.account.v1.model.ValuationSummaryDto;
import com.bt.nextgen.api.account.v1.model.ValuationSummaryListDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.util.TermDepositUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("ValuationDtoServiceV1")
@Transactional(value = "springJpaTransactionManager")
// Sonar issues fixed in v2
@SuppressWarnings("all")
class ValuationDtoServiceImpl implements ValuationDtoService {
    @Autowired
    private AvaloqAccountIntegrationServiceFactory avaloqAccountIntegrationServiceFactory;

    @Autowired
    private PortfolioIntegrationServiceFactory avaloqPortfolioIntegrationServiceFactory;


    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    @Qualifier("ManagedFundAccountDtoServiceV1")
    private ManagedFundAccountDtoService mfaDtoService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public ValuationDto find(DatedAccountKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        String accountType = getAccountType(accountKey, serviceErrors);
        String mode = null;

        if (key instanceof ParameterisedDatedAccountKey) {
            ParameterisedDatedAccountKey paramKey = (ParameterisedDatedAccountKey) key;
            Map<String, String> params = paramKey.getParameters();
            mode = params.get("serviceType");
        }

        WrapAccountValuation valuation = avaloqPortfolioIntegrationServiceFactory.getInstance(mode)
                .loadWrapAccountValuation(
                accountKey, key.getEffectiveDate(), serviceErrors);
        WrapAccountDetail detail = avaloqAccountIntegrationServiceFactory.getInstance(mode).loadWrapAccountDetail(accountKey,
                serviceErrors);
        return buildValuationDto(key, accountType, detail, valuation, serviceErrors);
    }

    private String getAccountType(AccountKey accountKey, ServiceErrors serviceErrors) {
        String mode = null;

        WrapAccount account = avaloqAccountIntegrationServiceFactory.getInstance(mode)
                .loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        String accountType = null;
        if (account != null && account.getAccountStructureType() != null) {
            accountType = account.getAccountStructureType().name();
        }
        return accountType;
    }

    protected ValuationDto buildValuationDto(DatedAccountKey key, String accountType, WrapAccountDetail detail,
            WrapAccountValuation valuation, ServiceErrors serviceErrors) {
        if (valuation == null) {
            ValuationSummaryListDto valSummary = new ValuationSummaryListDto();
            return new ValuationDto(key, BigDecimal.ZERO, BigDecimal.ZERO, null, valSummary);
        }
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        BigDecimal balance = valuation.getBalance();
        ValuationSummaryDto cashManagement = getCashManagementValuations(valuation, balance);
        ValuationSummaryDto termDeposits = getTermDepositValuations(accountKey, valuation, balance, serviceErrors);
        ValuationSummaryDto managedPortfolios = getManagedPortfolioValuations(valuation, balance);
        ValuationSummaryDto managedFunds = getManagedFundValuations(detail, valuation, balance);

        ValuationSummaryListDto valSummary = new ValuationSummaryListDto(cashManagement, termDeposits, managedPortfolios,
                managedFunds);

        return new ValuationDto(key, balance, BigDecimal.ZERO, accountType, valSummary);
    }

    private ValuationSummaryDto getCashManagementValuations(WrapAccountValuation valuation, BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (subAccount.getAssetType() == AssetType.CASH) {
                InvestmentValuationDto valuationDto = buildCashManagementValuationDto(valuation.getAccountKey(), subAccount,
                        balance);
                valuationList.add(valuationDto);
            }
        }

        return getSummarisedValuation(valuationList, balance, AccountType.CASH.toString());
    }

    private ValuationSummaryDto getTermDepositValuations(AccountKey accountKey, WrapAccountValuation valuation,
            BigDecimal balance, ServiceErrors serviceErrors) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (subAccount.getAssetType() == AssetType.TERM_DEPOSIT) {
                TermDepositAccountValuation tdAcc = (TermDepositAccountValuation) subAccount;
                for (AccountHolding tdHolding : tdAcc.getHoldings()) {
                    InvestmentValuationDto valuationDto = buildTermDepositValuationDto(accountKey,
                            (TermDepositHoldingImpl) tdHolding, balance, serviceErrors);
                    valuationList.add(valuationDto);
                }
            }
        }
        return getSummarisedValuation(valuationList, balance, AccountType.TERM_DEPOSIT.toString());
    }

    private ValuationSummaryDto getManagedPortfolioValuations(WrapAccountValuation valuation, BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (subAccount.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                InvestmentValuationDto valuationDto = buildManagedPortfolioValuationDto(subAccount, balance);
                valuationList.add(valuationDto);
            }
        }

        Collections.sort(valuationList, new Comparator<InvestmentValuationDto>() {
            @Override
            public int compare(InvestmentValuationDto o1, InvestmentValuationDto o2) {
                String name1 = o1.getName() == null ? "" : o1.getName().toLowerCase();
                String name2 = o2.getName() == null ? "" : o2.getName().toLowerCase();
                return name1.compareTo(name2);
            }
        });
        return getSummarisedValuation(valuationList, balance, AccountType.MANAGED_PORTFOLIO.toString());
    }

    protected ValuationSummaryDto getManagedFundValuations(WrapAccountDetail detail, WrapAccountValuation valuation,
            BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (subAccount.getAssetType() == AssetType.MANAGED_FUND) {
                ManagedFundAccountValuationImpl mfVal = (ManagedFundAccountValuationImpl) subAccount;
                List<ManagedFundValuationDto> mfList = buildManagedFundValuationDto(detail, mfVal, balance);
                if (CollectionUtils.isNotEmpty(mfList)) {
                    valuationList.addAll(mfList);
                }
            }
        }

        Collections.sort(valuationList, new Comparator<InvestmentValuationDto>() {
            @Override
            public int compare(InvestmentValuationDto o1, InvestmentValuationDto o2) {
                String name1 = o1.getName() == null ? "" : o1.getName().toLowerCase();
                String name2 = o2.getName() == null ? "" : o2.getName().toLowerCase();
                return name1.compareTo(name2);
            }
        });

        return getSummarisedValuation(valuationList, balance, AccountType.MANAGED_FUND.toString());
    }

    private CashManagementValuationDto buildCashManagementValuationDto(AccountKey accountKey, SubAccountValuation subAccount,
            BigDecimal accountBalance) {
        // CashAccountValuation cashAccount = (CashAccountValuation)subAccount;
        EncodedString subAccountId = EncodedString.fromPlainText(accountKey.getId());

        // Accounts must always have exactly one cash account in V1 of the
        // service.
        CashHolding holding = (CashHolding) subAccount.getHoldings().get(0);

        String name = holding.getAccountName();
        BigDecimal subAccountBalance = subAccount.getMarketValue();
        BigDecimal subAccountAvailableBalance = roundToZeroIfNegative(holding.getAvailableBalance());
        BigDecimal interestRate = holding.getInterestRate();
        BigDecimal interestEarned = subAccount.getAccruedIncome();
        CashManagementValuationDto cashManagementValuation = new CashManagementValuationDto(subAccountId.toString(), name, null,
                null, subAccountBalance, PortfolioUtils.getValuationAsPercent(subAccountBalance, accountBalance),
                subAccountAvailableBalance, interestRate, interestEarned);
        return cashManagementValuation;
    }

    private BigDecimal roundToZeroIfNegative(BigDecimal subAccountAvailableBalance) {
        if (subAccountAvailableBalance != null && subAccountAvailableBalance.compareTo(BigDecimal.ZERO) < 0) {
            subAccountAvailableBalance = BigDecimal.ZERO;
        }
        return subAccountAvailableBalance;
    }

    private TermDepositValuationDto buildTermDepositValuationDto(AccountKey accountKey,
            TermDepositHoldingImpl termDepositHolding, BigDecimal accountBalance, ServiceErrors serviceErrors) {
        BigDecimal subAccountBalance = termDepositHolding.getMarketValue();
        // TODO fill these out. Are they all required? its very fat...
        // any duplication or derived fields?

        // String name = termDepositAccount.getAccountName();
        Boolean hasBreakInProgress = termDepositHolding.getHasPending();
        BigDecimal interestRate = termDepositHolding.getInterestRate();
        BigDecimal interestEarned = termDepositHolding.getAccruedIncome();
        DateTime maturityDate = termDepositHolding.getMaturityDate();

        Integer daysLeft = Days.daysBetween(new DateTime().withTimeAtStartOfDay(), maturityDate.withTimeAtStartOfDay()).getDays(); // TODO
                                                                                                                                   // -
                                                                                                                                   // calculated
                                                                                                                                   // from
                                                                                                                                   // maturity
                                                                                                                                   // date
        BigDecimal percentCompleted = BigDecimal.ZERO; // TODO - calculated from
                                                       // maturity date
        BigDecimal balanceOnMaturity = BigDecimal.ZERO; // TODO - source unknown
        BigDecimal interestYetToBeEarned = BigDecimal.ZERO; // TODO - source
                                                            // unknown

        String maturityInstructionId = termDepositHolding.getMaturityInstruction();
        String maturityInstruction = "";
        if (StringUtils.isNotBlank(maturityInstructionId)) {
            maturityInstruction = staticIntegrationService.loadCode(CodeCategory.TD_RENEW_MODE, maturityInstructionId,
                    new ServiceErrorsImpl()).getName();
            maturityInstruction = TermDepositUtil.getMaturityInstructionForDisplay(maturityInstruction, "BT Cash", true);
        }
        TermDepositPresentation termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(accountKey,
                termDepositHolding.getAsset().getAssetId(), serviceErrors);

        EncodedString subAccountId = EncodedString.fromPlainText(termDepositHolding.getHoldingKey().getHid().getId());
        BigDecimal principal = termDepositHolding.getBalance();
        String logo = null; // TODO source asset*static*cms

        return new TermDepositValuationDto(subAccountId.toString(), termDepositPresentation.getBrandName(),
                termDepositPresentation.getBrandClass(), subAccountBalance, termDepositHolding.getAvailableBalance(),
                PortfolioUtils.getValuationAsPercent(subAccountBalance, accountBalance), interestRate, interestEarned,
                maturityDate, hasBreakInProgress, maturityInstructionId, maturityInstruction, balanceOnMaturity,
                interestYetToBeEarned, principal, daysLeft, percentCompleted, BigDecimal.ZERO, BigDecimal.ZERO, logo,
                termDepositPresentation.getTerm(), termDepositPresentation.getPaymentFrequency());
    }

    private ManagedPortfolioValuationDto buildManagedPortfolioValuationDto(SubAccountValuation subAccount,
            BigDecimal accountBalance) {
        ManagedPortfolioAccountValuation managedPortfolioAccount = (ManagedPortfolioAccountValuation) subAccount;
        EncodedString subAccountId = EncodedString.fromPlainText(managedPortfolioAccount.getSubAccountKey().getId());
        String assetCode = managedPortfolioAccount.getAsset() != null ? managedPortfolioAccount.getAsset().getAssetCode() : "";
        String assetId = managedPortfolioAccount.getAsset() != null ? managedPortfolioAccount.getAsset().getAssetId() : "";
        BigDecimal estimatedYield = managedPortfolioAccount.getYield();
        BigDecimal balance = managedPortfolioAccount.getMarketValue();
        BigDecimal averageCost = managedPortfolioAccount.getCost();
        BigDecimal estimatedGain = balance.subtract(averageCost);
        BigDecimal interestPaid = BigDecimal.ZERO;
        BigDecimal dividend = BigDecimal.ZERO;
        BigDecimal distribution = BigDecimal.ZERO;
        Boolean hasPending = hasPending(managedPortfolioAccount.getHoldings());

        for (AccountHolding holding : managedPortfolioAccount.getHoldings()) {
            AssetType assetType = holding.getAsset().getAssetType();
            BigDecimal interestEarned = holding.getAccruedIncome();
            if (interestEarned == null) {
                continue;
            }
            if (AssetType.CASH == assetType) {
                interestPaid = interestPaid.add(interestEarned);
            } else if (AssetType.SHARE == assetType) {
                dividend = dividend.add(interestEarned);
            } else if (AssetType.MANAGED_FUND == assetType) {
                distribution = distribution.add(interestEarned);
            }
        }

        BigDecimal totalInterest = interestPaid.add(dividend).add(distribution);
        balance = balance.add(totalInterest);

        List<InvestmentAssetDto> dtoList = getInvestmentList(balance, managedPortfolioAccount.getHoldings());

        ManagedPortfolioValuationDto valuation = new ManagedPortfolioValuationDto(subAccountId.toString(), assetId,
                "Managed portfolio", assetCode, estimatedYield, balance, managedPortfolioAccount.getAvailableBalance(),
                PortfolioUtils.getValuationAsPercent(balance, accountBalance), averageCost, estimatedGain, interestPaid,
                dividend, distribution, hasPending, PortfolioUtils.getValuationAsPercent(totalInterest, balance), dtoList);
        return valuation;
    }

    protected List<ManagedFundValuationDto> buildManagedFundValuationDto(WrapAccountDetail detail, SubAccountValuation mfAccount,
            BigDecimal accountBalance) {
        List<ManagedFundValuationDto> mfDtoList = new ArrayList<>();
        if (mfAccount == null || CollectionUtils.isEmpty(mfAccount.getHoldings())) {
            return mfDtoList;
        }

        for (AccountHolding mfHolding : mfAccount.getHoldings()) {
            EncodedString subAccountId = EncodedString.fromPlainText(mfHolding.getHoldingKey().getHid().getId());
            String name = mfHolding.getHoldingKey().getName();
            BigDecimal balance = mfHolding.getMarketValue();
            BigDecimal interestEarned = mfHolding.getAccruedIncome();

            List<InvestmentAssetDto> dtoList = getInvestmentList(balance, Collections.singletonList(mfHolding));
            if (!(dtoList.isEmpty())) {
                // A Pending-Sell-Down managed fund can be identified when it
                // has outstanding transaction, has valid market value and
                // available units is 0.
                boolean pendingSellDown = mfHolding.getHasPending() && mfHolding.getMarketValue().compareTo(BigDecimal.ZERO) > 0
                        && mfHolding.getAvailableUnits().compareTo(BigDecimal.ZERO) == 0;
                ValuationSummaryDto summaryDto = new ValuationSummaryDto(balance, PortfolioUtils.getValuationAsPercent(balance,
                        accountBalance), interestEarned, null, null);

                String distributionMethod = ((ManagedFundHolding) mfHolding).getDistributionMethod() == null ? null
                        : ((ManagedFundHolding) mfHolding).getDistributionMethod().getDisplayName();

                List<DistributionMethod> availableMethods = mfaDtoService.getAvailableDistributionMethod(detail,
                        mfHolding.getAsset());
                List<String> methods = Lambda.convert(availableMethods, new PropertyExtractor<DistributionMethod, String>(
                        "displayName"));

                mfDtoList.add(new ManagedFundValuationDto(subAccountId.toString(), name, summaryDto, mfHolding
                        .getAvailableBalance(), dtoList.get(0), pendingSellDown, distributionMethod, methods));
            }
        }
        return mfDtoList;
    }

    private List<InvestmentAssetDto> getInvestmentList(BigDecimal mpBalance, List<AccountHolding> holdings) {
        List<InvestmentAssetDto> investments = new ArrayList<>();
        for (AccountHolding holding : holdings) {
            String assetId = holding.getAsset().getAssetId();
            String assetType = holding.getAsset().getAssetType().name();
            Boolean isPrepaymentAsset = Boolean.FALSE;
            if (holding.getReferenceAsset() != null) {
                isPrepaymentAsset = Boolean.TRUE;
            }
            String assetName = holding.getAsset().getAssetName();
            String assetCode = holding.getAsset().getAssetCode();
            String status = holding.getAsset().getStatus() != null ? holding.getAsset().getStatus().getDisplayName() : null;
            DateTime unitPriceDate = holding.getUnitPriceDate();
            BigDecimal quantity = holding.getUnits();
            BigDecimal unitPrice = holding.getUnitPrice();
            BigDecimal averageCost = holding.getCost();
            BigDecimal balance = holding.getMarketValue();
            BigDecimal estimatedGain = balance.subtract(averageCost);
            BigDecimal allocationPercent = PortfolioUtils.getValuationAsPercent(balance, mpBalance);
            Boolean hasPending = holding.getHasPending();

            InvestmentAssetDto investment = new InvestmentAssetDto(assetId, assetType, assetName, assetCode, unitPriceDate,
                    quantity, unitPrice, averageCost, balance, estimatedGain, allocationPercent, holding.getAvailableUnits(),
                    status, hasPending, isPrepaymentAsset);
            investments.add(investment);
        }

        // Sort investment list into alphabetic order, with CASH type assets
        // first
        Collections.sort(investments, new Comparator<InvestmentAssetDto>() {
            @Override
            public int compare(InvestmentAssetDto o1, InvestmentAssetDto o2) {
                String name1 = o1.getAssetName() == null ? "" : o1.getAssetName().toLowerCase();
                String name2 = o2.getAssetName() == null ? "" : o2.getAssetName().toLowerCase();

                // If one asset is CASH, that one comes first
                if (AssetType.CASH.name() == o1.getAssetType() && AssetType.CASH.name() != o2.getAssetType()) {
                    return -1;
                } else if (AssetType.CASH.name() != o1.getAssetType() && AssetType.CASH.name() == o2.getAssetType()) {
                    return 1;
                } else {
                    // Both are CASH or both are not CASH, compare
                    // lexicographically
                    return name1.compareTo(name2);
                }
            }
        });

        return investments;
    }

    private ValuationSummaryDto getSummarisedValuation(List<InvestmentValuationDto> valuations, BigDecimal totalPv,
            String accountType) {
        ValuationSummaryDto summary = null;
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal income = BigDecimal.ZERO;

        if (valuations != null && valuations.size() > 0) {
            if (accountType.equals(AccountType.MANAGED_PORTFOLIO.toString())) {
                for (InvestmentValuationDto valuation : valuations) {
                    balance = balance.add(valuation.getBalance());
                    income = income.add(valuation.getIncome());
                }
            } else {
                for (InvestmentValuationDto valuation : valuations) {
                    balance = balance.add(valuation.getBalance()).add(valuation.getIncome());
                    income = income.add(valuation.getIncome());
                }
            }

            summary = new ValuationSummaryDto(balance, PortfolioUtils.getValuationAsPercent(balance, totalPv), income,
                    PortfolioUtils.getValuationAsPercent(income, totalPv), valuations);
        }

        return summary;
    }

    private boolean hasPending(List<AccountHolding> holdings) {
        for (AccountHolding holding : holdings) {
            if (holding.getHasPending()) {
                return true;
            }
        }
        return false;
    }

}
