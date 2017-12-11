package com.bt.nextgen.logon.service;

import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.service.avaloq.userinformation.UserPasswordDetailImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserPasswordDetail;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class LogonServiceImplIntegrationTest
{


	@InjectMocks
	LogonServiceImpl logonService;

	private UserReset userDetails;
	private String credentialId;
	private String newUserName;
	@Mock
	PrmService prmService;
	@Mock
	FeatureTogglesService featureTogglesService;

	@Mock
	CustomerPasswordManagementIntegrationService customerPasswordManagement;
	@Mock
	UserInformationIntegrationService userInformationIntegrationService;

	@Mock
	UserProfileService profileService;

	UserProfile	userProfile;

	@Before
	public void setUp()
	{
		TestingAuthenticationToken authentication = new TestingAuthenticationToken("", "", Roles.ROLE_ADVISER.name());
		Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
		authentication.setDetails(dummyProfile);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userDetails = new UserReset();
		userDetails.setPassword("Nextgen00");
		userDetails.setNewpassword("Invalid_Password_1");
		userDetails.setHalgm("halgm");
		credentialId = "investor";
		newUserName = "investor01";
		userDetails.setBankReferenceId("56463638");
		userProfile = mock(UserProfile.class);
	}

	@Test
	@SecureTestContext
	public void testChangePassword() throws Exception
	{
		userDetails.setRequestedAction("ResetPassword");
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		Mockito.when(customerCredentialManagementInformation.getServiceLevel()).thenReturn("SUCCESS");
		UserPasswordDetail userPasswordDetail = mock(UserPasswordDetailImpl.class);
		Mockito.when(customerPasswordManagement.updatePassword(Mockito.any(UserReset.class),Mockito.any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);
		Mockito.when(userInformationIntegrationService.notifyPasswordChange(Mockito.any(BankingCustomerIdentifier.class),Mockito.any(ServiceErrorsImpl.class))).thenReturn(userPasswordDetail);
		Mockito.when(profileService.getActiveProfile()).thenReturn(userProfile);
		FeatureToggles featureToggles = new FeatureToggles();
		featureToggles.setFeatureToggle("prmNonValueEvents",true);
		Mockito.when(featureTogglesService.findOne(Mockito.any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
		String status = logonService.updatePassword(userDetails, new ServiceErrorsImpl());
		assertNotNull(status);
		//Result will be success as we are loading different xmls for UpdatePassword,ResetPassword operation.
		assertThat(status, is("SUCCESS"));
	}

	@Test
	@SecureTestContext
	public void testUpdatePassword() throws Exception
	{
		userDetails.setRequestedAction("ResetPassword");
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		Mockito.when(customerCredentialManagementInformation.getServiceLevel()).thenReturn("ERROR");
		UserPasswordDetail userPasswordDetail = mock(UserPasswordDetailImpl.class);
		Mockito.when(customerPasswordManagement.updatePassword(Mockito.any(UserReset.class),Mockito.any(ServiceErrorsImpl.class))).thenReturn(customerCredentialManagementInformation);
		Mockito.when(userInformationIntegrationService.notifyPasswordChange(Mockito.any(BankingCustomerIdentifier.class),Mockito.any(ServiceErrorsImpl.class))).thenReturn(userPasswordDetail);
		Mockito.when(profileService.getActiveProfile()).thenReturn(userProfile);
		FeatureToggles featureToggles = new FeatureToggles();
		featureToggles.setFeatureToggle("prmNonValueEvents",true);
		Mockito.when(featureTogglesService.findOne(Mockito.any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
		userDetails.setRequestedAction("UpdatePassword");
		userDetails.setConfirmPassword("Invalid_Password_1");
		String status = logonService.updatePassword(userDetails, new ServiceErrorsImpl());
		assertNotNull(status);
		assertThat(status, is("ERROR"));
	}

	//TODO : Fix this
	@Ignore
	@Test
	@SecureTestContext
	public void testModifyUserAlias() throws Exception
	{
		UserReset userReset = new UserReset();
		userReset.setCredentialId(credentialId);
		userReset.setNewUserName(newUserName);
		userReset.setBankReferenceId("56463638");
		String status = logonService.modifyUserAlias(userReset, new ServiceErrorsImpl());
		assertNotNull(status);
		assertThat(status, is("SUCCESS"));
	}

}