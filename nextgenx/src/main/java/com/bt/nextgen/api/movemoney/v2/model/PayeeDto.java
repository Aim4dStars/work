package com.bt.nextgen.api.movemoney.v2.model;

import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class PayeeDto {

    @JsonView(JsonViews.Write.class)
    private String accountId;

    @JsonView(JsonViews.Write.class)
    private String accountName;

    @JsonView(JsonViews.Write.class)
    private String nickname;

    @JsonView(JsonViews.Write.class)
    private String code;

    @JsonView(JsonViews.Write.class)
    private String payeeType;

    @JsonView(JsonViews.Write.class)
    private boolean primary;

    private String limit;
    private String crn;
    private boolean fixedCRN;
    private String type;
    private String saveToList;
    private String smsCode;
    private String devieNumber;
    private String accountKey;
    private LinkedAccountStatusDto linkedAccountStatus;
    private String manuallyVerifiedFlag;

    public LinkedAccountStatusDto getLinkedAccountStatus() {
        return linkedAccountStatus;
    }

    public void setLinkedAccountStatus(LinkedAccountStatusDto linkedAccountStatus) {
        this.linkedAccountStatus = linkedAccountStatus;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getPayeeType() {
        return payeeType;
    }

    public void setPayeeType(String payeeType) {
        this.payeeType = payeeType;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public boolean isFixedCRN() {
        return fixedCRN;
    }

    public void setFixedCRN(boolean fixedCRN) {
        this.fixedCRN = fixedCRN;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getSaveToList() {
        return saveToList;
    }

    public void setSaveToList(String saveToList) {
        this.saveToList = saveToList;
    }

    public String getDevieNumber() {
        return devieNumber;
    }

    public void setDevieNumber(String devieNumber) {
        this.devieNumber = devieNumber;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public String getManuallyVerifiedFlag() {
        return manuallyVerifiedFlag;
    }

    public void setManuallyVerifiedFlag(String manuallyVerifiedFlag) {
        this.manuallyVerifiedFlag = manuallyVerifiedFlag;
    }
}
