package com.bt.nextgen.core.repository;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity for table PUSH_SUBSCRIPTION_DETAILS
 */
@Entity
@Table(name = "PUSH_SUBSCRIPTION_DETAILS")
public class PushSubscriptionDetails implements Serializable {

    @EmbeddedId
    private PushSubscriptionKey key;

    @Column(name = "ACTIVE_FLAG")
    private boolean active;

    @Column(name = "PLATFORM")
    private String platform;

    public PushSubscriptionKey getKey() {
        return key;
    }

    public void setKey(PushSubscriptionKey key) {
        this.key = key;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
