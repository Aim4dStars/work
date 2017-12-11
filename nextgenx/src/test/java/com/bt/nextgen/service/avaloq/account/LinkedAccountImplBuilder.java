package com.bt.nextgen.service.avaloq.account;

public class LinkedAccountImplBuilder {

    private String name = "default-name";
    private String bsb = "111111";
    private String nickName = "nick";
    private String accountNumber = "12345678";
    private boolean isPrimary = false;

    public static LinkedAccountImplBuilder aLinkedAccountImpl() {
        return new LinkedAccountImplBuilder();
    }

    public LinkedAccountImpl build() {
        LinkedAccountImpl linkedAccount = new LinkedAccountImpl();
        linkedAccount.setName(name);
        linkedAccount.setBsb(bsb);
        linkedAccount.setNickName(nickName);
        linkedAccount.setAccountNumber(accountNumber);
        linkedAccount.setPrimary(isPrimary);
        return linkedAccount;
    }

    public LinkedAccountImplBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LinkedAccountImplBuilder withBsb(String bsb) {
        this.bsb = bsb;
        return this;
    }

    public LinkedAccountImplBuilder withNickname(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public LinkedAccountImplBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public LinkedAccountImplBuilder withIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }
}
