package com.bt.nextgen.service.avaloq.pasttransaction;

public enum TransactionOrderType {
    CHQ("INCOMING PAYMENT.INPAY#CHQ"),
    CHQ_FILE("INCOMING PAYMENT.INPAY#CHQ_FILE"),
    BPAY_FILE("INCOMING PAYMENT.INPAY#BPAY_FILE"),
    BPAY("pay_bpay"),
    PAY_ANYONE("pay_payany");

    private String name;

    public String getName() {
        return name;
    }

    TransactionOrderType(String orderType) {
        this.name = orderType;
    }

    public String toString() {
        return name;
    }
}
