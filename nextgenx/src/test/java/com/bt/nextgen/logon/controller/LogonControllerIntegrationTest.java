package com.bt.nextgen.logon.controller;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.LogonController;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LogonControllerIntegrationTest extends BaseSecureIntegrationTest
{
	private static final Logger logger = LoggerFactory.getLogger(LogonControllerIntegrationTest.class);

	@Autowired
	LogonController logonController;
	
	@Before
	public void setup()
	{
		TestingAuthenticationToken authentication = new TestingAuthenticationToken("", "", Roles.ROLE_ADVISER.name());
		Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
		authentication.setDetails(dummyProfile);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	public void testresetPassword() throws Exception
	{
		logger.debug("Start of the method: testResetPassword()");
		UserReset userReset = new UserReset();
		userReset.setUserName("1234567");
		userReset.setPassword("test0987");
		userReset.setNewpassword("test@123");
		userReset.setConfirmPassword("test@123");
		userReset.setHalgm("halgm");
		BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));
		AjaxResponse result;
		result = logonController.resetPassword(userReset, bindingResult);
		assertNotNull(result);
		assertThat(result.getData(), IsNull.notNullValue());
		assertEquals(true, result.isSuccess());
		logger.debug("End of the method: testResetPassword()");

	}

	@Ignore
	@Test
	public void testResetUsername() throws Exception
	{
		logger.debug("Start of the method: testResetUsername()");
		UserReset userReset = new UserReset();
		userReset.setUserName("1234567");
		userReset.setNewUserName("newusername");
		userReset.setPassword("test0987");
		BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));
		AjaxResponse result;
		result = logonController.resetUsername(userReset, bindingResult);
		assertNotNull(result);
		assertThat(result.getData(), IsNull.notNullValue());
		assertEquals(true, result.isSuccess());
		logger.debug("End of the method: testResetUsername()");
	}

	/*@Test
	public void validateNewPassword() throws Exception
	{
		logger.debug("Start of the method: validateNewPassword()");
		String userName = "f";
		String newPassword = "password@4516";
		AjaxResponse result = null;
		result = logonController.validateNewPassword(userName, newPassword);
		assertNotNull(result);
		assertThat(result.getData(), IsNull.notNullValue());
		assertEquals(true, result.isSuccess());
		logger.debug("End of the method: validateNewPassword()");
	}

	@Test
	public void testValidateModifiedUsername() throws Exception
	{
		logger.debug("Start of the method: testValidateModifiedUsername()");
		String userName = "f";
		String modifiedUsername = "modified";
		AjaxResponse result = null;
		result = logonController.validateModifiedUsername(userName, modifiedUsername);
		assertNotNull(result);
		assertThat(result.getData(), IsNull.notNullValue());
		assertEquals(true, result.isSuccess());
		logger.debug("End of the method: testValidateModifiedUsername()");
	}*/

}
