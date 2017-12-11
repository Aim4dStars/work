package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.integration.movemoney.LinkedAccountDetails;

public class LinkedAccountDetailsImpl implements LinkedAccountDetails {
    private String account;
    private String bsb;
    private String payeeName;
    private String nickName;
    private boolean primary;

    @Override
    public boolean isPrimary() {
        return primary;
    }

    @Override
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String getAccountNumber() {
        return account;
    }

    @Override
    public String getName() {
        return payeeName;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public String getBsb() {
        return bsb;
    }

}
