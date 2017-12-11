package com.bt.nextgen.api.pushnotification.model;

/**
 * Identifier for PushNotificationDto
 */
public class PushSubscriptionDtoKey {

    private String deviceUid;

    /**
     * @param deviceUid
     */
    public PushSubscriptionDtoKey(String deviceUid) {
        this.deviceUid = deviceUid;
    }

    public PushSubscriptionDtoKey() {
    }

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }

}
