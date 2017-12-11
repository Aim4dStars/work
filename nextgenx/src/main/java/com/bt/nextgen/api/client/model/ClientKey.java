package com.bt.nextgen.api.client.model;

import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;

public class ClientKey {

    private final String clientId;

    public ClientKey(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
            return clientId;
    }

    @Override
    public int hashCode() {
        return nullSafeHashCode(clientId);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientKey && nullSafeEquals(clientId, ((ClientKey) obj).clientId);
    }
}
