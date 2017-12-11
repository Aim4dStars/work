package com.bt.nextgen.api.verifylinkedaccount.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Created by l078480 on 22/08/2017.
 */
public class LinkedAccountDetailsDto extends BaseDto implements KeyedDto<AccountKey> {

    private String accountNumber;
    private String bsb;
    private String verificationCode;
    private AccountKey key;
    private String verificationAction;
    private LinkedAccountStatusDto linkedAccountStatus;

    public LinkedAccountStatusDto getLinkedAccountStatus() {
        return linkedAccountStatus;
    }

    public void setLinkedAccountStatus(LinkedAccountStatusDto linkedAccountStatus) {
        this.linkedAccountStatus = linkedAccountStatus;
    }



    public String getVerificationAction() {
        return verificationAction;
    }

    public void setVerificationAction(String verificationAction) {
        this.verificationAction = verificationAction;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }




    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBsb() {
        return bsb;
    }

    public void setBsb(String bsb) {
        this.bsb = bsb;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
