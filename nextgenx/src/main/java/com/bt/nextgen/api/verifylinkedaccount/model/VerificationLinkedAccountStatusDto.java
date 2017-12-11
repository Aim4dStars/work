package com.bt.nextgen.api.verifylinkedaccount.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.payeedetails.LinkedAccountStatus;

/**
 * Created by l078480 on 22/08/2017.
 */
public class VerificationLinkedAccountStatusDto extends BaseDto implements KeyedDto<AccountKey>  {
    private LinkedAccountStatusDto linkedAccountStatus;

    private AccountKey key;

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }



    public LinkedAccountStatusDto getLinkedAccountStatus() {
        return linkedAccountStatus;
    }

    public void setLinkedAccountStatus(LinkedAccountStatusDto linkedAccountStatus) {
        this.linkedAccountStatus = linkedAccountStatus;
    }
}


