package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by l069679 on 8/12/2016.
 */
public class SafiSMSModel extends BaseDto {

    private String accountID;
    private String bsb;
    private String transactionID;
    private String smsCode;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getBsb() {
        return bsb;
    }

    public void setBsb(String bsb) {
        this.bsb = bsb;
    }

    @Override
    @JsonIgnore
    public String getType() {
        return "SafiSMSModel";
    }
}
