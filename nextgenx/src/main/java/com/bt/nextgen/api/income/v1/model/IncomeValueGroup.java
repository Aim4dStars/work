package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class IncomeValueGroup {
    private final String groupName; // Div, Dist, Fee Rebates, Cash
    private final List<IncomeValue> incomeValues;
    private final BigDecimal incomeTotal;

    public IncomeValueGroup(String groupName, List<IncomeValue> incomeValues, BigDecimal incomeTotal) {
        this.groupName = groupName;
        this.incomeValues = incomeValues;
        this.incomeTotal = incomeTotal;
    }    
    
    public String getIncomeName() {
        return groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<IncomeValue> getIncomeValues() {
        return incomeValues;
    }

    public BigDecimal getIncomeTotal() {
        return incomeTotal;
    }    
    
}
