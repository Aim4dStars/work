package com.bt.nextgen.api.movemoney.v3.model;

// used to differentiate deposits
public class RecurringDepositKey extends DepositKey {
    public RecurringDepositKey() {
        super();
    }

    public RecurringDepositKey(String depositId) {
        super(depositId);
    }
}
