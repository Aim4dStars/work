package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.api.account.v1.model.DepositDto;

@Deprecated
public class AccountSubscriptionDepositDto extends AccountSubscriptionDto {

    private DepositDto deposit;

    public AccountSubscriptionDepositDto(AccountSubscriptionDto subscription, DepositDto deposit) {
        super(subscription.getKey(), subscription.getSubscriptionType(), subscription.getInitialInvestments());
        this.deposit = deposit;
    }

    public DepositDto getDeposit() {
        return deposit;
    }

    public void setDeposit(DepositDto deposit) {
        this.deposit = deposit;
    }
}
