package com.bt.nextgen.service.group.customer.groupesb;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseDetailsType;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseMsgType;

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

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RequestedAction;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.login.util.SamlUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.btfin.panorama.core.security.integration.customer.ChannelType;
import com.bt.nextgen.service.group.customer.CredentialRequestModel;
import com.btfin.panorama.core.security.integration.customer.CredentialType;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class GroupEsbCustomerLoginManagementImplTest
{
	@Mock
    private BankingAuthorityService userSamlService;
	
	@Mock
	private WebServiceProvider provider;
	
	private GroupEsbCustomerLoginManagementImpl gesbclmService;
	
	private AvaloqGatewayResponseMsgType response;
	
	private RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialResponse;
	
	private RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialListResponse;
	
	private CorrelatedResponse correlatedResponse;
	
	private CredentialRequestModel credentialRequestModel;
	
	private InputStream stream;
	
	private InputStream listStream;
	
	@Mock
	private UserProfileService profileService;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private static final String responseXML = "<out:retrieveChannelAccessCredentialResponse xmlns:io=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
			"xmlns:io2=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" xmlns:io3=\"http://www.westpac.com.au/gn/utility/xsd/esbHeader/v4/\" " +
			"xmlns:out=\"http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v4/SVC0311/\" " +
			"xmlns:out2=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> " +
			"<sh:serviceStatus xmlns:EAM=\"http://v1.0.eam.entity.olb.westpac.com.au\" xmlns:MVC=\"http://www.westpac.com.au/gn/MediationService/MVC0172/v03\" " +
			"xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\"> <sh:statusInfo> <sh:level>Success</sh:level> <sh:code>000,00000</sh:code> " +
			"</sh:statusInfo> <sh:statusInfo> <sh:level>Warning</sh:level> <sh:code>480,00001</sh:code> " +
			"<sh:description>ReferenceDataLookup returned empty value,ElementName=brand,ElementValue=wpac,Category=BrandSiloCode</sh:description> <sh:referenceId>EAM</sh:referenceId> </sh:statusInfo> " +
			"</sh:serviceStatus> <userCredential> <credentialType>ONL</credentialType> <credentialGroup> <credentialGroupType>bt-investor</credentialGroupType> </credentialGroup> <channel> " +
			"<channelType>Online</channelType> </channel> <userName> <userId>010000705</userId> <userAlias>aUsername</userAlias> </userName> <internalIdentifier> " +
			"<out2:credentialId>44b2aafa-3490-11e3-888b-deadbeef90fe</out2:credentialId> </internalIdentifier> <lastUsedDate>2013-11-13</lastUsedDate> <lastUsedTime>05:23:06Z</lastUsedTime> " +
			"<lifecycleStatus> <status>MIG</status> <startTimestamp>2013-10-14T05:19:58Z</startTimestamp> </lifecycleStatus> <sourceSystem>EAM</sourceSystem> </userCredential><userCredential> " +
			"<credentialType>ONL</credentialType> <credentialGroup> <credentialGroupType>bt-adviser</credentialGroupType> </credentialGroup> <channel> <channelType>Online</channelType> </channel> " +
			"<userName> <userId>30981</userId> <userAlias>dealergroup</userAlias> </userName> <internalIdentifier> <out2:credentialId>44b2aafa-3490-11e3-888b-deadbeef90fe</out2:credentialId> " +
			"</internalIdentifier> <lastUsedDate>2013-11-13</lastUsedDate> <lastUsedTime>05:23:06Z</lastUsedTime> <lifecycleStatus> <status>Active</status> <startTimestamp>2013-10-14T05:19:58Z</startTimestamp> " +
			"</lifecycleStatus> <sourceSystem>EAM</sourceSystem> </userCredential></out:retrieveChannelAccessCredentialResponse>";
	
	private static final String responseListXml = "<out:retrieveChannelAccessCredentialResponse xmlns:io=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:io2=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\" " +
			"xmlns:io3=\"http://www.westpac.com.au/gn/utility/xsd/esbHeader/v4/\" xmlns:out=\"http://www.westpac.com.au/gn/channelManagement/services/credentialManagement/xsd/retrieveChannelAccessCredential/v4/SVC0311/\" " +
			"xmlns:out2=\"http://www.westpac.com.au/gn/common/xsd/identifiers/v1/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> " +
			"<sh:serviceStatus xmlns:EAM=\"http://v1.0.eam.entity.olb.westpac.com.au\" xmlns:MVC=\"http://www.westpac.com.au/gn/MediationService/MVC0172/v03\" " +
			"xmlns:sh=\"http://www.westpac.com.au/gn/utility/xsd/statusHandling/v1/\"> <sh:statusInfo> <sh:level>Success</sh:level> <sh:code>000,00000</sh:code> " +
			"</sh:statusInfo> </sh:serviceStatus> <userCredential> <credentialType>ONL</credentialType> <credentialGroup> <credentialGroupType>bt-adviser</credentialGroupType> " +
			"</credentialGroup> <channel> <channelType>Online</channelType> </channel> <userName> <userId>30981</userId> <userAlias>4743184e868f</userAlias> " +
			"<userAliasCreatedTimestamp>2013-12-17T05:55:13Z</userAliasCreatedTimestamp> </userName> <internalIdentifier> <out2:credentialId>437f4f31-1f82-48e1-816f-8af321ee01e6</out2:credentialId> " +
			"</internalIdentifier> <isCredentialOf> <hasBrandSilo> <brandSiloCode>WPAC</brandSiloCode> </hasBrandSilo> </isCredentialOf> <lifecycleStatus> <status>HAS_TP_PW</status> " +
			"<startTimestamp>2013-12-17T05:55:14Z</startTimestamp> </lifecycleStatus> <lifecycleStatus> <status>SUSP_TP_PW_XP</status> <startTimestamp>2014-02-10T19:12:58Z</startTimestamp> " +
			"</lifecycleStatus> <lifecycleStatus> <status>NOT_YET_ACTV</status> <startTimestamp>2014-12-12T19:12:58Z</startTimestamp> <endTimestamp>2014-12-12T19:12:58Z</endTimestamp> " +
			"</lifecycleStatus> <sourceSystem>EAM</sourceSystem> </userCredential> </out:retrieveChannelAccessCredentialResponse>";
	
	@Before
	public void setUp() throws Exception
	{
		profileService = new UserProfileServiceSpringImpl();
		gesbclmService = new GroupEsbCustomerLoginManagementImpl();
		credentialRequestModel = new CredentialRequestModel();
		credentialRequestModel.setBankReferenceId("30981");
		
		provider = mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(gesbclmService, "provider", provider);
		userSamlService = mock(BankingAuthorityService.class);
		ReflectionTestUtils.setField(gesbclmService, "userSamlService", userSamlService);
		profileService = mock(UserProfileService.class);
		ReflectionTestUtils.setField(gesbclmService, "profileService", profileService);
		Mockito.when(profileService.getGcmId()).thenReturn("30981");
		
		response = AvaloqObjectFactory.getResponseGatewayObjectFactory().createAvaloqGatewayResponseMsgType();
		AvaloqGatewayResponseDetailsType detail = AvaloqObjectFactory.getResponseGatewayObjectFactory()
			.createAvaloqGatewayResponseDetailsType();//AvaloqGatewaySuccessResponseType
		response.setResponseDetails(detail);

		stream = new ByteArrayInputStream(responseXML.getBytes("UTF-8"));
		
		retrieveChannelAccessCredentialResponse =  JaxbUtil.unmarshall(stream, RetrieveChannelAccessCredentialResponse.class);
		stream.close();

		listStream = new ByteArrayInputStream(responseListXml.getBytes("UTF-8"));
		retrieveChannelAccessCredentialListResponse = JaxbUtil.unmarshall(listStream, RetrieveChannelAccessCredentialResponse.class);
		listStream.close();
		correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), retrieveChannelAccessCredentialResponse);
		
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		

		Mockito.doAnswer(new Answer<SamlToken>() {
			@Override
			public SamlToken answer(InvocationOnMock invocation) throws Throwable {
				return 	new SamlToken(SamlUtil.loadSaml());
			}
			
		}).when(userSamlService).getSamlToken();
		
	}

	@Test
	public void testCustomerInformationHavingListOfStatusesResponse()
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		CustomerCredentialInformation customerCredentialInformation = gesbclmService.getCustomerInformation(credentialRequestModel, errors);
		assertThat(customerCredentialInformation, notNullValue());
		assertThat(customerCredentialInformation.getCredentialId(), Is.is("44b2aafa-3490-11e3-888b-deadbeef90fe"));
		assertThat(customerCredentialInformation.getBankReferenceId(), Is.is("30981"));
		assertThat(customerCredentialInformation.getUsername(), Is.is("dealergroup"));
		assertThat(customerCredentialInformation.getPrimaryStatus().getValue(), Is.is("Active"));
		assertThat(customerCredentialInformation.getStartTimeStamp(), Is.is("14 Oct 2013"));
		assertThat(customerCredentialInformation.getLastUsed(), Is.is("13 Nov 2013"));
		assertThat(customerCredentialInformation.getChannelType(), Is.is(ChannelType.UNKNOWN));
		assertThat(customerCredentialInformation.getCredentialType(), Is.is(CredentialType.ONL));
		
	}
	
	@Test
	public void testReqParams()
	{
		RetrieveChannelAccessCredentialRequest request = gesbclmService.createRetrieveChannelAccessCredentialRequest("30981");
		assertThat(request.getUserCredential().getUserName().getUserId(), Is.is("30981"));
		assertThat(request.getRequestedAction().get(0), Is.is(RequestedAction.RETRIEVE_ONLINE_BANKING_STATUS));
	}

	@Test
	public void testcreateRetrieveChannelAccessCredentialRequestV5(){
		au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RetrieveChannelAccessCredentialRequest request =
				gesbclmService.createRetrieveChannelAccessCredentialRequestV5("2016049533");
		assertThat(request.getUserCredential().getUserName().getUserId(), Is.is("2016049533"));
		assertThat(request.getRequestedAction().get(0),
				Is.<au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RequestedAction>is
						(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v5.svc0311.RequestedAction.RETRIEVE_ONLINE_BANKING_STATUS));

	}
	
	@Test
	public void testGetCustomerInformation() 
	{
		ServiceErrors errors = null;
		CustomerCredentialInformation customerCredentialInformation = gesbclmService.getCustomerInformation(credentialRequestModel, errors);
		assertThat(customerCredentialInformation, notNullValue());
		assertThat(customerCredentialInformation.getCredentialId(), Is.is("44b2aafa-3490-11e3-888b-deadbeef90fe"));
		assertThat(customerCredentialInformation.getBankReferenceId(), Is.is("30981"));
		assertThat(customerCredentialInformation.getUsername(), Is.is("dealergroup"));
		assertThat(customerCredentialInformation.getPrimaryStatus().getValue(), Is.is("Active"));
		assertThat(customerCredentialInformation.getStartTimeStamp(), Is.is("14 Oct 2013"));
		assertThat(customerCredentialInformation.getLastUsed(), Is.is("13 Nov 2013"));
		assertThat(customerCredentialInformation.getChannelType(), Is.is(ChannelType.UNKNOWN));
		assertThat(customerCredentialInformation.getCredentialType(), Is.is(CredentialType.ONL));
	}
	
	@Test
	public void testGetCustomerInformationNull()
	{
		Mockito.when(profileService.getGcmId()).thenReturn("123");
		ServiceErrors errors = null;
		credentialRequestModel.setBankReferenceId("123");
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Failed to find a matching credential for Id: 123 in credential response");
		gesbclmService.getCustomerInformation(credentialRequestModel, errors);
	}

	@Test
	public void testGetCustomerInformationGcmIdNull()
	{
		Mockito.when(profileService.getGcmId()).thenReturn(null);
		ServiceErrors errors = null;
		credentialRequestModel.setBankReferenceId(null);
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("No customer ID , cannot find the credential");
		gesbclmService.getCustomerInformation(credentialRequestModel, errors);
	}
	
	@Test
	public void testGetCustomerInformationResponseIsNull()
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
                CorrelationIdWrapper correlationIdWrapper = null;
				return new CorrelatedResponse(correlationIdWrapper, null);
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("The Credential Response was null");
		gesbclmService.getCustomerInformation(credentialRequestModel, errors);
	}
	
	@Test
	public void testGetCustomerInformationServiceStatusIsNull()
	{
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
                CorrelationIdWrapper correlationIdWrapper = null;
				return new CorrelatedResponse(correlationIdWrapper, new RetrieveChannelAccessCredentialResponse());
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("The credential query failed");
		gesbclmService.getCustomerInformation(credentialRequestModel, errors);
	}
	
	@Test
	public void testGetCustomerInformationUserCredentialIsNull()
	{
		final RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialNegativeResponse = new RetrieveChannelAccessCredentialResponse();
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setLevel(Level.SUCCESS);
		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.getStatusInfo().add(statusInfo);
		retrieveChannelAccessCredentialNegativeResponse.setServiceStatus(serviceStatus);
		final CorrelatedResponse correlatedNegativeResponse = new CorrelatedResponse(new CorrelationIdWrapper(), retrieveChannelAccessCredentialNegativeResponse);
		
		Mockito.doAnswer(new Answer<CorrelatedResponse>() {
			@Override
			public CorrelatedResponse answer(InvocationOnMock invocation) throws Throwable {
				return correlatedNegativeResponse;
			}
			
		}).when(provider).sendWebServiceWithSecurityHeaderAndResponseCallback(isA(SamlToken.class), anyString(), anyObject(), any(ServiceErrorsImpl.class));
		
		ServiceErrors errors = null;
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("No user found with this credential");
		gesbclmService.getCustomerInformation(credentialRequestModel, errors);
	}
}
