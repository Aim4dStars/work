package com.bt.nextgen.api.account.v3.model;


import com.bt.nextgen.api.client.model.ClientKey;

public class AccountSearchKey extends ClientKey {
    private AccountSearchTypeEnum searchType;

    public AccountSearchKey(String clientId, AccountSearchTypeEnum searchType) {
        super(clientId);
        this.searchType = searchType;
    }

    public AccountSearchTypeEnum getSearchType() {
        return searchType;
    }
}