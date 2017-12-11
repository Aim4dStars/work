package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

@Deprecated
public class AccountSubscriptionDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;
    private String subscriptionType;
    private List<InitialInvestmentAssetDto> initialInvestments;

    public AccountSubscriptionDto() {
    }

    public AccountSubscriptionDto(AccountKey key, String subscriptionType) {
        this.key = key;
        this.subscriptionType = subscriptionType;
    }

    public AccountSubscriptionDto(AccountKey key, String subscriptionType, List<InitialInvestmentAssetDto> initialInvestments) {
        this.key = key;
        this.subscriptionType = subscriptionType;
        this.initialInvestments = initialInvestments;
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

    public List<InitialInvestmentAssetDto> getInitialInvestments() {
        return initialInvestments;
    }

    public void setInitialInvestments(List<InitialInvestmentAssetDto> initialInvestments) {
        this.initialInvestments = initialInvestments;
    }
}
