package com.bt.nextgen.service.integration.registration.repository;


import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository("UserRoleTermsAndConditionsRepository")
public class UserRoleTermsAndConditionsRepositoryImpl implements UserRoleTermsAndConditionsRepository
{
	private static final Logger logger = LoggerFactory.getLogger(UserRoleTermsAndConditionsRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;


	public static UserRoleTermsAndConditions createNewUserRoleTermsAndConditions(String gcmId, String jobProfileId)
	{
		UserRoleTermsAndConditionsKey userRoleTncKey = new UserRoleTermsAndConditionsKey(gcmId, jobProfileId);

		UserRoleTermsAndConditions userRoleTermsAndConditions = new UserRoleTermsAndConditions();
		userRoleTermsAndConditions.setUserRoleTermsAndConditionsKey(userRoleTncKey);

		return userRoleTermsAndConditions;
	}

	@Override
	public UserRoleTermsAndConditions find(UserRoleTermsAndConditionsKey key)
	{
		UserRoleTermsAndConditions userRole = entityManager.find(UserRoleTermsAndConditions.class, key);

		logger.debug("Retrieved {} record from USER_ROLE_TNC table with profileId: {} and gcmId: {}",
					userRole == null ? "1" : "0", key.getJobProfileId(), key.getGcmId());

		return userRole;
	}

	@Override
	@Transactional(value = "springJpaTransactionManager")
	public void save(UserRoleTermsAndConditions userRole)
	{
		entityManager.merge(userRole);
		entityManager.flush();
	}
}