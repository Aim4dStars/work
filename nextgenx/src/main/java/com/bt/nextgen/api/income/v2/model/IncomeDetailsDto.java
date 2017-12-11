package com.bt.nextgen.api.income.v2.model;

import java.util.ArrayList;
import java.util.List;

public class IncomeDetailsDto {

    private List<InvestmentTypeDto> incomeValueTypes;

    public List<InvestmentTypeDto> getIncomeValueTypes() {
        if(incomeValueTypes == null){
            return new ArrayList<InvestmentTypeDto>();
        }
        return incomeValueTypes;
    }

    public void setIncomeValueTypes(List<InvestmentTypeDto> incomeValueTypes) {
        this.incomeValueTypes = incomeValueTypes;
    }
    
    
    
}
