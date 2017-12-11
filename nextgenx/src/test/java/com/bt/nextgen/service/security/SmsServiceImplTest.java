package com.bt.nextgen.service.security;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;

import javax.servlet.http.HttpSession;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import au.com.rsa.ps.smsotp.SMSOTPAuthenticationRequest;
import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.rsa.csd.ws.APIType;
import com.rsa.csd.ws.AcspAuthenticationRequest;
import com.rsa.csd.ws.AcspAuthenticationRequestData;
import com.rsa.csd.ws.AcspAuthenticationResponseData;
import com.rsa.csd.ws.AcspChallengeRequest;
import com.rsa.csd.ws.AcspChallengeRequestData;
import com.rsa.csd.ws.AcspChallengeResponseData;
import com.rsa.csd.ws.ActionCode;
import com.rsa.csd.ws.AnalyzeResponse;
import com.rsa.csd.ws.AnalyzeResponseType;
import com.rsa.csd.ws.AnalyzeType;
import com.rsa.csd.ws.AuthenticateResponse;
import com.rsa.csd.ws.AuthenticateResponseType;
import com.rsa.csd.ws.AuthenticateType;
import com.rsa.csd.ws.AuthorizationMethod;
import com.rsa.csd.ws.CallStatus;
import com.rsa.csd.ws.ChallengeResponse;
import com.rsa.csd.ws.ChallengeResponseType;
import com.rsa.csd.ws.ChallengeType;
import com.rsa.csd.ws.CredentialAuthResultList;
import com.rsa.csd.ws.CredentialChallengeList;
import com.rsa.csd.ws.CredentialChallengeRequestList;
import com.rsa.csd.ws.CredentialDataList;
import com.rsa.csd.ws.DeviceRequest;
import com.rsa.csd.ws.GenericActionType;
import com.rsa.csd.ws.GenericActionTypeList;
import com.rsa.csd.ws.IdentificationData;
import com.rsa.csd.ws.MessageHeader;
import com.rsa.csd.ws.RequestType;
import com.rsa.csd.ws.RiskResult;
import com.rsa.csd.ws.SecurityHeader;
import com.rsa.csd.ws.TriggeredRule;
import com.rsa.csd.ws.UserStatus;
import com.rsa.csd.ws.WSUserType;

@RunWith(MockitoJUnitRunner.class)
public class SmsServiceImplTest
{
	@InjectMocks
	SmsService smsService = new SmsServiceImpl();
	
	@Mock
	private SmsHelperService smsHelperService;
	@Mock
	private WebServiceProvider provider;
	
	@Mock
	private SafiAnalyzeResult safiAnalyzeResult;
	@Mock
	private HttpSession session;

	@Before
	public void setUp() throws Exception
	{
		Mockito.doAnswer(new Answer<GenericActionTypeList>() {
			@Override
			public GenericActionTypeList answer(InvocationOnMock invocation) throws Throwable {
				GenericActionTypeList genericActionTypeList = new GenericActionTypeList();
				GenericActionType genericActionType = GenericActionType.SET_USER_STATUS;
				genericActionTypeList.getGenericActionTypes().add(genericActionType);
				return genericActionTypeList;
			}
			
		}).when(smsHelperService).setAnalyzeActionTypeListRequest();

		Mockito.doAnswer(new Answer<IdentificationData>() {
			@Override
			public IdentificationData answer(InvocationOnMock invocation) throws Throwable {
				IdentificationData identificationData = new IdentificationData();
				identificationData.setClientSessionId("");
				identificationData.setClientTransactionId("");
				identificationData.setOrgName("WPACBTNG");
				identificationData.setUserName("");
				identificationData.setUserStatus(UserStatus.VERIFIED);
				identificationData.setUserType(WSUserType.PERSISTENT);
				return identificationData;
			}
			
		}).when(smsHelperService).setAnalyzeIdentificationData();

		Mockito.doAnswer(new Answer<MessageHeader>() {
			@Override
			public MessageHeader answer(InvocationOnMock invocation) throws Throwable {
				MessageHeader messageHeader = new MessageHeader();
				messageHeader.setApiType(APIType.DIRECT_SOAP_API);
				messageHeader.setRequestType(RequestType.ANALYZE);
				messageHeader.setVersion("6.0");
				return messageHeader;
			}
			
		}).when(smsHelperService).setMessageHeader(isA(RequestType.class));

		Mockito.doAnswer(new Answer<SecurityHeader>() {
			@Override
			public SecurityHeader answer(InvocationOnMock invocation) throws Throwable {
				SecurityHeader securityHeader = new SecurityHeader();
				securityHeader.setCallerCredential("123");
				securityHeader.setCallerId("12");
				securityHeader.setMethod(AuthorizationMethod.PASSWORD);
				return securityHeader;

			}
			
		}).when(smsHelperService).setSecurityHeader();

		Mockito.doAnswer(new Answer<DeviceRequest>() {
			@Override
			public DeviceRequest answer(InvocationOnMock invocation) throws Throwable {
				return Mockito.mock(DeviceRequest.class);

			}
			
		}).when(smsHelperService).setAnalyzeDeviceRequest();
	
		Mockito.doAnswer(new Answer<DeviceRequest>() {
			@Override
			public DeviceRequest answer(InvocationOnMock invocation) throws Throwable {
				return Mockito.mock(DeviceRequest.class);

			}
			
		}).when(smsHelperService).setDeviceRequest();
		
		Mockito.doAnswer(new Answer<IdentificationData>() {
			@Override
			public IdentificationData answer(InvocationOnMock invocation) throws Throwable {
				IdentificationData identificationData = new IdentificationData();
				identificationData.setOrgName("");
				identificationData.setSessionId("");
				identificationData.setTransactionId("");
				identificationData.setUserName("");
				identificationData.setUserType(WSUserType.PERSISTENT);
				return identificationData;
			}
			
		}).when(smsHelperService).setIdentificationData("", null);
		
		Mockito.doAnswer(new Answer<CredentialChallengeRequestList>() {
			@Override
			public CredentialChallengeRequestList answer(InvocationOnMock invocation) throws Throwable {
				CredentialChallengeRequestList credentialChallengeRequestList = new CredentialChallengeRequestList();
				AcspChallengeRequestData acspChallengeRequestData = new AcspChallengeRequestData();
				SMSOTPChallengeRequest sMSOTPChallengeRequest = new SMSOTPChallengeRequest();
				sMSOTPChallengeRequest.setDeviceId("");
				sMSOTPChallengeRequest.setNetworkId("");
				sMSOTPChallengeRequest.setInitDevice(true);
				sMSOTPChallengeRequest.setTransactionId("");
				sMSOTPChallengeRequest.setOrganisationId("");
				sMSOTPChallengeRequest.setBrandSilo("");
				sMSOTPChallengeRequest.setRequestingUserId("");
				sMSOTPChallengeRequest.setMessageText("");
				sMSOTPChallengeRequest.setSamlAssertion("");
				
				AcspChallengeRequest payload = sMSOTPChallengeRequest;
				acspChallengeRequestData.setPayload(payload);
				credentialChallengeRequestList.setAcspChallengeRequestData(acspChallengeRequestData);
				
				return credentialChallengeRequestList;
			}
			
		}).when(smsHelperService).setCredentialChallengeRequestList(anyString());
		
	}

	@Test
	public void testAnalyzeFromSafi_Allow() throws Exception
	{
		Mockito.doAnswer(new Answer<AnalyzeResponseType>() {
			@Override
			public AnalyzeResponseType answer(InvocationOnMock invocation) throws Throwable {
				AnalyzeResponseType response = new AnalyzeResponseType();
				AnalyzeResponse analyzeResponse = new AnalyzeResponse();
				ActionCode actionCode= ActionCode.ALLOW;
				TriggeredRule triggeredRule = new TriggeredRule();
				triggeredRule.setActionCode(actionCode);
				RiskResult riskResult = new RiskResult();
				riskResult.setTriggeredRule(triggeredRule);
				analyzeResponse.setRiskResult(riskResult);
				response.setAnalyzeReturn(analyzeResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(AnalyzeType.class));
		//SafiAnalyzeResult  value = smsService.analyzeFromSafi(new EventModel(), getHttpRequestParams());
		//assertThat(value,Is.is(false));
	}
	
	@Test
	public void testAnalyzeFromSafi_Challenge() throws Exception
	{
		Mockito.doAnswer(new Answer<AnalyzeResponseType>() {
			@Override
			public AnalyzeResponseType answer(InvocationOnMock invocation) throws Throwable {
				AnalyzeResponseType response = new AnalyzeResponseType();
				AnalyzeResponse analyzeResponse = new AnalyzeResponse();
				ActionCode actionCode= ActionCode.CHALLENGE;
				TriggeredRule triggeredRule = new TriggeredRule();
				triggeredRule.setActionCode(actionCode);
				RiskResult riskResult = new RiskResult();
				riskResult.setTriggeredRule(triggeredRule);
				analyzeResponse.setRiskResult(riskResult);
				response.setAnalyzeReturn(analyzeResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(AnalyzeType.class));
		boolean value = smsService.analyzeFromSafi(new EventModel());
		assertThat(value,Is.is(true));
	}
	
	@Test
	public void testAnalyzeFromSafi_Default() throws Exception
	{
		Mockito.doAnswer(new Answer<AnalyzeResponseType>() {
			@Override
			public AnalyzeResponseType answer(InvocationOnMock invocation) throws Throwable {
				AnalyzeResponseType response = new AnalyzeResponseType();
				AnalyzeResponse analyzeResponse = new AnalyzeResponse();
				ActionCode actionCode= ActionCode.COLLECT;
				TriggeredRule triggeredRule = new TriggeredRule();
				triggeredRule.setActionCode(actionCode);
				RiskResult riskResult = new RiskResult();
				riskResult.setTriggeredRule(triggeredRule);
				analyzeResponse.setRiskResult(riskResult);
				response.setAnalyzeReturn(analyzeResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(AnalyzeType.class));
		boolean value = smsService.analyzeFromSafi(new EventModel());
		assertThat(value,Is.is(false));
	}

	public void testAnalyzeFromSafi_Default_1() throws Exception
	{
		EventModel eventModel = Mockito.mock(EventModel.class);
		Mockito.when(safiAnalyzeResult.getActionCode()).thenReturn(true);
		SmsServiceImpl smsServiceImpl = Mockito.mock(SmsServiceImpl.class);
		Mockito.when(smsService.analyzeFromSafi(eventModel, getHttpRequestParams())).thenReturn(safiAnalyzeResult);
		SafiAnalyzeResult value = smsService.analyzeFromSafi(eventModel, getHttpRequestParams());
		//assertThat(value,Is.is(false));
	}
	
	@Test
	public void testSendSmsCodeFromSafi_Success() throws Exception
	{
		Mockito.doAnswer(new Answer<MessageHeader>() {
			@Override
			public MessageHeader answer(InvocationOnMock invocation) throws Throwable {
				MessageHeader messageHeader = new MessageHeader();
				messageHeader.setApiType(APIType.DIRECT_SOAP_API);
				messageHeader.setRequestType(RequestType.CHALLENGE);
				messageHeader.setVersion("6.0");
				return messageHeader;
			}
			
		}).when(smsHelperService).setMessageHeader(isA(RequestType.class));
		
		Mockito.doAnswer(new Answer<ChallengeResponseType>() {
			@Override
			public ChallengeResponseType answer(InvocationOnMock invocation) throws Throwable {
				ChallengeResponseType response = new ChallengeResponseType();
				ChallengeResponse challengeResponse = new ChallengeResponse();
				CallStatus callStatus = new CallStatus();
				callStatus.setStatusCode(Attribute.SUCCESS_MESSAGE);
				AcspChallengeResponseData acspChallengeResponseData = new AcspChallengeResponseData();
				acspChallengeResponseData.setCallStatus(callStatus);
				CredentialChallengeList credentialChallengeList = new  CredentialChallengeList();
				credentialChallengeList.setAcspChallengeResponseData(acspChallengeResponseData);
				challengeResponse.setCredentialChallengeList(credentialChallengeList);
				response.setChallengeReturn(challengeResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(ChallengeType.class));
		boolean value = smsService.sendSmsCodeFromSafi();
		assertThat(value,Is.is(true));
		/*SafiAnalyzeResult result = Mockito.mock(SafiAnalyzeResult.class);
		Mockito.when(safiAnalyzeResult.getActionCode()).thenReturn(true);
		boolean value = smsService.sendSmsCodeFromSafi(null, getHttpRequestParams()).getActionCode();
		assertThat(value,Is.is(true));*/
	}
	
	@Test
	public void testSendSmsCodeFromSafi_Failure() throws Exception
	{
		Mockito.doAnswer(new Answer<MessageHeader>() {
			@Override
			public MessageHeader answer(InvocationOnMock invocation) throws Throwable {
				MessageHeader messageHeader = new MessageHeader();
				messageHeader.setApiType(APIType.DIRECT_SOAP_API);
				messageHeader.setRequestType(RequestType.CHALLENGE);
				messageHeader.setVersion("6.0");
				return messageHeader;
			}
			
		}).when(smsHelperService).setMessageHeader(isA(RequestType.class));
		
		Mockito.doAnswer(new Answer<ChallengeResponseType>() {
			@Override
			public ChallengeResponseType answer(InvocationOnMock invocation) throws Throwable {
				ChallengeResponseType response = new ChallengeResponseType();
				ChallengeResponse challengeResponse = new ChallengeResponse();
				String statusCode = "Failure";
				CallStatus callStatus = new CallStatus();
				callStatus.setStatusCode(statusCode);
				AcspChallengeResponseData acspChallengeResponseData = new AcspChallengeResponseData();
				acspChallengeResponseData.setCallStatus(callStatus);
				CredentialChallengeList credentialChallengeList = new  CredentialChallengeList();
				credentialChallengeList.setAcspChallengeResponseData(acspChallengeResponseData);
				challengeResponse.setCredentialChallengeList(credentialChallengeList);
				response.setChallengeReturn(challengeResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(ChallengeType.class));
		boolean value = smsService.sendSmsCodeFromSafi();
		assertThat(value,Is.is(false));
		/*SafiAnalyzeResult result = Mockito.mock(SafiAnalyzeResult.class);
		Mockito.when(safiAnalyzeResult.getActionCode()).thenReturn(true);
		boolean value = smsService.sendSmsCodeFromSafi(null, getHttpRequestParams()).getActionCode();
		assertThat(value,Is.is(false));*/
	}

	
	@Test
	public void testAuthenticateSmsCodeFromSafi_Success() throws Exception
	{
		Mockito.doAnswer(new Answer<MessageHeader>() {
			@Override
			public MessageHeader answer(InvocationOnMock invocation) throws Throwable {
				MessageHeader messageHeader = new MessageHeader();
				messageHeader.setApiType(APIType.DIRECT_SOAP_API);
				messageHeader.setRequestType(RequestType.AUTHENTICATE);
				messageHeader.setVersion("6.0");
				return messageHeader;
			}
			
		}).when(smsHelperService).setMessageHeader(isA(RequestType.class));

		Mockito.doAnswer(new Answer<CredentialDataList>() {
			@Override
			public CredentialDataList answer(InvocationOnMock invocation) throws Throwable {
				CredentialDataList CredentialDataList = new CredentialDataList();
				AcspAuthenticationRequestData acspAuthenticationRequestData = new AcspAuthenticationRequestData();
				SMSOTPAuthenticationRequest sMSOTPAuthenticationRequest = new SMSOTPAuthenticationRequest();
				sMSOTPAuthenticationRequest.setSmsOTP("123");
				AcspAuthenticationRequest acspAuthenticationRequest = sMSOTPAuthenticationRequest;
				acspAuthenticationRequestData.setPayload(acspAuthenticationRequest);
				CredentialDataList.setAcspAuthenticationRequestData(acspAuthenticationRequestData);
				
				return CredentialDataList;
			}
			
		}).when(smsHelperService).setCredentialDataList(anyString(), any(SafiAnalyzeResult.class));

		
		Mockito.doAnswer(new Answer<AuthenticateResponseType>() {
			@Override
			public AuthenticateResponseType answer(InvocationOnMock invocation) throws Throwable {
				AuthenticateResponseType response = new AuthenticateResponseType();
				
				AuthenticateResponse authenticateResponse = new AuthenticateResponse();
				CallStatus callStatus = new CallStatus();
				callStatus.setStatusCode(Attribute.SUCCESS_MESSAGE);
				AcspAuthenticationResponseData acspAuthenticationResponseData = new AcspAuthenticationResponseData();
				acspAuthenticationResponseData.setCallStatus(callStatus);
				CredentialAuthResultList credentialAuthResultList = new  CredentialAuthResultList();
				credentialAuthResultList.setAcspAuthenticationResponseData(acspAuthenticationResponseData);
				authenticateResponse.setCredentialAuthResultList(credentialAuthResultList);
				response.setAuthenticateReturn(authenticateResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(AuthenticateType.class));
		SafiAnalyzeResult result = Mockito.mock(SafiAnalyzeResult.class);
		
		Mockito.when(safiAnalyzeResult.getActionCode()).thenReturn(true);
		IdentificationData identData = new IdentificationData();
		identData.setClientSessionId("CS-345-323-252-334-324");
		safiAnalyzeResult.setIdentificationData(identData);

		/*SafiAuthenticateResult value = smsService.authenticateSmsCodeFromSafi("123", getHttpRequestParams(), safiAnalyzeResult);
		assertThat(value.isSuccessFlag(),Is.is(true));*/
		boolean value = smsService.authenticateSmsCodeFromSafi("123");
		assertThat(value,Is.is(true));
	}

	@Test
	public void testAuthenticateSmsCodeFromSafi_Failure() throws Exception
	{
		Mockito.doAnswer(new Answer<MessageHeader>() {
			@Override
			public MessageHeader answer(InvocationOnMock invocation) throws Throwable {
				MessageHeader messageHeader = new MessageHeader();
				messageHeader.setApiType(APIType.DIRECT_SOAP_API);
				messageHeader.setRequestType(RequestType.AUTHENTICATE);
				messageHeader.setVersion("6.0");
				return messageHeader;
			}
			
		}).when(smsHelperService).setMessageHeader(isA(RequestType.class));

		Mockito.doAnswer(new Answer<CredentialDataList>() {
			@Override
			public CredentialDataList answer(InvocationOnMock invocation) throws Throwable {
				CredentialDataList CredentialDataList = new CredentialDataList();
				AcspAuthenticationRequestData acspAuthenticationRequestData = new AcspAuthenticationRequestData();
				SMSOTPAuthenticationRequest sMSOTPAuthenticationRequest = new SMSOTPAuthenticationRequest();
				sMSOTPAuthenticationRequest.setSmsOTP("123");
				AcspAuthenticationRequest acspAuthenticationRequest = sMSOTPAuthenticationRequest;
				acspAuthenticationRequestData.setPayload(acspAuthenticationRequest);
				CredentialDataList.setAcspAuthenticationRequestData(acspAuthenticationRequestData);
				
				return CredentialDataList;
			}
			
		}).when(smsHelperService).setCredentialDataList(anyString(), any(SafiAnalyzeResult.class));

		
		Mockito.doAnswer(new Answer<AuthenticateResponseType>() {
			@Override
			public AuthenticateResponseType answer(InvocationOnMock invocation) throws Throwable {
				AuthenticateResponseType response = new AuthenticateResponseType();
				
				AuthenticateResponse authenticateResponse = new AuthenticateResponse();
				String statusCode = "Failure";
				CallStatus callStatus = new CallStatus();
				callStatus.setStatusCode(statusCode);
				AcspAuthenticationResponseData acspAuthenticationResponseData = new AcspAuthenticationResponseData();
				acspAuthenticationResponseData.setCallStatus(callStatus);
				CredentialAuthResultList credentialAuthResultList = new  CredentialAuthResultList();
				credentialAuthResultList.setAcspAuthenticationResponseData(acspAuthenticationResponseData);
				authenticateResponse.setCredentialAuthResultList(credentialAuthResultList);
				response.setAuthenticateReturn(authenticateResponse);
				return response;

			}
			
		}).when(provider).sendWebService(anyString(),isA(AuthenticateType.class));
		boolean value = smsService.authenticateSmsCodeFromSafi("123");
		assertThat(value,Is.is(false));
		/*SafiAnalyzeResult result = Mockito.mock(SafiAnalyzeResult.class);
		Mockito.when(safiAnalyzeResult.getActionCode()).thenReturn(true);
		SafiAuthenticateResult value = smsService.authenticateSmsCodeFromSafi("123", getHttpRequestParams(), safiAnalyzeResult);
		assertThat(value.isSuccessFlag(),Is.is(false));*/
	}
	
	public HttpRequestParams getHttpRequestParams()
 	{
 		HttpRequestParams requestParams = new HttpRequestParams();
 		requestParams.setHttpAccept("text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
 		requestParams.setHttpAcceptChars("ISO-8859-1,utf-8;q=0.7,*;q=0.7");
 		requestParams.setHttpAcceptEncoding("gzip,deflate");
 		requestParams.setHttpAcceptLanguage("en-ua,en;q=0.5");
 		requestParams.setHttpReferrer("http://www.cba.com.uk");
 		requestParams.setHttpUserAgent("Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.04506.30)");
 		requestParams.setHttpOriginatingIpAddress("127.0.0.1");
 		
 		return requestParams;
 	}

}
