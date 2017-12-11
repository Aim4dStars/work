package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;

import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;

/**
 * Created by F030695 on 14/12/2015.
 */
public class GcmKey extends BaseDto implements BankingCustomerIdentifier, KeyedDto<ClientUpdateKey> {

    private String gcmId;

    public GcmKey() {
        //Default constructor
    }

    public GcmKey(String gcmId) {
        this.gcmId = gcmId;
    }

    @Override
    public String getBankReferenceId() {
        return gcmId;
    }

    @Override
    public UserKey getBankReferenceKey() {
        return null;
    }

    @Override
    public CISKey getCISKey() {
        return null;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    @Override
    public int hashCode() {
        return nullSafeHashCode(gcmId);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof GcmKey && nullSafeEquals(gcmId, ((GcmKey) obj).gcmId);
    }

    @Override
    public ClientUpdateKey getKey() {
        return null;
    }
}
