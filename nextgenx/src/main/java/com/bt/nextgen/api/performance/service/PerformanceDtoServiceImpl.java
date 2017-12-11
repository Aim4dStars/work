package com.bt.nextgen.api.performance.service;


import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.performance.model.ManagedPortfolioPerformanceDto;
import com.bt.nextgen.api.performance.model.PerformanceDto;
import com.bt.nextgen.api.performance.model.PortfolioPerformanceDto;
import com.bt.nextgen.api.performance.model.TermDepositPerformanceDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioPerformanceImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.performance.PortfolioPerformanceOverallImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(value = "springJpaTransactionManager")
// TODO This class is a disaster area and needs a rewrite. The problem extends
// down
// to the modelling of the AssetPerformance integration model.
class PerformanceDtoServiceImpl implements PerformanceDtoService {
    @Autowired
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    public PerformanceDtoServiceImpl() {
    }

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public PortfolioPerformanceDto find(DateRangeAccountKey key, ServiceErrors serviceErrors) {

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        Performance periodPerformance = null;
        BigDecimal performanceBeforeFee = null;

        BigDecimal twrrGrossPct = null;
        BigDecimal performanceAfterFee = null;
        BigDecimal performancePct = null;
        BigDecimal incomeRtn = null;
        BigDecimal capitalGrowth = null;

        WrapAccountPerformance accountPeriodPerformance = accountPerformanceService.loadAccountTotalPerformance(accountKey, null,
                key.getStartDate(), key.getEndDate(), serviceErrors);

        if (accountPeriodPerformance != null) {
            periodPerformance = accountPeriodPerformance.getPeriodPerformanceData();
            if (periodPerformance != null) {
                performanceBeforeFee = periodPerformance.getPerformanceBeforeFee();
                twrrGrossPct = periodPerformance.getTwrrGross() == null ? BigDecimal.ZERO
                        : periodPerformance.getTwrrGross().divide(BigDecimal.valueOf(100));
                performanceAfterFee = periodPerformance.getPerformanceAfterFee();
                performancePct = periodPerformance.getPerformance() == null ? BigDecimal.ZERO
                        : periodPerformance.getPerformance().divide(BigDecimal.valueOf(100));
                incomeRtn = periodPerformance.getIncomeRtn() == null ? BigDecimal.ZERO
                        : periodPerformance.getIncomeRtn().divide(BigDecimal.valueOf(100));
                capitalGrowth = periodPerformance.getCapitalGrowth() == null ? BigDecimal.ZERO
                        : periodPerformance.getCapitalGrowth().divide(BigDecimal.valueOf(100));
            }
        }

        PortfolioPerformanceOverallImpl accountPerformance = (PortfolioPerformanceOverallImpl) accountPerformanceService
                .loadAccountOverallPerformance(accountKey, key.getStartDate(), key.getEndDate(), serviceErrors);

        PortfolioPerformanceDto portfolioPerfDto = new PortfolioPerformanceDto(key, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);

        if (accountPerformance != null) {
            portfolioPerfDto = new PortfolioPerformanceDto(key, performanceBeforeFee, twrrGrossPct, performanceAfterFee,
                    performancePct, getPerformanceSinceInception(key, serviceErrors, accountKey), incomeRtn, capitalGrowth,
                    groupAndSortInvestmentPerformances(constructPerformanceDtos(accountKey, serviceErrors, accountPerformance)));
        }

        return portfolioPerfDto;

    }

    private List<PerformanceDto> groupAndSortInvestmentPerformances(List<PerformanceDto> investmentDtos) {
        final List<PerformanceDto> investments = new ArrayList<>();
        if (!(investmentDtos.isEmpty())) {
            groupDirectInvestments(investmentDtos, investments);

            groupMpInvestments(investmentDtos, investments);
        }

        return investments;

    }

    private void groupMpInvestments(List<PerformanceDto> investmentDtos, final List<PerformanceDto> investments) {
        final List<PerformanceDto> mpInvestments = Lambda.select(investmentDtos, Lambda.having(
                Lambda.on(PerformanceDto.class).getContainerType(), Matchers.equalTo(ContainerType.MANAGED_PORTFOLIO.getCode())));

        if (!(mpInvestments.isEmpty())) {
            Collections.sort(mpInvestments);
            investments.addAll(mpInvestments);
        }
    }

    private void groupDirectInvestments(List<PerformanceDto> investmentDtos, final List<PerformanceDto> investments) {
        List<PerformanceDto> directInvestments = Lambda.select(investmentDtos,

        Lambda.having(Lambda.on(PerformanceDto.class).getContainerType(), Matchers.equalTo(ContainerType.DIRECT.getCode())));

        sortDirectInvestments(investments, directInvestments);
    }

    private void sortDirectInvestments(final List<PerformanceDto> investments, List<PerformanceDto> directInvestments) {
        if (!(directInvestments.isEmpty())) {
            Group<PerformanceDto> groupDirectInvestmentsByAssetGroup = Lambda.group(directInvestments,
                    Lambda.by(Lambda.on(PerformanceDto.class).getAssetType()));
            if (groupDirectInvestmentsByAssetGroup != null) {
                final List<PerformanceDto> cashInvestment = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.CASH.getDisplayName());
                if (!(cashInvestment.isEmpty())) {
                    investments.addAll(cashInvestment);
                }

                final List<PerformanceDto> tdInvestments = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.TERM_DEPOSIT.getDisplayName());

                if (!(tdInvestments.isEmpty())) {
                    Collections.sort(tdInvestments);
                    investments.addAll(tdInvestments);
                }

                final List<PerformanceDto> lsInvestments = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.SHARE.getDisplayName());
                lsInvestments.addAll(groupDirectInvestmentsByAssetGroup.find(AssetType.OPTION.getDisplayName()));
                lsInvestments.addAll(groupDirectInvestmentsByAssetGroup.find(AssetType.BOND.getDisplayName()));

                if (!(lsInvestments.isEmpty())) {
                    Collections.sort(lsInvestments);
                    investments.addAll(lsInvestments);
                }

                final List<PerformanceDto> mfInvestments = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.MANAGED_FUND.getDisplayName());

                if (!(mfInvestments.isEmpty())) {
                    Collections.sort(mfInvestments);
                    investments.addAll(mfInvestments);
                }
            }

        }
    }

    private List<PerformanceDto> constructPerformanceDtos(AccountKey accountKey, ServiceErrors serviceErrors,
            PortfolioPerformanceOverallImpl accountPerformance) {
        PerformanceDto performanceDto;
        List<PerformanceDto> investmentDtos = new ArrayList<>();
        if (accountPerformance != null && accountPerformance.getInvestmentPerformances() != null) {
            for (AssetPerformance assetPerformance : accountPerformance.getInvestmentPerformances()) {
                if (assetPerformance instanceof ManagedPortfolioPerformanceImpl) {
                    performanceDto = getManagedPortfolioPerformanceDto(assetPerformance);

                } else if (AssetType.TERM_DEPOSIT.equals(assetPerformance.getAssetType())) {
                    performanceDto = getTermDepositPerformanceDto(accountKey, assetPerformance, serviceErrors);
                } else {
                    performanceDto = getPerformanceDto(assetPerformance);
                }
                investmentDtos.add(performanceDto);
            }
        }
        return investmentDtos;
    }

    private BigDecimal getPerformanceSinceInception(DateRangeAccountKey key, ServiceErrors serviceErrors,
            com.bt.nextgen.service.integration.account.AccountKey accountKey) {
        BigDecimal incepPerformanceValue;
        BigDecimal roundedIncepPerformanceValue = null;
        PeriodicPerformance performanceSinceInception = accountPerformanceService
                .loadAccountPerformanceSummarySinceInception(accountKey, null, key.getEndDate(), serviceErrors);
        if (performanceSinceInception != null && performanceSinceInception.getPerformanceData() != null) {
            incepPerformanceValue = performanceSinceInception.getPerformanceData().getPerformance();
            if (incepPerformanceValue != null) {
                roundedIncepPerformanceValue = incepPerformanceValue.divide(BigDecimal.valueOf(100));
            }
        }
        return roundedIncepPerformanceValue;
    }

    protected PerformanceDto getPerformanceDto(AssetPerformance assetPerformance) {
        BigDecimal capitalReturnPct = BigDecimal.ZERO;
        BigDecimal incomeReturnPct = BigDecimal.ZERO;

        String assetCode = assetPerformance.getCode() == null ? Constants.EMPTY_STRING : assetPerformance.getCode();

        if (assetPerformance.getIncomeReturn() != null) {
            incomeReturnPct = assetPerformance.getIncomeReturn().divide(BigDecimal.valueOf(100));
        }
        if (assetPerformance.getCapitalReturn() != null) {
            capitalReturnPct = assetPerformance.getCapitalReturn().divide(BigDecimal.valueOf(100));
        }

        PerformanceDto performanceDto = new PerformanceDto(assetPerformance.getName(), assetCode,
                assetPerformance.getOpeningBalance(), assetPerformance.getClosingBalance(), assetPerformance.getPurchases(),
                assetPerformance.getSales(), assetPerformance.getMarketMovement(), assetPerformance.getIncome(),
                assetPerformance.getPerformancePercent().divide(BigDecimal.valueOf(100)), assetPerformance.getPerformanceDollar(),
                assetPerformance.getPeriodOfDays(), null, null, incomeReturnPct, capitalReturnPct,
                assetPerformance.getContainerType().getCode(), assetPerformance.getAssetType().getDisplayName());

        // If we have a reference asset, use these details instead
        Asset refAsset = assetPerformance.getReferenceAsset();

        if (refAsset != null && refAsset.getAssetType() != null) {
            performanceDto.setReferenceAssetType(refAsset.getAssetType().getDisplayName());
            performanceDto.setReferenceAssetCode(refAsset.getAssetCode());

            // Case of MF prepayment
            if (assetPerformance.getAsset().isPrepayment()) {
                performanceDto.setAssetType(refAsset.getAssetType().getDisplayName());
                performanceDto.setAssetCode(refAsset.getAssetCode());
            }
        }
        // Options should be treated as shares
        if (assetPerformance.getAssetType() == AssetType.OPTION || assetPerformance.getAssetType() == AssetType.BOND) {
            performanceDto.setAssetType(AssetType.SHARE.getDisplayName());
        }
        return performanceDto;
    }

    protected PerformanceDto getTermDepositPerformanceDto(AccountKey accountKey, AssetPerformance assetPerformance,
            ServiceErrors serviceErrors) {
        if (assetPerformance == null) {
            return null;
        }

        BigDecimal capitalReturnPct = BigDecimal.ZERO;
        BigDecimal incomeReturnPct = BigDecimal.ZERO;
        String assetCode = assetPerformance.getCode() == null ? Constants.EMPTY_STRING : assetPerformance.getCode();
        if (assetPerformance.getIncomeReturn() != null) {
            incomeReturnPct = assetPerformance.getIncomeReturn().divide(BigDecimal.valueOf(100));
        }
        if (assetPerformance.getCapitalReturn() != null) {
            capitalReturnPct = assetPerformance.getCapitalReturn().divide(BigDecimal.valueOf(100));
        }
        DateTime maturityDate = ((TermDepositAsset) assetPerformance.getAsset()).getMaturityDate();

        TermDepositPresentation termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(accountKey,
                assetPerformance.getAsset().getAssetId(), serviceErrors);

        PerformanceDto performanceDto = new TermDepositPerformanceDto(termDepositPresentation.getBrandName(), assetCode,
                assetPerformance.getOpeningBalance(), assetPerformance.getClosingBalance(), assetPerformance.getPurchases(),
                assetPerformance.getSales(), assetPerformance.getMarketMovement(), assetPerformance.getIncome(),
                assetPerformance.getPerformancePercent().divide(BigDecimal.valueOf(100)), assetPerformance.getPerformanceDollar(),
                assetPerformance.getPeriodOfDays(), incomeReturnPct, capitalReturnPct,
                assetPerformance.getContainerType().getCode(), assetPerformance.getAssetType().getDisplayName(), maturityDate,
                termDepositPresentation.getBrandClass(), termDepositPresentation.getTerm(),
                termDepositPresentation.getPaymentFrequency());

        return performanceDto;
    }

    protected ManagedPortfolioPerformanceDto getManagedPortfolioPerformanceDto(AssetPerformance assetPerformance) {
        BigDecimal capitalReturnPct = BigDecimal.ZERO;
        BigDecimal incomeReturnPct = BigDecimal.ZERO;
        ManagedPortfolioPerformanceImpl managedPortfolioPerformance = (ManagedPortfolioPerformanceImpl) assetPerformance;
        List<PerformanceDto> invDtoList = null;
        if (assetPerformance != null) {
            invDtoList = new ArrayList<>();
            List<PerformanceDto> cashList = new ArrayList<>();
            List<PerformanceDto> shareList = new ArrayList<>();
            List<PerformanceDto> fundList = new ArrayList<>();
            populateAssetsList(managedPortfolioPerformance, cashList, shareList, fundList);

            invDtoList.addAll(cashList);
            invDtoList.addAll(shareList);
            invDtoList.addAll(fundList);
            // Sort on Asset type ordering, if both assets are of same type,
            // then sort them alphabetically
            sortOnAssetType(invDtoList);

        }

        String assetCode = assetPerformance.getCode() == null ? Constants.EMPTY_STRING : assetPerformance.getCode();
        String invstId = assetPerformance.getId() == null ? Constants.EMPTY_STRING
                : EncodedString.fromPlainText(assetPerformance.getId()).toString();

        if (assetPerformance.getIncomeReturn() != null) {
            incomeReturnPct = assetPerformance.getIncomeReturn().divide(BigDecimal.valueOf(100));
        }
        if (assetPerformance.getCapitalReturn() != null) {
            capitalReturnPct = assetPerformance.getCapitalReturn().divide(BigDecimal.valueOf(100));
        }

        ManagedPortfolioPerformanceDto managedPortfolioDto = new ManagedPortfolioPerformanceDto(assetPerformance.getName(),
                invstId, assetCode, assetPerformance.getOpeningBalance(), assetPerformance.getClosingBalance(),
                assetPerformance.getPurchases(), assetPerformance.getSales(), assetPerformance.getMarketMovement(),
                assetPerformance.getIncome(), assetPerformance.getPerformancePercent().divide(BigDecimal.valueOf(100)),
                assetPerformance.getPerformanceDollar(), assetPerformance.getPeriodOfDays(), null, null, incomeReturnPct,
                capitalReturnPct, invDtoList, assetPerformance.getContainerType().getCode());

        return managedPortfolioDto;
    }

    private void populateAssetsList(ManagedPortfolioPerformanceImpl managedPortfolioPerformance, List<PerformanceDto> cashList,
            List<PerformanceDto> shareList, List<PerformanceDto> fundList) {
        for (AssetPerformance performance : managedPortfolioPerformance.getAssetPerformances()) {
            PerformanceDto performanceDto = getPerformanceDto(performance);
            if (performanceDto.getAssetType().equals(AssetType.CASH.getDisplayName())) {
                cashList.add(performanceDto);
            } else if (performanceDto.getAssetType().equals(AssetType.SHARE.getDisplayName())) {
                shareList.add(performanceDto);
            } else if (performanceDto.getAssetType().equals(AssetType.MANAGED_FUND.getDisplayName())) {
                fundList.add(performanceDto);
            }
        }
    }

    private void sortOnAssetType(List<PerformanceDto> invDtoList) {
        Collections.sort(invDtoList, new Comparator<PerformanceDto>() {
            @Override
            public int compare(PerformanceDto o1, PerformanceDto o2) {
                return compareAsset(getName(o1), getName(o2), getAssetType(o1), getAssetType(o2));

            }

            private String getName(PerformanceDto o) {
                return o.getName() == null ? "" : o.getName().toLowerCase();
            }

            private AssetType getAssetType(PerformanceDto o) {
                AssetType assetType = AssetType.forDisplay(o.getAssetType());

                return assetType;
            }

            private int compareAsset(String name1, String name2, AssetType assetType1, AssetType assetType2) {
                if (assetType1.getSortOrder() < (assetType2.getSortOrder())) {
                    return -1;
                } else if (assetType1.getSortOrder() > assetType2.getSortOrder()) {
                    return 1;
                } else
                    return name1.compareTo(name2);

            }
        });
    }

}
