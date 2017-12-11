package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.integration.product.ProductKey;

public class TermDepositCalculatorAccountKey extends TermDepositCalculatorKey {
    private final AccountKey accountKey;

    public TermDepositCalculatorAccountKey(ProductKey badge, String amount, AccountKey accountKey) {
        super(badge, amount);
        this.accountKey = accountKey;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    @Override
    public String toString() {
        return "TermDepositCalculatorAccountKey [accountKey=" + accountKey + ", getBadge()=" + getBadge() + ", getAmount()="
                + getAmount() + ", toString()=" + super.toString() + "]";
    }
}
