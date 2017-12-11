package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.SubAccountKey;

public class IncomePreferenceDto extends BaseDto implements KeyedDto<SubAccountKey> {

    private SubAccountKey subAccountKey;

    private IncomePreference incomePreference;

    public IncomePreferenceDto(SubAccountKey subAccountKey, String incomePreference) {
        super();
        this.subAccountKey = subAccountKey;
        this.incomePreference = IncomePreference.valueOf(incomePreference);

    }

    @Override
    public SubAccountKey getKey() {
        return subAccountKey;
    }

    public SubAccountKey getSubAccountKey() {
        return subAccountKey;
    }

    public IncomePreference getIncomePreference() {
        return incomePreference;
    }
}
