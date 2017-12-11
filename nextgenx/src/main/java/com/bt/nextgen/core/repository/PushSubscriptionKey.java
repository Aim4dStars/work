package com.bt.nextgen.core.repository;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identifier for Push Notification in the table
 */
@Embeddable
public class PushSubscriptionKey implements Serializable {

    @Column(name = "DEVICE_UID")
    private String deviceUid;

    @Column(name = "USER_ID")
    private String userId;

    public PushSubscriptionKey() {
    }

    /**
     * Constructs a PushNotificationKey
     *
     * @param deviceUid
     * @param userId
     */
    public PushSubscriptionKey(String deviceUid, String userId) {
        this.deviceUid = deviceUid;
        this.userId = userId;
    }

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
