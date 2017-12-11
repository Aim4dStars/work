package com.bt.nextgen.api.income.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.income.v2.model.AbstractIncomeDto;
import com.bt.nextgen.api.income.v2.model.CashIncomeDto;
import com.bt.nextgen.api.income.v2.model.DistributionIncomeDto;
import com.bt.nextgen.api.income.v2.model.DividendIncomeDto;
import com.bt.nextgen.api.income.v2.model.FeeRebateIncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeDto;
import com.bt.nextgen.api.income.v2.model.IncomeValueDto;
import com.bt.nextgen.api.income.v2.model.InterestIncomeDto;
import com.bt.nextgen.api.income.v2.model.InvestmentIncomeTypeDto;
import com.bt.nextgen.api.income.v2.model.InvestmentTypeDto;
import com.bt.nextgen.api.income.v2.model.TermDepositIncomeDto;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.income.IncomeType;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class IncomeValueDtoBuilder {

    protected List<IncomeDto> buildIncomeTypesDtoList(Map<AssetType, Map<IncomeType, List<AbstractIncomeDto>>> investmentMap) {
        List<IncomeDto> investmentTypes = new ArrayList<IncomeDto>();

        List<AssetType> assetTypeSortedList = new ArrayList<AssetType>(investmentMap.keySet());
        sortOnAssetType(assetTypeSortedList);

        for (AssetType investmentTypeIterKey : assetTypeSortedList) {
            Map<IncomeType, List<AbstractIncomeDto>> investmentType = investmentMap.get(investmentTypeIterKey);

            List<IncomeDto> investmentIncomeTypes = new ArrayList<IncomeDto>();

            List<IncomeType> incomeTypeSortedList = new ArrayList<IncomeType>(investmentType.keySet());
            sortOnIncomeType(incomeTypeSortedList);

            for (IncomeType incomeTypeIterKey : incomeTypeSortedList) {

                List<AbstractIncomeDto> incomeDtoList = investmentType.get(incomeTypeIterKey);

                sortOnPaymentDate(incomeDtoList);

                List<IncomeDto> incomeValues = toIncomeValuesDto(incomeDtoList, incomeTypeIterKey);

                investmentIncomeTypes.add(new InvestmentIncomeTypeDto(incomeTypeIterKey, incomeValues,
                        incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getAmount()));

            }
            investmentTypes.add(new InvestmentTypeDto(investmentTypeIterKey, investmentIncomeTypes, investmentIncomeTypes
                    .isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(investmentIncomeTypes).getAmount()));
        }

        return investmentTypes;

    }

    private List<IncomeDto> toIncomeValuesDto(List<AbstractIncomeDto> incomeDtoList, IncomeType incomeType) {

        List<IncomeDto> incomeValues = new ArrayList<IncomeDto>();

        for (AbstractIncomeDto incomeDto : incomeDtoList) {
            switch (incomeType) {
                case CASH:
                    incomeValues.add(new IncomeValueDto((CashIncomeDto) incomeDto));
                    break;
                case TERM_DEPOSIT:
                    incomeValues.add(new IncomeValueDto((TermDepositIncomeDto) incomeDto));
                    break;
                case DIVIDEND:
                    incomeValues.add(new IncomeValueDto((DividendIncomeDto) incomeDto));
                    break;
                case DISTRIBUTION:
                    incomeValues.add(new IncomeValueDto((DistributionIncomeDto) incomeDto));
                    break;
                case INTEREST:
                    incomeValues.add(new IncomeValueDto((InterestIncomeDto) incomeDto));
                    break;
                case FEE_REBATE:
                    incomeValues.add(new IncomeValueDto((FeeRebateIncomeDto) incomeDto));
                    break;
                default:
                    break;
            }
        }
        return incomeValues;
    }

    private void sortOnPaymentDate(List<AbstractIncomeDto> incomeDtoList) {
        Collections.sort(incomeDtoList, new Comparator<AbstractIncomeDto>() {
            public int compare(AbstractIncomeDto o1, AbstractIncomeDto o2) {
                DateTime o1Date = o1.getPaymentDate();
                DateTime o2Date = o2.getPaymentDate();

                if (o1Date == null) {
                    return o2Date == null ? 0 : 1;
                }
                return o2Date == null ? -1 : o2Date.compareTo(o1Date);
            }
        });
    }

    private void sortOnAssetType(List<AssetType> assetTypeList) {
        Collections.sort(assetTypeList, new Comparator<AssetType>() {
            @Override
            public int compare(AssetType assetType1, AssetType assetType2) {
                if (assetType1.getSortOrder() < (assetType2.getSortOrder())) {
                    return -1;
                } else if (assetType1.getSortOrder() > assetType2.getSortOrder()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    private void sortOnIncomeType(List<IncomeType> incomeTypeList) {
        Collections.sort(incomeTypeList, new Comparator<IncomeType>() {
            @Override
            public int compare(IncomeType incomeType1, IncomeType incomeType2) {
                if (incomeType1.getSortOrder() < (incomeType2.getSortOrder())) {
                    return -1;
                } else if (incomeType1.getSortOrder() > incomeType2.getSortOrder()) {
                    return 1;
                }
                return 0;
            }
        });
    }

}
