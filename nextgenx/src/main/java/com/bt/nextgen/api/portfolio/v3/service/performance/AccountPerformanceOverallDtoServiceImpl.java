package com.bt.nextgen.api.portfolio.v3.service.performance;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceOverallDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.ManagedPortfolioPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PeriodPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.TermDepositPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceOverall;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("AccountPerformanceOverallDtoServiceV3")
@Profile({"default","WrapOnThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
public class AccountPerformanceOverallDtoServiceImpl implements AccountPerformanceOverallDtoService {

    @Autowired
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    private static final String EMPTY_STRING = "";

    @Override
    public AccountPerformanceOverallDto find(DateRangeAccountKey key, ServiceErrors serviceErrors) {

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        AccountPerformanceOverall accountPerformance = accountPerformanceService.loadAccountOverallPerformance(accountKey,
                key.getStartDate(), key.getEndDate(), serviceErrors);

        if (accountPerformance != null) {
            List<PeriodPerformanceDto> dtoList = constructPerformanceDtos(accountKey, serviceErrors, accountPerformance);
            return new AccountPerformanceOverallDto(key, groupAndSortInvestmentPerformances(dtoList));
        }
        return new AccountPerformanceOverallDto(key, null);
    }

    private List<PeriodPerformanceDto> groupAndSortInvestmentPerformances(List<PeriodPerformanceDto> investmentDtos) {
        final List<PeriodPerformanceDto> investments = new ArrayList<>();
        if (!(investmentDtos.isEmpty())) {
            groupDirectInvestments(investmentDtos, investments);
            groupMpInvestments(investmentDtos, investments);
        }

        return investments;
    }

    private void groupMpInvestments(List<PeriodPerformanceDto> investmentDtos, final List<PeriodPerformanceDto> investments) {
        final List<PeriodPerformanceDto> mpInvestments = Lambda.select(investmentDtos,
                Lambda.having(Lambda.on(PeriodPerformanceDto.class).getContainerType(),
                        Matchers.equalTo(ContainerType.MANAGED_PORTFOLIO.getCode())));

        if (!(mpInvestments.isEmpty())) {
            Collections.sort(mpInvestments);
            investments.addAll(mpInvestments);
        }
    }

    private void groupDirectInvestments(List<PeriodPerformanceDto> investmentDtos, final List<PeriodPerformanceDto> investments) {
        List<PeriodPerformanceDto> directInvestments = Lambda.select(investmentDtos, Lambda.having(
                Lambda.on(PeriodPerformanceDto.class).getContainerType(), Matchers.equalTo(ContainerType.DIRECT.getCode())));

        sortDirectInvestments(investments, directInvestments);
    }

    private void sortDirectInvestments(final List<PeriodPerformanceDto> investments,
            List<PeriodPerformanceDto> directInvestments) {
        if (!(directInvestments.isEmpty())) {
            Group<PeriodPerformanceDto> groupDirectInvestmentsByAssetGroup = Lambda.group(directInvestments,
                    Lambda.by(Lambda.on(PeriodPerformanceDto.class).getAssetType()));
            if (groupDirectInvestmentsByAssetGroup != null) {
                final List<PeriodPerformanceDto> cashInvestment = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.CASH.getDisplayName());
                if (!(cashInvestment.isEmpty())) {
                    investments.addAll(cashInvestment);
                }

                final List<PeriodPerformanceDto> tdInvestments = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.TERM_DEPOSIT.getDisplayName());

                if (!(tdInvestments.isEmpty())) {
                    Collections.sort(tdInvestments);
                    investments.addAll(tdInvestments);
                }

                final List<PeriodPerformanceDto> lsInvestments = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.SHARE.getDisplayName());
                lsInvestments.addAll(groupDirectInvestmentsByAssetGroup.find(AssetType.OPTION.getDisplayName()));
                lsInvestments.addAll(groupDirectInvestmentsByAssetGroup.find(AssetType.BOND.getDisplayName()));

                if (!(lsInvestments.isEmpty())) {
                    Collections.sort(lsInvestments);
                    investments.addAll(lsInvestments);
                }

                final List<PeriodPerformanceDto> mfInvestments = groupDirectInvestmentsByAssetGroup
                        .find(AssetType.MANAGED_FUND.getDisplayName());

                if (!(mfInvestments.isEmpty())) {
                    Collections.sort(mfInvestments);
                    investments.addAll(mfInvestments);
                }
            }
        }
    }

    private List<PeriodPerformanceDto> constructPerformanceDtos(AccountKey accountKey, ServiceErrors serviceErrors,
            AccountPerformanceOverall accountPerformance) {
        PeriodPerformanceDto performanceDto;
        List<PeriodPerformanceDto> investmentDtos = new ArrayList<>();
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

    protected PeriodPerformanceDto getPerformanceDto(AssetPerformance assetPerformance) {

        if (assetPerformance == null) {
            return null;
        }

        PeriodPerformanceDto performanceDto = new PeriodPerformanceDto(assetPerformance, assetPerformance.getName(),
                assetPerformance.getAssetType());

        // If we have a reference asset, use these details instead
        Asset refAsset = assetPerformance.getReferenceAsset();

        if (refAsset != null && refAsset.getAssetType() != null) {
            performanceDto.setReferenceAssetType(refAsset.getAssetType().getDisplayName());
            performanceDto.setReferenceAssetCode(refAsset.getAssetCode());

            // Case of MF prepayment
            if (assetPerformance.getAsset().isPrepayment()) {
                // Filter prepayments
                if (filterPrepayment(assetPerformance)) {
                    return null;
                }
                performanceDto.setAssetType(refAsset.getAssetType());
                performanceDto.setAssetCode(refAsset.getAssetCode());
            }
        }
        // Options should be treated as shares
        if (assetPerformance.getAssetType() == AssetType.OPTION || assetPerformance.getAssetType() == AssetType.BOND) {
            performanceDto.setAssetType(AssetType.SHARE);
        }
        return performanceDto;
    }

    private boolean filterPrepayment(AssetPerformance assetPerformance) {
        return (assetPerformance.getOpeningBalance().compareTo(BigDecimal.ZERO) == 0
                && assetPerformance.getClosingBalance().compareTo(BigDecimal.ZERO) == 0);
    }

    protected PeriodPerformanceDto getTermDepositPerformanceDto(AccountKey accountKey, AssetPerformance assetPerformance,
            ServiceErrors serviceErrors) {

        if (assetPerformance == null) {
            return null;
        }

        DateTime maturityDate = ((TermDepositAsset) assetPerformance.getAsset()).getMaturityDate();

        return new TermDepositPerformanceDto(assetPerformance, termDepositPresentationService
                .getTermDepositPresentation(accountKey, assetPerformance.getAsset().getAssetId(), serviceErrors), maturityDate);
    }

    protected ManagedPortfolioPerformanceDto getManagedPortfolioPerformanceDto(AssetPerformance assetPerformance) {

        if (assetPerformance == null) {
            return null;
        }

        String invstId = assetPerformance.getId() == null ? EMPTY_STRING
                : EncodedString.fromPlainText(assetPerformance.getId()).toString();

        List<PeriodPerformanceDto> assetsList = populateAssetsList((ManagedPortfolioPerformanceImpl) assetPerformance);
        Collections.sort(assetsList, new PerformanceComparator());

        ManagedPortfolioPerformanceDto managedPortfolioDto = new ManagedPortfolioPerformanceDto(assetPerformance, invstId,
                assetsList);

        managedPortfolioDto.setAssetType(assetPerformance.getAssetType());

        return managedPortfolioDto;
    }

    private List<PeriodPerformanceDto> populateAssetsList(ManagedPortfolioPerformanceImpl managedPortfolioPerformance) {

        List<PeriodPerformanceDto> assetsList = new ArrayList<>();
        List<PeriodPerformanceDto> cashList = new ArrayList<>();
        List<PeriodPerformanceDto> shareList = new ArrayList<>();
        List<PeriodPerformanceDto> fundList = new ArrayList<>();

        for (AssetPerformance performance : managedPortfolioPerformance.getAssetPerformances()) {
            // Filter prepayments
            if (!(performance.getAsset().isPrepayment() && filterPrepayment(performance))) {
                PeriodPerformanceDto performanceDto = getPerformanceDto(performance);
                if (performanceDto.getAssetType().equals(AssetType.CASH.getDisplayName())) {
                    cashList.add(performanceDto);
                } else if (performanceDto.getAssetType().equals(AssetType.SHARE.getDisplayName())) {
                    shareList.add(performanceDto);
                } else if (performanceDto.getAssetType().equals(AssetType.MANAGED_FUND.getDisplayName())) {
                    fundList.add(performanceDto);
                }
            }
        }

        assetsList.addAll(cashList);
        assetsList.addAll(shareList);
        assetsList.addAll(fundList);

        return assetsList;
    }
}
