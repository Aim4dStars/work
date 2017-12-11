package com.bt.nextgen.api.smsf.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

/**
 * External assets smsf - dto for save/update
 *
 */
public class ExternalAssetTrxnDto extends BaseDto implements KeyedDto <AccountKey>{

    private AccountKey key;

    private String container;

    private List <ExternalAssetDto> assetDtos;

    private String transactionStatus;

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public List<ExternalAssetDto> getAssetDtos() {
        return assetDtos;
    }

    public void setAssetDtos(List<ExternalAssetDto> assetDtos) {
        this.assetDtos = assetDtos;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}

