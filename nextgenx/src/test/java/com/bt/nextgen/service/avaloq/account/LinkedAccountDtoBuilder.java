package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.api.account.v1.model.LinkedAccountDto;

public class LinkedAccountDtoBuilder {

    private String name = "default-name";
    private String bsb = "111111";
    private String nickName = "nick";
    private String accountNumber = "12345678";
    private boolean isPrimary = false;

    public static LinkedAccountDtoBuilder linkedAccountDto() {
        return new LinkedAccountDtoBuilder();
    }

    public LinkedAccountDto build() {
        LinkedAccountDto linkedAccount = new LinkedAccountDto();
        linkedAccount.setName(name);
        linkedAccount.setBsb(bsb);
        linkedAccount.setNickName(nickName);
        linkedAccount.setAccountNumber(accountNumber);
        linkedAccount.setPrimary(isPrimary);
        return linkedAccount;
    }

    public LinkedAccountDtoBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LinkedAccountDtoBuilder withBsb(String bsb) {
        this.bsb = bsb;
        return this;
    }

    public LinkedAccountDtoBuilder withNickname(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public LinkedAccountDtoBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public LinkedAccountDtoBuilder withIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }
}
