package com.bt.nextgen.service.group.customer.groupesb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserAlternateAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserNameAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.ActionCode;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordRequest;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordResponse;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseDetailsType;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseMsgType;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.core.Is;
import org.junit.Before;
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

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.validator.ValidationErrorCode;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerPasswordManagementImplTest
{
	@Mock
    private BankingAuthorityService userSamlService;
	
	@Mock
	private WebServiceProvider provider;
	
	@Mock
    CustomerLoginManagementIntegrationService customerLoginManagement;
	
	@Mock
	CmsService cmsService;

	private GroupEsbCustomerPasswordManagementImpl gesbcpmService;
	
	private AvaloqGatewayResponseMsgType response;
	
	private MaintainChannelAccessServicePasswordResponse maintainChannelAccessServicePasswordResponse;
	
	private MaintainChannelAccessServicePasswordResponse maintainChannelAccessServicePasswordFailResponse;
	
	private UserReset passwordUpdateRequest;
	
	private CorrelatedResponse correlatedResponse;
	
	private CorrelatedResponse correlatedFailedResponse;
	
	private InputStream stream;
	private InputStream failStream;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static final String responseXML = "<ns4:maintainChannelAccessServicePasswordResponse xmlns=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" " 
											+  "xmlns:ns2=\"http://www.westpac.com.au/gn/channelManagement/services/passwordManagement/common/xsd/v1/\" xmlns:ns3=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" " 
											+  "xmlns:ns4=\"http://www.westpac.com.au/gn/channelManagement/services/passwordManagement/xsd/maintainChannelAccessServicePassword/v2/SVC0247/\">" 
											+  "<serviceStatus><statusInfo><level>Success</level><code>00000000000000000</code></statusInfo></serviceStatus></ns4:maintainChannelAccessServicePasswordResponse>";
	
	private static final String failedResponseXML = "<ns4:maintainChannelAccessServicePasswordResponse xmlns=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" " +
			"xmlns:ns2=\"http://www.westpac.com.au/gn/channelManagement/services/passwordManagement/common/xsd/v1/\" xmlns:ns3=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" " +
			"xmlns:ns4=\"http://www.westpac.com.au/gn/channelManagement/services/passwordManagement/xsd/maintainChannelAccessServicePassword/v2/SVC0247/\">" +
			"<serviceStatus><statusInfo><level>Error</level><code>309,00012</code><description>The supplied password does not meet the password policy requirements.</description> " +
			"<statusDetail><mediationName>MVC0176v02</mediationName><providerErrorDetail><providerErrorCategory/><providerErrorCode>pwdPolicyValidationFault</providerErrorCode> " +
			"<providerErrorDescription>The supplied password does not meet the password policy requirements.</providerErrorDescription>	</providerErrorDetail>  " +
			"</statusDetail></statusInfo></serviceStatus></ns4:maintainChannelAccessServicePasswordResponse>";
	
	@Before
	public void setUp() throws Exception
	{
		gesbcpmService = new GroupEsbCustomerPasswordManagementImpl();
		passwordUpdateRequest = new UserReset();
		passwordUpdateRequest.setNewpassword("password123");
		passwordUpdateRequest.setPassword("password");
		passwordUpdateRequest.setHalgm("halgm");
		passwordUpdateRequest.setRequestedAction("UpdatePassword");
		passwordUpdateRequest.setCredentialId("321");

		provider = mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(gesbcpmService, "provider", provider);
		userSamlService = mock(BankingAuthorityService.class);
		ReflectionTestUtils.setField(gesbcpmService, "userSamlService", userSamlService);
		
		customerLoginManagement = mock(GroupEsbCustomerLoginManagementImpl.class);
		
		response = AvaloqObjectFactory.getResponseGatewayObjectFactory().createAvaloqGatewayResponseMsgType();
		AvaloqGatewayResponseDetailsType detail = AvaloqObjectFactory.getResponseGatewayObjectFactory()
			.createAvaloqGatewayResponseDetailsType();
		//detail.setSuccessResponse(responseXML);
		response.setResponseDetails(detail);

		stream = new ByteArrayInputStream(responseXML.getBytes("UTF-8"));
		failStream = new ByteArrayInputStream(failedResponseXML.getBytes("UTF-8"));
		
		maintainChannelAccessServicePasswordResponse = JaxbUtil.unmarshall(stream, MaintainChannelAccessServicePasswordResponse.class);
		stream.close();
		
		maintainChannelAccessServicePasswordFailResponse = JaxbUtil.unmarshall(failStream, MaintainChannelAccessServicePasswordResponse.class);
		failStream.close();
		
		Mockito.doAnswer(new Answer<SamlToken>() {
			@Override
			public SamlToken answer(InvocationOnMock invocation) throws Throwable {
				return 	new SamlToken(SamlUtil.loadSaml());
			}
			
		}).when(userSamlService).getSamlToken();
		
		RetrieveChannelAccessCredentialResponse credentialResponse = new RetrieveChannelAccessCredentialResponse();
		ServiceStatus serviceStatus = new ServiceStatus();
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setLevel(Level.SUCCESS);
		serviceStatus.getStatusInfo().add(statusInfo);
		credentialResponse.setServiceStatus(serviceStatus);
		UserCredentialDocument userCredentialDocument = new UserCredentialDocument();
		UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
		userNameAliasCredentialDocument.setUserAlias("userAlias");
		userNameAliasCredentialDocument.setUserId("123");
		
		UserAlternateAliasCredentialDocument hasAlternateAlias = new UserAlternateAliasCredentialDocument();
		hasAlternateAlias.setUserId("123");
		userNameAliasCredentialDocument.setHasAlternateUserNameAlias(hasAlternateAlias);
		
		userCredentialDocument.setUserName(userNameAliasCredentialDocument);
		CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
		credentialIdentifier.setCredentialId("321");
		userCredentialDocument.setInternalIdentifier(credentialIdentifier);
		credentialResponse.getUserCredential().add(userCredentialDocument);
		String requiredCustomerId = "123";
		
		CredentialRequestModel credentialRequestModel = new CredentialRequestModel();
		credentialRequestModel.setBankReferenceId("1111");
		
		final CustomerCredentialInformation customerCredentialInformation = new GroupEsbCustomerCredentialAdapter(credentialResponse,
			requiredCustomerId,
			new ServiceErrorsImpl());
		
		correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), maintainChannelAccessServicePasswordResponse);
		correlatedFailedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), maintainChannelAccessServicePasswordFailResponse);
		
		Mockito.doAnswer(new Answer<CustomerCredentialInformation>() {
			@Override
			public CustomerCredentialInformation answer(InvocationOnMock invocation) throws Throwable {
				return customerCredentialInformation;
			}
			
		}).when(customerLoginManagement).getCustomerInformation(any(CredentialRequestModel.class), any(ServiceErrorsImpl.class));
	}

	
	@Test
	public void testReqParams()  throws Exception
	{
		MaintainChannelAccessServicePasswordRequest request = gesbcpmService.createMaintainChannelAccessServicePasswordRequest(passwordUpdateRequest, new ServiceErrorsImpl());
		assertThat(request.getUserCredential().getCurrentPassword().getPassword(), Is.is("password"));
		assertThat(request.getRequestedAction(), Is.is(ActionCode.UPDATE_PASSWORD));
		//assertThat(request.getUserCredential().getNewPassword().getPassword(), Is.is("password123"));
		assertThat(request.getUserCredential().getInternalIdentifier().getCredentialId(), Is.is("321"));
        byte[] halgmEncoded = Base64.decodeBase64("halgm".getBytes("CP1252"));
		assertThat(request.getUserCredential().getEncryptionKey(), Is.is(halgmEncoded));
		assertThat(request.getUserCredential().getChannel().getChannelType(), Is.is("ONL"));
	}
	
	@Test
	public void testUpdatePassword() 
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));

		ServiceErrors errors = null;
		CustomerCredentialManagementInformation customerCredentialManagementInformation = gesbcpmService.updatePassword(passwordUpdateRequest, errors);
		assertThat(customerCredentialManagementInformation, notNullValue());
		assertThat(customerCredentialManagementInformation.getServiceLevel().toString(), Is.is("SUCCESS"));
	}
	
	@Test
	public void testUpdatePasswordFail() 
	{
		ServiceErrors errors = new ServiceErrorsImpl();
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedFailedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));

		when(cmsService.getContent(ValidationErrorCode.PWD_INVALID)).thenReturn("Please enter a new password that meets the required guidelines.");
		ReflectionTestUtils.setField(gesbcpmService, "cmsService", cmsService);

		CustomerCredentialManagementInformation customerCredentialManagementInformation = gesbcpmService.updatePassword(passwordUpdateRequest, errors);
		assertThat(customerCredentialManagementInformation, notNullValue());
		assertThat(customerCredentialManagementInformation.getServiceLevel().toString(), Is.is("ERROR"));
		assertThat(customerCredentialManagementInformation.getServiceStatusErrorCode(), Is.is("pwdPolicyValidationFault"));
		assertThat(customerCredentialManagementInformation.getServiceNegativeResponse(), Is.is("errorInReg.Message"));
		assertThat(errors.getErrorList().iterator().next().getErrorCode(), Is.is("309,00012"));
		assertThat(errors.getErrorList().iterator().next().getReason(), Is.is("Please enter a new password that meets the required guidelines."));
		assertThat(errors.getErrorList().iterator().next().getType(), Is.is("pwdPolicyValidationFault"));
		assertThat(errors.getErrorList().iterator().next().getService(), Is.is("Group-ESB MaintainChannelAccess Service (svc0247)"));
	}
	
}
