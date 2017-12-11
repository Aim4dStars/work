package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ProviderErrorDetail;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusDetail;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.web.model.User;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.util.SamlUtil;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerUserNameManagementImplTest
{
	@Mock
    private BankingAuthorityService userSamlService;
	
	@Mock
	private WebServiceProvider provider;
	
	private GroupEsbCustomerUserNameManagementImpl gesbcunmService;
	
	private ModifyChannelAccessCredentialResponse modifyChannelAccessCredentialResponse;
	
	private ModifyChannelAccessCredentialResponse modifyChannelAccessCredentialFailResponse;
	
	private CredentialRequestModel credentialRequestModel;
	
	private CorrelatedResponse correlatedResponse;
	
	private CorrelatedResponse correlatedFailedResponse;
	
	private User usernameUpdateRequest;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp() throws Exception
	{
		gesbcunmService = new GroupEsbCustomerUserNameManagementImpl();
		credentialRequestModel = new CredentialRequestModel();
		credentialRequestModel.setBankReferenceId("30981");
		usernameUpdateRequest = new User();
		usernameUpdateRequest.setUserName("userName");
		usernameUpdateRequest.setNewUserName("userName");
        usernameUpdateRequest.setCredentialId("321");
		
		provider = mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(gesbcunmService, "provider", provider);
		userSamlService = mock(BankingAuthorityService.class);
		ReflectionTestUtils.setField(gesbcunmService, "userSamlService", userSamlService);
		
		modifyChannelAccessCredentialResponse = new ModifyChannelAccessCredentialResponse();
		ServiceStatus serviceStatus = new ServiceStatus();
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setLevel(Level.SUCCESS);
		serviceStatus.getStatusInfo().add(statusInfo);
		modifyChannelAccessCredentialResponse.setServiceStatus(serviceStatus);
		
		modifyChannelAccessCredentialFailResponse = new ModifyChannelAccessCredentialResponse();
		//status.getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode().equalsIgnoreCase(ErrorConstants.ALIAS_IN_USE_FAULT)
		StatusDetail statusDetail = new StatusDetail();
		ProviderErrorDetail providerErrorDetail = new ProviderErrorDetail();
		providerErrorDetail.setProviderErrorCode("aliasInUseFault");
		statusDetail.getProviderErrorDetail().add(providerErrorDetail);
		ServiceStatus failServiceStatus = new ServiceStatus();
		StatusInfo failStatusInfo = new StatusInfo();
		failStatusInfo.setLevel(Level.ERROR);
		failStatusInfo.setCode("000000");
		failStatusInfo.getStatusDetail().add(statusDetail);
		failServiceStatus.getStatusInfo().add(failStatusInfo);
		modifyChannelAccessCredentialFailResponse.setServiceStatus(failServiceStatus);
		
		correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), modifyChannelAccessCredentialResponse);
		correlatedFailedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), modifyChannelAccessCredentialFailResponse);
		
		Mockito.doAnswer(new Answer<SamlToken>() {
			@Override
			public SamlToken answer(InvocationOnMock invocation) throws Throwable {
				return 	new SamlToken(SamlUtil.loadSaml());
			}
			
		}).when(userSamlService).getSamlToken();
	}

	//TODO : Fix this
	@Ignore
	@Test
	public void testUpdateUsername() 
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		CustomerCredentialManagementInformation customerCredentialManagementInformation = gesbcunmService.updateUsername(usernameUpdateRequest, errors);
		
		assertThat(customerCredentialManagementInformation, notNullValue());
		assertThat(customerCredentialManagementInformation.getServiceLevel().toString(), Is.is("SUCCESS"));
	}

	//TODO : Fix this
	@Ignore
	@Test
	public void testUpdateUsernameFail() 
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedFailedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		CustomerCredentialManagementInformation customerCredentialManagementInformation = gesbcunmService.updateUsername(usernameUpdateRequest, errors);
		
		assertThat(customerCredentialManagementInformation, notNullValue());
		assertThat(customerCredentialManagementInformation.getServiceLevel().toString(), Is.is("ERROR"));
	}

	//TODO : Fix this
	@Ignore
	@Test
	public void testBlockUser() 
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		CustomerCredentialManagementInformation customerCredentialManagementInformation = gesbcunmService.blockUser("321", errors);
		
		assertThat(customerCredentialManagementInformation, notNullValue());
		assertThat(customerCredentialManagementInformation.getServiceLevel().toString(), Is.is("SUCCESS"));
	}
}
