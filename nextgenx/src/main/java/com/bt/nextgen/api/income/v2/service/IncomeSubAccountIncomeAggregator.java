package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.AbstractIncomeDto;
import com.bt.nextgen.api.income.v2.model.DistributionIncomeDto;
import com.bt.nextgen.api.income.v2.model.FeeRebateIncomeDto;
import com.bt.nextgen.api.income.v2.model.InterestIncomeDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.income.DividendIncomeImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.IncomeType;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.TermDepositIncome;
import com.bt.nextgen.service.integration.order.OrderType;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.service.wrap.integration.util.WrapAssetUtil.isWrapTermDeposit;

@Component
public class IncomeSubAccountIncomeAggregator {

    @Autowired
    private IncomeDtoFromIncomeValuationBuilder incomeDtoIncomeBuilder;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    protected Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> buildInvestmentMapFromSubAccount(
            List<SubAccountIncomeDetails> incomes, AccountKey accountKey, ServiceErrors serviceErrors) {
        Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> investmentMap =
                new HashMap<AssetType, Map<IncomeType, List<AbstractIncomeDto>>>();
        for (SubAccountIncomeDetails subAccountIncome : incomes) {
            Map<IncomeType, List<AbstractIncomeDto>> incomeMap = getFromInvestmentMap(investmentMap,
                    subAccountIncome.getAssetType());
            for (HoldingIncomeDetails holding : subAccountIncome.getIncomes()) {
                buildIncomeMapFromHolding(incomeMap, holding, accountKey, serviceErrors);
            }

            investmentMap.put(subAccountIncome.getAssetType(), incomeMap);
        }
        return investmentMap;
    }

    private void buildIncomeMapFromHolding(Map<IncomeType, List<AbstractIncomeDto>> incomeMap, HoldingIncomeDetails holding,
                                           AccountKey accountKey, ServiceErrors serviceErrors) {
        for (Income income : holding.getIncomes()) {
            switch (income.getIncomeType()) {
                case CASH:
                    getFromIncomeMap(incomeMap, IncomeType.CASH).add(incomeDtoIncomeBuilder.toCashIncomeDto(holding, income));
                    break;
                case TERM_DEPOSIT:
                    addTermDepositDto(income, incomeMap, holding, accountKey, serviceErrors);
                    break;
                case DIVIDEND:
                    addDividendDto(income, incomeMap, holding);
                    break;
                case DISTRIBUTION:
                    addDistributionDto(income, incomeMap, holding);
                    break;
                case INTEREST:
                    addInterestDto(income, incomeMap, holding);
                    break;
                case FEE_REBATE:
                    addFeeRebateDto(income, incomeMap, holding);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Use cost base as the amount for DRP's
     *
     * @param income the accrued income details
     *
     * @return updated income object
     */
    private void addDividendDto(Income income, Map<IncomeType, List<AbstractIncomeDto>> incomeMap, HoldingIncomeDetails holding) {
        DividendIncome dividendIncome = (DividendIncome) income;

        // DRP must use cost base as the pay amount
        if (dividendIncome.getOrderType() == OrderType.DIVIDEND_REINVEST && dividendIncome.getCostBase() != null) {
            ((DividendIncomeImpl) dividendIncome).setAmount(dividendIncome.getCostBase());
        }

        getFromIncomeMap(incomeMap, IncomeType.DIVIDEND).add(incomeDtoIncomeBuilder.toDividendIncomeDto(holding, dividendIncome));
    }

    private void addTermDepositDto(Income income, Map<IncomeType, List<AbstractIncomeDto>> incomeMap, HoldingIncomeDetails holding,
                                   AccountKey accountKey, ServiceErrors serviceErrors) {
        TermDepositPresentation termDepositPresentation = new TermDepositPresentation();
        final Boolean isWrapTD = isWrapTermDeposit(holding.getAsset().getAssetId());
        if (isWrapTD) {
            termDepositPresentation.setBrandName(((TermDepositIncome) income).getDescription());
        }
        else {
            termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(accountKey,
                    holding.getAsset().getAssetId(), serviceErrors);
        }

        getFromIncomeMap(incomeMap, IncomeType.TERM_DEPOSIT).add(
                incomeDtoIncomeBuilder.toTermDepositIncomeDto(holding, income, termDepositPresentation,
                        isWrapTD ? Boolean.TRUE : null));

    }

    private void addDistributionDto(Income income, Map<IncomeType, List<AbstractIncomeDto>> incomeMap, HoldingIncomeDetails holding) {
        DistributionIncomeDto distributionIncomeDto = incomeDtoIncomeBuilder.toDistributionIncomeDto(holding, income);
        getFromIncomeMap(incomeMap, IncomeType.DISTRIBUTION).add(distributionIncomeDto);
    }

    private void addFeeRebateDto(Income income, Map<IncomeType, List<AbstractIncomeDto>> incomeMap, HoldingIncomeDetails holding) {
        FeeRebateIncomeDto feeRebateDto = incomeDtoIncomeBuilder.toFeeRebateIncomeDto(holding, income);
        getFromIncomeMap(incomeMap, IncomeType.FEE_REBATE).add(feeRebateDto);
    }

    private void addInterestDto(Income income, Map<IncomeType, List<AbstractIncomeDto>> incomeMap, HoldingIncomeDetails holding) {
        InterestIncomeDto distributionIncomeDto = incomeDtoIncomeBuilder.toInterestIncomeDto(holding, income);
        getFromIncomeMap(incomeMap, IncomeType.INTEREST).add(distributionIncomeDto);
    }

    private Map<IncomeType, List<AbstractIncomeDto>> getFromInvestmentMap(
            Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> investmentMap, AssetType assetType) {
        if (investmentMap.get(assetType) == null) {
            investmentMap.put(assetType, new HashMap<IncomeType, List<AbstractIncomeDto>>());
        }
        return investmentMap.get(assetType);
    }

    private List<AbstractIncomeDto> getFromIncomeMap(Map<IncomeType, List<AbstractIncomeDto>> incomeMap, IncomeType incomeType) {
        if (incomeMap.get(incomeType) == null) {
            incomeMap.put(incomeType, new ArrayList<AbstractIncomeDto>());
        }
        return incomeMap.get(incomeType);
    }
}
