package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class AccountSubscriptionDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;
    private String subscriptionType;
    private List<InitialInvestmentDto> initialInvestments;
    private DepositDto deposit;

    public AccountSubscriptionDto(AccountKey key) {
        this.key = key;
    }

    public AccountSubscriptionDto(AccountKey key, String subscriptionType) {
        this.key = key;
        this.subscriptionType = subscriptionType;
    }

    public AccountSubscriptionDto(AccountKey key, String subscriptionType, List<InitialInvestmentDto> initialInvestments) {
        this.key = key;
        this.subscriptionType = subscriptionType;
        this.initialInvestments = initialInvestments;
    }

    public AccountSubscriptionDto(AccountSubscriptionDto subscriptionDto, DepositDto deposit) {
        this.key = subscriptionDto.getKey();
        this.subscriptionType = subscriptionDto.getSubscriptionType();
        this.initialInvestments = subscriptionDto.getInitialInvestments();
        this.deposit = deposit;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    @Override
    public AccountKey getKey() {
        return this.key;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public List<InitialInvestmentDto> getInitialInvestments() {
        return initialInvestments;
    }

    public void setInitialInvestments(List<InitialInvestmentDto> initialInvestments) {
        this.initialInvestments = initialInvestments;
    }

    public DepositDto getDeposit() {
        return deposit;
    }

    public void setDeposit(DepositDto deposit) {
        this.deposit = deposit;
    }
}
