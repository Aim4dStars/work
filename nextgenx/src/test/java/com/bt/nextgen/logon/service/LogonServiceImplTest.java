package com.bt.nextgen.logon.service;


import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogonServiceImplTest
{

	@InjectMocks LogonServiceImpl logonServiceImpl;
	
	@Mock
	private WebServiceProvider provider;
   
	private BankingAuthorityService userSamlService;
	
	@Mock
	CustomerPasswordManagementIntegrationService customerPasswordManagement;

	@Mock
	UserProfileService userProfileService;

    @Mock
    UserInformationIntegrationService userInformationIntegrationService;

	@Mock
	FeatureTogglesService featureTogglesService;

	@Mock
	PrmService prmService;

	@Before
	public void setup()
	{
		provider = mock(WebServiceProvider.class);
		userSamlService = mock(BankingAuthorityService.class);
		ReflectionTestUtils.setField(logonServiceImpl, "userSamlService", userSamlService);
		when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
		ReflectionTestUtils.setField(logonServiceImpl, "provider", provider);
		when(userProfileService.getUserId()).thenReturn("userID");
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(new FeatureToggles());
	}
	
	@Test
	public void testModifyUserAlias() throws Exception
	{
		String credentialID = "adviser";
		String modifiedUserName = "adviser1";
		ModifyChannelAccessCredentialResponse response = mock(ModifyChannelAccessCredentialResponse.class);
		when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), anyString(), anyObject())).thenReturn(response);
		when(provider.sendWebService(anyString(),anyObject())).thenReturn(response);
		doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus serviceStatus = new ServiceStatus();
                StatusInfo statusInfo = new StatusInfo();
                statusInfo.setLevel(Level.SUCCESS);
                serviceStatus.getStatusInfo().add(0, statusInfo);
                return serviceStatus;
            }
        }).when(response).getServiceStatus();


		
		String status = logonServiceImpl.modifyUserAlias(credentialID,modifiedUserName);
		assertThat(status,Is.is("SUCCESS"));
	}
	
	@Test
	public void testUpdatePassword_Success() throws Exception
	{
		UserReset userReset = new UserReset();
		userReset.setPassword("adviser");
		userReset.setConfirmPassword("adviser");
		userReset.setRequestedAction(ServiceConstants.UPDATE_PASSWORD);
		userReset.setCredentialId("adviser");
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerPasswordManagement.updatePassword(any(UserReset.class), any(ServiceErrors.class))).thenReturn(customerCredentialManagementInformation);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.SUCCESS_MESSAGE);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		FeatureToggles featureToggles = new FeatureToggles();
		featureToggles.setFeatureToggle("prmNonValueEvents",true);
		when(featureTogglesService.findOne(Matchers.any(ServiceErrorsImpl.class))).thenReturn(featureToggles);
		String status = logonServiceImpl.updatePassword(userReset, serviceErrors);
		//verify no call to avaloqClientIntegrationService.sendPasswordChangeInformation() method when returning error
		Mockito.verify(userInformationIntegrationService, Mockito.times(1)).notifyPasswordChange(any(UserProfile.class), any(ServiceErrors.class));
		assertThat(status, Is.is(Attribute.SUCCESS_MESSAGE));
	}
	
	@Test
	public void testUpdatePassword_Fail() throws Exception
	{
		UserReset userReset = new UserReset();
		userReset.setPassword("adviser");
		userReset.setConfirmPassword("adviser");
		userReset.setRequestedAction(ServiceConstants.UPDATE_PASSWORD);
		userReset.setCredentialId("adviser");
		CustomerCredentialManagementInformation customerCredentialManagementInformation = mock(CustomerCredentialManagementInformation.class);
		when(customerPasswordManagement.updatePassword(any(UserReset.class), any(ServiceErrors.class))).thenReturn(customerCredentialManagementInformation);
		when(customerCredentialManagementInformation.getServiceLevel()).thenReturn(Attribute.ERROR_MESSAGE);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		
		String status = logonServiceImpl.updatePassword(userReset, serviceErrors);

		//verify no call to avaloqClientIntegrationService.sendPasswordChangeInformation() method when returning error
        Mockito.verify(userInformationIntegrationService, Mockito.times(0)).notifyPasswordChange(any(UserProfile.class), any(ServiceErrors.class));
		assertThat(status, Is.is(Attribute.ERROR_MESSAGE));
	}
	

	
	@Test
	public void testValidateUser_successMessage()
	{
		String credentialID = "adviser";
		String lastName = "adviser";
		int postCode = 1111;
		String message = logonServiceImpl.validateUser(credentialID, lastName, postCode);
		assertThat(message, Is.is(Attribute.SUCCESS_MESSAGE));
	}
	
	@Test
	public void testValidateUser_accountLockedMessage()
	{
		String credentialID = "test1";
		String lastName = "test1";
		int postCode = 3345;
		String message = logonServiceImpl.validateUser(credentialID, lastName, postCode);
		assertThat(message, Is.is(Attribute.ACCOUNT_LOCKED_MESSAGE));
	}
	
	@Test
	public void testValidateUser_failureMessage()
	{
		String credentialID = "test";
		String lastName = "test";
		int postCode = 1111;
		String message = logonServiceImpl.validateUser(credentialID, lastName, postCode);
		assertThat(message, Is.is(Attribute.FAILURE_MESSAGE));
		
	}
	
	@Test
	public void testverifySmsCode_nullSmscode()
	{
		String credentialID = "adviser";
		String lastName = "adviser";
		int postCode = 1111;
		String smsCode = null;
		String message = logonServiceImpl.verifySmsCode(credentialID, lastName, postCode ,smsCode);
		assertThat(message, Is.is(Attribute.SUCCESS_MESSAGE));
	}
	
	@Test
	public void testverifySmsCode_SmscodeSuccessMessage()
	{
		String credentialID = "investor";
		String lastName = "investor";
		int postCode = 1111;
		String smsCode = "111111" ;
		String message = logonServiceImpl.verifySmsCode(credentialID, lastName, postCode ,smsCode);
		assertThat(message, Is.is(Attribute.SUCCESS_MESSAGE));		
	}
	
	@Test
	public void testverifySmsCode_SmscodeErrorMessage()
	{
		String credentialID = "investor";
		String lastName = "investor";
		int postCode = 1111;
		String smsCode = "222222" ;
		String message = logonServiceImpl.verifySmsCode(credentialID, lastName, postCode ,smsCode);
		assertThat(message, Is.is(Attribute.ERROR_MESSAGE));
	}
	
	@Test
	public void testverifySmsCode_SmscodeFailureMessage()
	{
		String credentialID = "test";
		String lastName = "test";
		int postCode = 1111;
		String smsCode = "222222" ;
		String message = logonServiceImpl.verifySmsCode(credentialID, lastName, postCode ,smsCode);
		assertThat(message, Is.is(Attribute.FAILURE_MESSAGE));
	}
	
	
}
