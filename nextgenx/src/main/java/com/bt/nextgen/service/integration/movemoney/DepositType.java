package com.bt.nextgen.service.integration.movemoney;

public enum DepositType {
    INPAY("inpay"),
    PAY("pay"),
    UNKNOWN("Unknown");

    private String name;

    DepositType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getDepositType() {
        return name;
    }
}
