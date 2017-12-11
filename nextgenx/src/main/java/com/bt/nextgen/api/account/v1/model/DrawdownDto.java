package com.bt.nextgen.api.account.v1.model;

import java.util.List;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * @deprecated Use V2
 */
@Deprecated
public class DrawdownDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey accountKey;
    private String drawdownType;
    private List<DomainApiErrorDto> warnings;

    public DrawdownDto(AccountKey accountKey, String drawdownType) {
        this.accountKey = accountKey;
        this.drawdownType = drawdownType;
    }

    public String getDrawdownType() {
        return drawdownType;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

}
