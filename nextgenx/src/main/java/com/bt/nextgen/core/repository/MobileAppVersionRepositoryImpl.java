package com.bt.nextgen.core.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("mobileAppVersionRepository")
public class MobileAppVersionRepositoryImpl implements MobileAppVersionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileAppVersionRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns a list of all the available platform-version pairs
     * @return  List of MobileAppVersion
     */
    @Override
    public List<MobileAppVersion> findAppVersions() {
        final List<MobileAppVersion> mobileAppVersionList = entityManager.createQuery("from MobileAppVersion",
                MobileAppVersion.class).getResultList();
        if (CollectionUtils.isEmpty(mobileAppVersionList)) {
            LOGGER.info("No application version details found.");
        }
        return mobileAppVersionList;
    }

    /**
     * Updates/adds a new or existing moble platform-version data
     *
     * @param appVersion
     * @return
     */
    @Override
    @Transactional(value = "springJpaTransactionManager")
    public MobileAppVersion update(MobileAppVersion appVersion) {
        final MobileAppVersion updatedDetails = entityManager.merge(appVersion);
        entityManager.flush();
        return updatedDetails;
    }
}

