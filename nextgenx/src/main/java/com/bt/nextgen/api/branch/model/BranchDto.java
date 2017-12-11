package com.bt.nextgen.api.branch.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class BranchDto extends BaseDto implements KeyedDto<BranchKey> {

    private BranchKey key;
    private String financialInstitutionName;

    public BranchDto(BranchKey key, String financialInstitutionName) {
        this.key = key;
        this.financialInstitutionName = financialInstitutionName;
    }

    @Override
    public BranchKey getKey() {
        return key;
    }

    public String getFinancialInstitutionName() {
        return financialInstitutionName;
    }
}
