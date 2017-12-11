package com.bt.nextgen.api.movemoney.v3.model;

public class DepositKey {
    private String depositId;

    public DepositKey() {
        // default constructor
    }

    public DepositKey(String depositId) {
        if (depositId == null) {
            throw new IllegalArgumentException("depositId cannot be null");
        }

        this.depositId = depositId;
    }

    public String getDepositId() {
        return depositId;
    }
}
