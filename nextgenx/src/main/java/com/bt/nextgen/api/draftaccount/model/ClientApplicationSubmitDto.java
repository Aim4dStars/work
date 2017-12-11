package com.bt.nextgen.api.draftaccount.model;


import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class ClientApplicationSubmitDto extends BaseDto implements KeyedDto<ClientApplicationKey> {
    private ClientApplicationKey key;
    private boolean offline;
    private String adviserId;
    private String productId;

    public ClientApplicationSubmitDto() {}

    public ClientApplicationSubmitDto(ClientApplicationKey key) {
        this.key = key;
    }

    @Override
    public ClientApplicationKey getKey() {
        return key;
    }

    public void setKey(ClientApplicationKey key) {
        this.key = key;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
