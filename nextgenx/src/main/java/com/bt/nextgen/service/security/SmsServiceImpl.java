package com.bt.nextgen.service.security;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import au.com.rsa.ps.smsotp.SMSOTPAuthenticationResponse;
import com.rsa.csd.ws.*;

import com.bt.nextgen.api.safi.model.Event;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.rsa.csd.ws.ActionCode;
import com.rsa.csd.ws.AnalyzeRequest;
import com.rsa.csd.ws.AnalyzeResponse;
import com.rsa.csd.ws.AnalyzeResponseType;
import com.rsa.csd.ws.AnalyzeType;
import com.rsa.csd.ws.AuthenticateRequest;
import com.rsa.csd.ws.AuthenticateResponse;
import com.rsa.csd.ws.AuthenticateResponseType;
import com.rsa.csd.ws.AuthenticateType;
import com.rsa.csd.ws.ChallengeRequest;
import com.rsa.csd.ws.ChallengeResponse;
import com.rsa.csd.ws.ChallengeResponseType;
import com.rsa.csd.ws.ChallengeType;
import com.rsa.csd.ws.ObjectFactory;
import com.rsa.csd.ws.RequestType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SmsServiceImpl implements SmsService
{
	private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
	
	@Autowired
	private WebServiceProvider provider;
	
	@Autowired
	private SmsHelperService smsHelperService;
	
	@Override
	public String notifyUser() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryUser() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createUser() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String queryAuthStatus() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateUser() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	//From Cash 4
	public String getSessionId() throws Exception
	{
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return session.getId();
	}
	
	//From Cash 4
	private String getTransactionId() throws Exception
	{
		return UUID.randomUUID().toString();
	}
	/**
	 */
	@Override
	public boolean analyzeFromSafi(EventModel eventModel) 
	{
		ObjectFactory of = new ObjectFactory();
		AnalyzeRequest arequest = of.createAnalyzeRequest();
		AnalyzeType request = of.createAnalyzeType();
		
		arequest.setActionTypeList(smsHelperService.setAnalyzeActionTypeListRequest());
		arequest.setIdentificationData(smsHelperService.setAnalyzeIdentificationData());
		arequest.setMessageHeader(smsHelperService.setMessageHeader(RequestType.ANALYZE));
		arequest.setSecurityHeader(smsHelperService.setSecurityHeader());
		arequest.setDeviceRequest(smsHelperService.setAnalyzeDeviceRequest());
		
		request.setRequest(arequest);
		
		AnalyzeResponseType response = (AnalyzeResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		AnalyzeResponse responseData = response.getAnalyzeReturn();
		ActionCode action = responseData.getRiskResult().getTriggeredRule().getActionCode();

		switch (action)
		{
			case ALLOW:
				return false;
			case CHALLENGE:
				return true;
			case ELEVATE_SECURITY:
				if (eventModel.getClientDefinedEventType().equalsIgnoreCase(
					Event.ADD_PAYEE.value()) || eventModel.getClientDefinedEventType().equalsIgnoreCase(
					Event.PAYMENT.value()))
				{
					return false;
				}
				else
				{
					return true;
				}
			default:
				return false;
		}
	}
	
	@Override
	public boolean sendSmsCodeFromSafi() throws Exception 
	{
		ObjectFactory of = new ObjectFactory();
		ChallengeRequest arequest = of.createChallengeRequest();
		ChallengeType request = of.createChallengeType();
		arequest.setDeviceRequest(smsHelperService.setDeviceRequest());
		arequest.setIdentificationData(smsHelperService.setIdentificationData());
		arequest.setMessageHeader(smsHelperService.setMessageHeader(RequestType.CHALLENGE));
		arequest.setSecurityHeader(smsHelperService.setSecurityHeader());
		arequest.setCredentialChallengeRequestList(smsHelperService.setCredentialChallengeRequestList());
		request.setRequest(arequest);
		
		ChallengeResponseType response = (ChallengeResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		ChallengeResponse responseData = response.getChallengeReturn();
		String statusCode = String.valueOf(responseData.getCredentialChallengeList().getAcspChallengeResponseData().getCallStatus().getStatusCode());
		if(statusCode.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
		{
			return true;
		}
		else
		{
			return false;
		}
		//String deliveryStatus = String.valueOf(responseData.getCredentialChallengeList().getAcspChallengeResponseData().getPayload());
	}
	
	@Override
	public boolean authenticateSmsCodeFromSafi(String smsCode) throws Exception 
	{
		ObjectFactory of = new ObjectFactory();
		AuthenticateRequest arequest = of.createAuthenticateRequest();
		arequest.setIdentificationData(smsHelperService.setIdentificationData());
		arequest.setMessageHeader(smsHelperService.setMessageHeader(RequestType.AUTHENTICATE));
		arequest.setSecurityHeader(smsHelperService.setSecurityHeader());
		arequest.setCredentialDataList(smsHelperService.setCredentialDataList(smsCode));
		AuthenticateType request = of.createAuthenticateType();
		request.setRequest(arequest);
		
		AuthenticateResponseType response = (AuthenticateResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		AuthenticateResponse responseData = response.getAuthenticateReturn();
		String statusCode = String.valueOf(responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getCallStatus().getStatusCode());
		if(statusCode.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
		{
			return true;
		}
		else
		{
			return false;
		}
		//SMSOTPAuthenticationResponse resultCode = (SMSOTPAuthenticationResponse) responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getPayload();
		//System.out.println("SmsServiceImpl.sendSmsCodeFromSafi()::::"+resultCode.getResultCode());
	}
	
	/**
	 */
	@Override
	public SafiAnalyzeResult analyzeFromSafi(EventModel eventModel, HttpRequestParams requestParams) 
	{
		ObjectFactory of = new ObjectFactory();
		AnalyzeRequest arequest = of.createAnalyzeRequest();
		AnalyzeType request = of.createAnalyzeType();
		String devicePrint = "";
		String clientSessionId = "";
		String clientTransactionId = "";
		
		try
		{
			devicePrint = eventModel.getDeviceToken();
			clientSessionId = getSessionId();
			clientTransactionId = getTransactionId();
		}
		catch (Exception e)
		{
			logger.warn("Unable to decode deviceToken from user", e);
		}
		
		arequest.setActionTypeList(smsHelperService.setAnalyzeActionTypeListRequest());
		arequest.setIdentificationData(smsHelperService.setAnalyzeIdentificationData(clientSessionId, clientTransactionId));
		arequest.setMessageHeader(smsHelperService.setMessageHeader(RequestType.ANALYZE));
		arequest.setSecurityHeader(smsHelperService.setSecurityHeader());
		arequest.setDeviceRequest(smsHelperService.setAnalyzeDeviceRequest(requestParams, devicePrint));
		arequest.setRunRiskType(smsHelperService.setRunRiskType());
		arequest.setChannelIndicator(smsHelperService.setChannelIndicatorType());		
		arequest.setEventDataList(smsHelperService.setEventDataList(eventModel));
		
		request.setRequest(arequest);		
		
		AnalyzeResponseType response = (AnalyzeResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		AnalyzeResponse responseData = response.getAnalyzeReturn();
		ActionCode action = responseData.getRiskResult().getTriggeredRule().getActionCode();
		
		
		SafiAnalyzeResult result = new SafiAnalyzeResult();		
		result.setIdentificationData(responseData.getIdentificationData());
		result.setTransactionId(clientTransactionId);	
		result.setDeviceToken(responseData.getDeviceResult().getDeviceData().getDeviceTokenCookie());
		result.setDevicePrint(devicePrint);
		
		boolean actionCode = false;
		
		switch (action)
		{
			case ALLOW:
				actionCode = false;
				break;
			case CHALLENGE:
				actionCode = true;
				break;
			default:
				actionCode = false;
		}
		
		result.setActionCode(actionCode);
		
		return result;
	}
	
	
	/**
	 * Retrieve client session id
	 * @return
	 * @throws Exception
	 */
	public String getClientSessionId() throws Exception
	{
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}
	
	
	@Override
	public SafiAnalyzeResult sendSmsCodeFromSafi(SafiAnalyzeResult safiResponse, HttpRequestParams requestParams, String... userName) throws Exception 
	{
		String devicePrint = "";
		String clientSessionId = "";
		String clientTransactionId = "";
				
		ObjectFactory of = new ObjectFactory();
		ChallengeRequest arequest = of.createChallengeRequest();
		ChallengeType request = of.createChallengeType();
		arequest.setDeviceRequest(smsHelperService.setChallengeDeviceRequest(requestParams, safiResponse.getDevicePrint(), safiResponse.getDeviceToken()));
		arequest.setIdentificationData(smsHelperService.setIdentificationData((safiResponse.getIdentificationData().getSessionId()), safiResponse.getIdentificationData().getTransactionId()));
		arequest.setMessageHeader(smsHelperService.setMessageHeader(RequestType.CHALLENGE));
		arequest.setSecurityHeader(smsHelperService.setSecurityHeader());
		arequest.setCredentialChallengeRequestList(smsHelperService.setCredentialChallengeRequestList(safiResponse.getTransactionId()));
		request.setRequest(arequest);		
		
		ChallengeResponseType response = (ChallengeResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		ChallengeResponse responseData = response.getChallengeReturn();
		String statusCode = String.valueOf(responseData.getCredentialChallengeList().getAcspChallengeResponseData().getCallStatus().getStatusCode());
		
		SafiAnalyzeResult result = safiResponse;		
		result.setIdentificationData(responseData.getIdentificationData());
				
		boolean actionCode = false;
		
		if(statusCode.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
		{
			actionCode = true;
		}
		else
		{
			actionCode = false;
		}
		
		result.setActionCode(actionCode);
		return result;
		//String deliveryStatus = String.valueOf(responseData.getCredentialChallengeList().getAcspChallengeResponseData().getPayload());
	}
	
	
	/**
	 * Where a custom SAML token (application level) is required, then use this method.
	 * @param smsCode
	 * @param requestParams
	 * @param safiResponse
	 * @param samlToken
	 * @return
	 * @throws Exception
	 */
	public SafiAuthenticateResult authenticateSmsCodeFromSafi(String smsCode, HttpRequestParams requestParams, SafiAnalyzeResult safiResponse, String samlToken) throws Exception 
	{		
		String sessionId = "";
		String transactionId = "";
		String devicePrint = "";
		String deviceToken = "";
		String customerId = "";
		
		if (safiResponse != null)
		{
			sessionId = safiResponse.getIdentificationData().getSessionId();
			transactionId = safiResponse.getIdentificationData().getTransactionId();
			devicePrint = safiResponse.getDevicePrint();
			deviceToken = safiResponse.getDeviceToken();
			customerId = safiResponse.getIdentificationData().getUserName();
		}
		
		AuthenticateType request = createAuthenticationRequestMessage(smsCode, requestParams, safiResponse, devicePrint, deviceToken, sessionId, transactionId, customerId, samlToken);		
		AuthenticateResponseType response = (AuthenticateResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		AuthenticateResponse responseData = response.getAuthenticateReturn();
		String statusCode = String.valueOf(responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getCallStatus().getStatusCode());
		SMSOTPAuthenticationResponse payload = (SMSOTPAuthenticationResponse) responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getPayload();
		IdentificationData identificationData = (IdentificationData) responseData.getIdentificationData();

        SafiAuthenticateResult result = new SafiAuthenticateResult();

        result.setUsername(identificationData.getUserName());

		if(payload.getResultCode().toString().equalsIgnoreCase("ACCESS_OK"))
		{
			result.setSuccessFlag(true);
		}
		else
		{
			result.setSuccessFlag(false);
		}
		
		result.setStatusCode(payload.getResultCode().toString());
		return result;
	}
	
	
	
	@Override
	public SafiAuthenticateResult authenticateSmsCodeFromSafi(String smsCode, HttpRequestParams requestParams, SafiAnalyzeResult safiResponse) throws Exception 
	{		
		String sessionId = "";
		String transactionId = "";
		String devicePrint = "";
		String deviceToken = "";
		String customerId = "";
		
		if (safiResponse != null)
		{
			sessionId = safiResponse.getIdentificationData().getSessionId();
			transactionId = safiResponse.getIdentificationData().getTransactionId();
			devicePrint = safiResponse.getDevicePrint();
			deviceToken = safiResponse.getDeviceToken();
			customerId = safiResponse.getIdentificationData().getUserName();
		}
		
		AuthenticateType request = createAuthenticationRequestMessage(smsCode, requestParams, safiResponse, devicePrint, deviceToken, sessionId, transactionId, customerId);		
		AuthenticateResponseType response = (AuthenticateResponseType) provider.sendWebService(Attribute.SAFI_KEY, request);
		
		AuthenticateResponse responseData = response.getAuthenticateReturn();
		String statusCode = String.valueOf(responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getCallStatus().getStatusCode());
		SMSOTPAuthenticationResponse payload = (SMSOTPAuthenticationResponse) responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getPayload();
				
		SafiAuthenticateResult result = new SafiAuthenticateResult();
		
		if(payload.getResultCode().toString().equalsIgnoreCase("ACCESS_OK"))
		{
			result.setSuccessFlag(true);
		}
		else
		{
			result.setSuccessFlag(false);
		}
		
		result.setStatusCode(payload.getResultCode().toString());
		return result;
	}
	
	
	
	private AuthenticateType createAuthenticationRequestMessage(String smsCode, HttpRequestParams requestParams, SafiAnalyzeResult safiResponse, 
																String devicePrint, String deviceToken, String sessionId, 
																String transactionId, String customerId ) throws Exception
	{
		ObjectFactory of = new ObjectFactory();
		AuthenticateRequest arequest = of.createAuthenticateRequest();
		arequest.setActionTypeList(smsHelperService.setAnalyzeActionTypeListRequest());
		arequest.setDeviceRequest(smsHelperService.setAuthenticateDeviceRequest(requestParams, devicePrint, deviceToken));
		arequest.setIdentificationData(smsHelperService.setIdentificationData(sessionId, transactionId, customerId));
		arequest.setMessageHeader(smsHelperService.setMessageHeader(RequestType.AUTHENTICATE));
		arequest.setSecurityHeader(smsHelperService.setSecurityHeader());
		arequest.setCredentialDataList(smsHelperService.setCredentialDataList(smsCode, safiResponse));
		
		AuthenticateType request = of.createAuthenticateType();
		request.setRequest(arequest);
				
		return request;
	}
	
	
	private AuthenticateType createAuthenticationRequestMessage(String smsCode, HttpRequestParams requestParams, SafiAnalyzeResult safiResponse, 
			String devicePrint, String deviceToken, String sessionId, 
			String transactionId, String customerId, String samlToken ) throws Exception
	{
		AuthenticateType authenticateType = createAuthenticationRequestMessage(smsCode, requestParams, safiResponse, devicePrint, deviceToken, sessionId, transactionId, customerId);
		authenticateType.getRequest().setCredentialDataList(smsHelperService.setCredentialDataList(smsCode, safiResponse, samlToken, getTransactionId()));
		return authenticateType;		
	}
}
