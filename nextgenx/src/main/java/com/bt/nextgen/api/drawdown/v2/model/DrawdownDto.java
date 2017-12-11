package com.bt.nextgen.api.drawdown.v2.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class DrawdownDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;
    private String drawdownType;
    private List<DomainApiErrorDto> warnings;

    public DrawdownDto() {
        super();
    }

    public DrawdownDto(AccountKey accountKey, String drawdownType) {
        this.key = accountKey;
        this.drawdownType = drawdownType;
    }

    public String getDrawdownType() {
        return drawdownType;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public void setDrawdownType(String drawdownType) {
        this.drawdownType = drawdownType;
    }

}
