package com.bt.nextgen.core.repository;

/**
 * Interface for the repository for managing push notification details
 */
public interface PushSubscriptionRepository {

    public PushSubscriptionDetails find(String deviceUid, String userId);

    public PushSubscriptionDetails update(PushSubscriptionDetails pushSubscriptionDetails);

}
