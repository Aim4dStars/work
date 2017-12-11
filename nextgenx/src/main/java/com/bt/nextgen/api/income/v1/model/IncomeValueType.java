package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class IncomeValueType {
    private final String valueTypeName; // Cash, TD, MF, MP, LS
    private final List<IncomeValueGroup> incomeValueGroups;
    private final Boolean showSubGroup;
    private final BigDecimal incomeTotal;

    public IncomeValueType(String valueTypeName, List<IncomeValueGroup> incomeValueGroups, Boolean showSubGroup,
            BigDecimal incomeTotal) {
        this.valueTypeName = valueTypeName;
        this.incomeValueGroups = incomeValueGroups;
        this.showSubGroup = showSubGroup;
        this.incomeTotal = incomeTotal;
    }

    public String getValueTypeName() {
        return valueTypeName;
    }

    public List<IncomeValueGroup> getIncomeValueGroups() {
        return incomeValueGroups;
    }

    public Boolean getShowSubGroup() {
        return showSubGroup;
    }

    public BigDecimal getIncomeTotal() {
        return incomeTotal;
    }

}
