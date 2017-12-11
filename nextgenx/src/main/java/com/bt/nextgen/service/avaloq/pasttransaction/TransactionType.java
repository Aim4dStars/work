package com.bt.nextgen.service.avaloq.pasttransaction;

public enum TransactionType {
    INPAY("inpay"), PAY("pay"), UNKNOWN("Unknown");

    private String name;

    TransactionType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public String getTransactionType() {
        return name;
    }
}
