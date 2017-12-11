package com.bt.nextgen.api.income.v2.model;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.integration.income.IncomeType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IncomeValueTotals {

    private final List<InvestmentTypeDto> investmentTypes;

    public IncomeValueTotals(List<IncomeDto> incomeDtos) {
        List<InvestmentTypeDto> investmentTypes = new ArrayList<>();
        for (IncomeDto incomeDto : incomeDtos) {
            if (incomeDto instanceof InvestmentTypeDto) {
                investmentTypes.add((InvestmentTypeDto) incomeDto);
            }
        }
        this.investmentTypes = investmentTypes;
    }

    public BigDecimal getIncomeTotal() {
        List<IncomeValueDto> incomeValues = getIncomeValues(null);
        return incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getAmount();
    }

    public BigDecimal getInterestTotal() {
        List<IncomeValueDto> incomeValues = getIncomeValues(IncomeType.INTEREST);
        return incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getAmount();
    }

    public BigDecimal getDividendTotal() {
        List<IncomeValueDto> incomeValues = getIncomeValues(IncomeType.DIVIDEND);
        return incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getAmount();
    }

    public BigDecimal getFrankedDividendTotal() {
        List<IncomeValueDto> incomeValues = getIncomeValues(IncomeType.DIVIDEND);
        return incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getFrankedDividend();
    }

    public BigDecimal getUnfrankedDividendTotal() {
        List<IncomeValueDto> incomeValues = getIncomeValues(IncomeType.DIVIDEND);
        return incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getUnfrankedDividend();
    }

    public BigDecimal getDistributionTotal() {
        List<IncomeValueDto> incomeValues = getIncomeValues(IncomeType.DISTRIBUTION);
        return incomeValues.isEmpty() ? BigDecimal.ZERO : Lambda.sumFrom(incomeValues).getAmount();
    }

    private List<IncomeValueDto> getIncomeValues(IncomeType incomeType) {
        List<IncomeValueDto> incomeValues = new ArrayList<>();
        for(InvestmentTypeDto investmentType:investmentTypes) {
            incomeValues.addAll(getIncomeValues(incomeType, investmentType.getChildren()));
        }        
        return incomeValues;
    }

    private List<IncomeValueDto> getIncomeValues(IncomeType incomeType, List<IncomeDto> incomes) {
        List<IncomeValueDto> incomeValues = new ArrayList<>();
        for (IncomeDto income : incomes) {
            if (income.getChildren().isEmpty()) {
                IncomeValueDto value = (IncomeValueDto) income;
                if (value.getIncomeType() == incomeType || incomeType == null) {
                    incomeValues.add((IncomeValueDto) income);
                }
            } else {
                incomeValues.addAll(getIncomeValues(incomeType, income.getChildren()));
            }
        }
        return incomeValues;
    }
}
