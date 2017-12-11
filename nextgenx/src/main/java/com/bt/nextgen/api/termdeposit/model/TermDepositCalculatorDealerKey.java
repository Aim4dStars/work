package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;

public class TermDepositCalculatorDealerKey extends TermDepositCalculatorKey {
    private final BrokerKey brokerKey;
    private final String accountType;

    public TermDepositCalculatorDealerKey(ProductKey badge, String amount, BrokerKey brokerKey, String accountType) {
        super(badge, amount);
        this.brokerKey = brokerKey;
        this.accountType = accountType;
    }

    public BrokerKey getBrokerKey() {
        return brokerKey;
    }
    @Override
    public String toString() {
        return "TermDepositCalculatorDealerKey [brokerKey=" + brokerKey + ", getBadge()=" + getBadge() + ", getAmount()="
                + getAmount() + ", getAccountType()=" + getAccountType()+ "]";
    }

    public String getAccountType() {
        return accountType;
    }
}
