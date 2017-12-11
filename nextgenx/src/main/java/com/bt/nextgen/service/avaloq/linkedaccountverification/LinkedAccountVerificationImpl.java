package com.bt.nextgen.service.avaloq.linkedaccountverification;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.bt.nextgen.service.integration.account.LinkedAccountVerification;

import java.math.BigDecimal;

/**
 * Created by l078480 on 18/08/2017.
 */
public class LinkedAccountVerificationImpl implements LinkedAccountVerification {

    private String accountNumber;
    private String bsb;
    private AccountKey accountKey;
    private String verificationCode;
    private String verificationAction;
    private BigDecimal modificationIdentifier;
    private AccountType accountType;


    @Override
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }



    @Override
    public BigDecimal getModificationIdentifier() {
        return modificationIdentifier;
    }

    @Override
    public void setModificationIdentifier(BigDecimal modificationIdentifier) {
        this.modificationIdentifier = modificationIdentifier;
    }



    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    @Override
    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }


    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String getBsb() {
        return bsb;
    }

    public void setBsb(String bsb) {
        this.bsb = bsb;
    }

    @Override
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @Override
    public String getVerificationAction() {
        return verificationAction;
    }

    public void setVerificationAction(String verificationAction) {
        this.verificationAction = verificationAction;
    }




}
