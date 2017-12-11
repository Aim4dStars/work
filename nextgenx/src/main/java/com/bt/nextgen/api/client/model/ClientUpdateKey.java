package com.bt.nextgen.api.client.model;

import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;

public class ClientUpdateKey extends ClientKey {

    private final String updateType;
    private final String cisId;
    private final String clientType;

    public ClientUpdateKey(String clientId, String updateType, String cisId, String clientType) {
        super(clientId);
        this.cisId = cisId;
        this.clientType = clientType;
        this.updateType = updateType;
    }

    public String getUpdateType() {
        return updateType;
    }

    public String getCisId() {
        return cisId;
    }

    public String getClientType() {
        return clientType;
    }

    private Object[] fields() {
        return new Object[]{ getClientId(), updateType, cisId, clientType };
    }

    @Override
    public int hashCode() {
        return nullSafeHashCode(fields());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof ClientUpdateKey
                && nullSafeEquals(fields(), ((ClientUpdateKey) obj).fields());
    }

    @Override
    public String toString() {
        return "ClientUpdateKey{clientId:" + getClientId() + ";cisId:" + cisId + ";clientType:" + clientType
                + ";updateType" + updateType + "}";
    }
}
