package com.bt.nextgen.service.integration.registration.repository;


import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserRoleTermsAndConditionsRepositoryImplTest  extends BaseSecureIntegrationTest
{
	@Autowired
	UserRoleTermsAndConditionsRepositoryImpl userRoleTermsAndConditionsRepositoryImpl;

	@Test
	@Transactional(value = "springJpaTransactionManager")
	@Rollback(true)
	public void testNoUserRoleTermsAndConditions()
	{
		UserRoleTermsAndConditions userRole = userRoleTermsAndConditionsRepositoryImpl.find(new UserRoleTermsAndConditionsKey("20167777", "1234566"));
		assertTrue(userRole == null);
	}

	@Test
	@Transactional(value = "springJpaTransactionManager")
	@Rollback(true)
	public void testSaveNewUserRole()
	{
		UserRoleTermsAndConditions userRoleTnc = new UserRoleTermsAndConditions();
		userRoleTnc.setModifyDatetime(new Date());
		userRoleTnc.setTncAccepted("Y");
		userRoleTnc.setTncAcceptedOn(new Date());
		userRoleTnc.setVersion(1);
		userRoleTnc.setUserRoleTermsAndConditionsKey(new UserRoleTermsAndConditionsKey("20167777", "44435555"));

		userRoleTermsAndConditionsRepositoryImpl.save(userRoleTnc);
	}


	@Test
	@Transactional(value = "springJpaTransactionManager")
	@Rollback(true)
	public void testSaveNewUserRoleAndRetrieve()
	{
		Date tncAcceptedOn = new Date();

		UserRoleTermsAndConditions userRoleTnc = new UserRoleTermsAndConditions();
		userRoleTnc.setModifyDatetime(new Date());
		userRoleTnc.setTncAccepted("Y");
		userRoleTnc.setTncAcceptedOn(tncAcceptedOn);
		userRoleTnc.setVersion(1);
		userRoleTnc.setUserRoleTermsAndConditionsKey(new UserRoleTermsAndConditionsKey("20167777", "44435555"));

		userRoleTermsAndConditionsRepositoryImpl.save(userRoleTnc);

		UserRoleTermsAndConditions userRole = userRoleTermsAndConditionsRepositoryImpl.find(new UserRoleTermsAndConditionsKey("20167777", "44435555"));
		assertTrue(userRole != null);
		assertEquals("Y", userRoleTnc.getTncAccepted());
		assertEquals(tncAcceptedOn, userRoleTnc.getTncAcceptedOn());
		assertEquals("20167777", userRoleTnc.getUserRoleTermsAndConditionsKey().getGcmId());
		assertEquals("44435555", userRoleTnc.getUserRoleTermsAndConditionsKey().getJobProfileId());
	}


	@Test
	@Transactional(value = "springJpaTransactionManager")
	@Rollback(true)
	public void testUpdateTermsAndConditionsFlag()
	{
		Date tncAcceptedOn = new Date();

		UserRoleTermsAndConditions userRoleTnc = new UserRoleTermsAndConditions();
		userRoleTnc.setModifyDatetime(new Date());
		userRoleTnc.setTncAccepted("N");
		userRoleTnc.setTncAcceptedOn(tncAcceptedOn);
		userRoleTnc.setVersion(1);
		userRoleTnc.setUserRoleTermsAndConditionsKey(new UserRoleTermsAndConditionsKey("20167777", "44435555"));

		userRoleTermsAndConditionsRepositoryImpl.save(userRoleTnc);
		UserRoleTermsAndConditions userRoleTnc2 = userRoleTermsAndConditionsRepositoryImpl.find(new UserRoleTermsAndConditionsKey("20167777", "44435555"));

		assertTrue(userRoleTnc2 != null);
		assertEquals("N", userRoleTnc2.getTncAccepted());
		assertEquals(tncAcceptedOn, userRoleTnc2.getTncAcceptedOn());
		assertEquals("20167777", userRoleTnc2.getUserRoleTermsAndConditionsKey().getGcmId());
		assertEquals("44435555", userRoleTnc2.getUserRoleTermsAndConditionsKey().getJobProfileId());

		userRoleTnc2.setModifyDatetime(new Date());
		userRoleTnc2.setTncAccepted("Y");
		userRoleTnc2.setTncAcceptedOn(tncAcceptedOn);
		userRoleTermsAndConditionsRepositoryImpl.save(userRoleTnc2);

		UserRoleTermsAndConditions updatedUserRoleTnc = userRoleTermsAndConditionsRepositoryImpl.find(new UserRoleTermsAndConditionsKey("20167777", "44435555"));
		assertTrue(updatedUserRoleTnc != null);
		assertEquals("Y", updatedUserRoleTnc.getTncAccepted());
		assertEquals(tncAcceptedOn, updatedUserRoleTnc.getTncAcceptedOn());
	}


	//@Test
	@Transactional
	@Rollback(true)
	public void testNoUserRoleTermsAndConditionsFromRepo()
	{
		//UserRoleTermsAndConditions userRole = (UserRoleTermsAndConditions) userRoleRepositoryImpl.findOne(new UserRoleTermsAndConditionsKey("20167777", "1234566"));
		//assertTrue(userRole == null);
	}
}