package com.bt.nextgen.core.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * This repository retrieves and updates a push notification details
 */

@Repository("pushSubscriptionRepository")
public class PushSubscriptionRepositoryImpl implements PushSubscriptionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushSubscriptionRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Finds the push notification details in the table
     *
     * @param deviceUid
     * @param userId
     * @return
     */
    @Override
    public PushSubscriptionDetails find(String deviceUid, String userId) {
        final PushSubscriptionDetails pushSubscriptionDetails = entityManager.find(PushSubscriptionDetails.class,
                new PushSubscriptionKey(deviceUid, userId));
        if (pushSubscriptionDetails == null) {
            LOGGER.info("Push details for deviceId: {} & userId: {} doesn't exist", deviceUid, userId);
        }
        return pushSubscriptionDetails;
    }

    /**
     * Updates the push notification details in the table
     *
     * @param pushSubscriptionDetails
     * @return updated pushNotification details
     */
    @Override
    @Transactional(value = "springJpaTransactionManager")
    public PushSubscriptionDetails update(PushSubscriptionDetails pushSubscriptionDetails) {
        final PushSubscriptionDetails updatedDetails = entityManager.merge(pushSubscriptionDetails);
        entityManager.flush();
        LOGGER.info("Updated push subscription details for deviceId: {}, user: {}",
                updatedDetails.getKey().getDeviceUid(), updatedDetails.getKey().getUserId());

        return updatedDetails;
    }
}
