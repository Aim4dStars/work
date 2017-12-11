package com.bt.nextgen.core.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This repository retrieves and updates a user's preference
 */
@Repository("userPreferenceRepository")
public class UserPreferenceRepositoryImpl implements UserPreferenceRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferenceRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find a user's saved preference for the given key
     *
     * @param userId user's gcm id
     * @param key    preference key to retrieve
     */
    @Override public UserPreference find(String userId, String key) {
        UserPreference userPreference = entityManager.find(UserPreference.class, new UserPreferenceKey(userId, key));
        if (userPreference == null) {
            LOGGER.info("Preference: {} for user: {} doesn't exist", key, userId);
        } else {
            LOGGER.info("Retrieved preference: {} for user: {}", key, userId);
        }
        return userPreference;
    }

    /**
     * Update a user's preference
     *
     * @param userPreference user's preference details to update
     */
    @Override
    @Transactional(value = "springJpaTransactionManager")
    public UserPreference save(UserPreference userPreference) {
        UserPreference updated = entityManager.merge(userPreference);
        entityManager.flush();
        LOGGER.info("Updated preference: {} for user: {}", updated.getKey().getPreferenceId(),
            updated.getKey().getUserId());
        return updated;
    }
}
