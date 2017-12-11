package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

public class DashboardClientApplicationDetailsDto extends BaseDto implements KeyedDto<ClientApplicationKey> {
    private ClientApplicationKey key;
    private String accountName;
    private String accountType;
    private DateTime lastModifiedDate;


    @Override
    public ClientApplicationKey getKey() {
        return key;
    }

    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
