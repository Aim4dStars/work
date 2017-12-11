package com.bt.nextgen.service.safi;

import au.com.rsa.ps.smsotp.SMSOTPAuthenticationRequest;
import au.com.rsa.ps.smsotp.SMSOTPAuthenticationResponse;
import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.safi.document.*;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.rsa.csd.ws.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class TwoFactorAuthenticationDocumentServiceImpl implements TwoFactorAuthenticationDocumentService {
    @Value("${safi.username}")
    private String callerId;

    @Value("${safi.password}")
    private String callerCredential;
	
	private static final String WEBSERVICE_VERSION = "6.0";
	
	private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationDocumentServiceImpl.class);
	
	@PostConstruct
	public void init()
	{
		if (StringUtils.isEmpty(callerId) || StringUtils.isEmpty(callerCredential))
		{
			throw new IllegalArgumentException("Unable to load username or password for safi from properties files");
		}
	}



	public AnalyzeType createAnalyzeRequest(HttpRequestParams requestParams, String devicePrint, String clientSessionId, String clientTransactionId, Map<String, String> facts, Map<String, String> eventType, String gcmid) 
	{
		DeviceRequest deviceRequest = new DeviceRequestBuilder().setHttpAccept(requestParams.getHttpAccept())
																.setHttpAcceptChars(requestParams.getHttpAcceptChars())
																.setHttpAcceptEncoding(requestParams.getHttpAcceptEncoding())
																.setHttpAcceptLanguage(requestParams.getHttpAcceptLanguage())
																.setHttpReferrer(requestParams.getHttpReferrer())
																.setIpAddress(requestParams.getHttpOriginatingIpAddress())
																.setUserAgent(requestParams.getHttpUserAgent())
																.setDevicePrint(devicePrint)
																.build();
		
		IdentificationData idData = new IdentificationDataBuilder().setOrgName("BTNG")																	
																	.setUserStatus(UserStatus.VERIFIED)
																	.setUserType(WSUserType.PERSISTENT)
																	.setClientSessionIdIdentificationData(clientSessionId)
																	.setClientTransactionIdIdentificationData(clientTransactionId)
																	.setUserName(gcmid)
																	.build();
		
		SecurityHeader securityHeader = new SecurityHeaderBuilder().setCallerCredential(callerCredential)
																	.setCallerId(callerId)
																	.setMethod(AuthorizationMethod.PASSWORD)
																	.build();
		
		MessageHeader messageHeader = new MessageHeaderBuilder().setApiType(APIType.DIRECT_SOAP_API)
																	.forRequestType(RequestType.ANALYZE)
																	.setVersion(WEBSERVICE_VERSION)
																	.build();		
				
		EventDataList eventDataList = new EventDataListBuilder().addFactList(facts)
																.setEventType(eventType)
																.build();
		
		
		
		AnalyzeType request = new AnalyzeRequestBuilder().setAnalyzeActionType(GenericActionType.SET_USER_STATUS)
															.setChannelIndicatorType(ChannelIndicatorType.WEB)
															.setRunRiskType(RunRiskType.ALL)
															.addDeviceRequest(deviceRequest)
															.addEventDataList(eventDataList)
															.addIdentificationData(idData)
															.addMessageHeader(messageHeader)
															.addSecurityHeader(securityHeader).build();
		
		return request;
	}



	@Override
	public AuthenticateType createAuthenticateRequest(String smsCode, HttpRequestParams requestParams, SafiAnalyzeAndChallengeResponse safiResponse, String avaloqId, String safiDeviceId, String samlAssertion, String customerId, String gcmId)
	{
		DeviceRequest deviceRequest = new DeviceRequestBuilder().setHttpAccept(requestParams.getHttpAccept())
				.setHttpAcceptChars(requestParams.getHttpAcceptChars())
				.setHttpAcceptEncoding(requestParams.getHttpAcceptEncoding())
				.setHttpAcceptLanguage(requestParams.getHttpAcceptLanguage())
				.setHttpReferrer(requestParams.getHttpReferrer())
				.setIpAddress(requestParams.getHttpOriginatingIpAddress())
				.setUserAgent(requestParams.getHttpUserAgent())
				.setDevicePrint(safiResponse.getDevicePrint())
				.setDeviceTokenCookie(safiResponse.getDeviceToken())
				.build();
		
		IdentificationData idData = new IdentificationDataBuilder().setOrgName("BTNG")
				.setUserStatus(UserStatus.VERIFIED)
				.setUserType(WSUserType.PERSISTENT)
				.setSessionId(safiResponse.getIdentificationData().getSessionId())
				.setTransactionId(safiResponse.getIdentificationData().getTransactionId())
				.setUserName(gcmId)
				.build();
		
		MessageHeader messageHeader = new MessageHeaderBuilder().setApiType(APIType.DIRECT_SOAP_API)
				.forRequestType(RequestType.AUTHENTICATE)
				.setVersion(WEBSERVICE_VERSION)
				.build();		
		
		SecurityHeader securityHeader = new SecurityHeaderBuilder().setCallerCredential(callerCredential)
				.setCallerId(callerId)
				.setMethod(AuthorizationMethod.PASSWORD)
				.build();

        SMSOTPAuthenticationRequest smsOtpAuthRequest = new SMSOTPAuthenticationRequestBuilder().setDeviceId(safiDeviceId)
																								.setSmsOTP(smsCode)
																								.setTransactionId(safiResponse.getTransactionId())
																								.setSamlAssertion(samlAssertion)
																								.build();
																							
		
		
		AuthenticateType request = new AuthenticateRequestBuilder().setActionType(GenericActionType.SET_USER_STATUS)
																.addDeviceRequest(deviceRequest)
																.addIdentificationData(idData)
																.addMessageHeader(messageHeader)
																.addSecurityHeader(securityHeader)
																.addSmsOpAuthenticateRequest(smsOtpAuthRequest)
																.build();
		
		return request;
	}

	
	@Override
	public ChallengeType createChallengeRequest(SafiAnalyzeAndChallengeResponse safiResponse, HttpRequestParams requestParams, String avaloqId, String safiDeviceId, String samlAssertion, String gcmId) 
	{
        boolean initDevice = isInitDeviceRequired();
        
		DeviceRequest deviceRequest = new DeviceRequestBuilder().setHttpAccept(requestParams.getHttpAccept())
				.setHttpAcceptChars(requestParams.getHttpAcceptChars())
				.setHttpAcceptEncoding(requestParams.getHttpAcceptEncoding())
				.setHttpAcceptLanguage(requestParams.getHttpAcceptLanguage())
				.setHttpReferrer(requestParams.getHttpReferrer())
				.setIpAddress(requestParams.getHttpOriginatingIpAddress())
				.setUserAgent(requestParams.getHttpUserAgent())
				.setDevicePrint(safiResponse.getDevicePrint())
				.setDeviceTokenCookie(safiResponse.getDeviceToken())
				.build();

		IdentificationDataBuilder builder =  new IdentificationDataBuilder().setOrgName("BTNG")
				.setUserStatus(UserStatus.VERIFIED)
				.setUserType(WSUserType.PERSISTENT)
				.setUserName(gcmId);

		if (safiResponse.getIdentificationData().getSessionId() != null) {
			builder.setSessionId(safiResponse.getIdentificationData().getSessionId());
		}

		if (safiResponse.getIdentificationData().getTransactionId() != null) {
			builder.setTransactionId(safiResponse.getIdentificationData().getTransactionId());
		}

		if (safiResponse.getIdentificationData().getClientTransactionId() != null) {
			builder.setClientTransactionIdIdentificationData(safiResponse.getIdentificationData().getClientTransactionId());
		}

		if (safiResponse.getIdentificationData().getClientSessionId() != null) {
			builder.setClientSessionIdIdentificationData(safiResponse.getIdentificationData().getClientSessionId());
		}
		IdentificationData idData = builder.build();

		MessageHeader messageHeader = new MessageHeaderBuilder().setApiType(APIType.DIRECT_SOAP_API)
				.forRequestType(RequestType.CHALLENGE)
				.setVersion(WEBSERVICE_VERSION)
				.build();		
		
		SecurityHeader securityHeader = new SecurityHeaderBuilder().setCallerCredential(callerId)
				.setCallerId(callerId)
				.setCallerCredential(callerCredential)
				.setMethod(AuthorizationMethod.PASSWORD)
				.build();
		
		SMSOTPChallengeRequest smsOtpChallengeRequest = new SMSOTPChallengeRequestBuilder().setDeviceId(safiDeviceId)
																							.setNetworkId(avaloqId)
																							.setInitDevice(initDevice)
																							.setTransactionId(safiResponse.getTransactionId())
																							.setOrganisationId("BTNG")
																							.setBrandSilo("BTNG")
																							.setRequestingUserId("BTNG")
																							.setMessageText("Your SMS security code to approve your online transaction is SMSOTP")
																							.setSamlAssertion(samlAssertion)
																							.build();
																							
		
		
		ChallengeType request = new ChallengeRequestBuilder().addDeviceRequest(deviceRequest)
																.addIdentificationData(idData)
																.addMessageHeader(messageHeader)
																.addSecurityHeader(securityHeader)
																.addSmsOpChallengeRequest(smsOtpChallengeRequest)
																.build();
		
		return request;
	}

    @SuppressWarnings("squid:S00112")
    private boolean isInitDeviceRequired() {
        return false;
    }

	public SafiAuthenticateResponse toAuthenticateResultModel(CorrelatedResponse correlatedResponse, ServiceErrors serviceErrors)
	{
		SafiAuthenticateResponse result = new SafiAuthenticateResponse();			
		
		AuthenticateResponseType response = (AuthenticateResponseType) correlatedResponse.getResponseObject();
		AuthenticateResponse responseData = response.getAuthenticateReturn();
		SMSOTPAuthenticationResponse payload = (SMSOTPAuthenticationResponse) responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getPayload();
							
		
		// Error repsonse from SAFI -- generate service error object
		if (responseData.getStatusHeader().getStatusCode() != 200)
		{
			ServiceErrorImpl serviceErrImpl;
			
			// If there is credential data in the response then safi got quite far processing - not just a simple validation problem
			if (responseData.getCredentialAuthResultList() != null)
			{
				serviceErrImpl = new ServiceErrorImpl("SAFI", "Safi Authenticate error occured: {}" + getStatusCodeFromAuthenticateResponse(responseData), 
														getStatusDescriptionFromAuthenticateResponse(responseData), 
														correlatedResponse.getCorrelationIdWrapper().getCorrelationId());
			}
			else	// Safi basic validation problem
			{
				serviceErrImpl = new ServiceErrorImpl("SAFI", "Safi Authenticate error occured: {}" + getStatusCodeFromAuthenticateResponse(responseData), 
														getStatusDescriptionFromAuthenticateResponse(responseData), 
														correlatedResponse.getCorrelationIdWrapper().getCorrelationId());
			}
			
			serviceErrors.addError(serviceErrImpl);
		}
		
		
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
	
	
	public SafiAnalyzeAndChallengeResponse toChallengeResultModel(CorrelatedResponse correlatedResponse, SafiAnalyzeAndChallengeResponse analyzeResult, ServiceErrors serviceErrors)
	{
		// Persist the analyze results as part of the new challenge results
		SafiAnalyzeAndChallengeResponse result = analyzeResult;	
		ChallengeResponseType response = (ChallengeResponseType) correlatedResponse.getResponseObject();
		
		try
		{
			ChallengeResponse responseData = response.getChallengeReturn();		
			boolean actionCode = false;
			
			// Error repsonse from SAFI -- generate service error object
			if (responseData.getStatusHeader().getStatusCode() != 200)
			{
				ServiceErrorImpl serviceErrImpl;
				
				// If there is credential data in the response then safi got quite far processing - not just a simple validation problem
				if (responseData.getCredentialChallengeList() != null)
				{
					serviceErrImpl = new ServiceErrorImpl("SAFI", getStatusCodeFromChallengeResponse(responseData), 
														"Safi Challenge error occured: " + getStatusDescriptionFromChallengeResponse(responseData), 
														correlatedResponse.getCorrelationIdWrapper().getCorrelationId());
				}
				else	// Safi basic validation problem
				{
					serviceErrImpl = new ServiceErrorImpl("Safi Challenge error occured: " + getStatusDescriptionFromChallengeResponse(responseData),
														"Safi Challenge error occured: " + getStatusDescriptionFromChallengeResponse(responseData), 
														correlatedResponse.getCorrelationIdWrapper().getCorrelationId());
				}
				
				serviceErrors.addError(serviceErrImpl);
			}
							
			String statusCode = String.valueOf(responseData.getCredentialChallengeList().getAcspChallengeResponseData().getCallStatus().getStatusCode());					
			result.setIdentificationData(responseData.getIdentificationData());
								
			logger.info("toChallengeResultModel - statusCode: {}", statusCode);
			if(statusCode.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
			{
				actionCode = true;
			}
			else
			{
				actionCode = false;
			}
			result.setActionCode(actionCode);
		}
		catch (Exception e)
		{
			logger.warn("Error occured during conversion of Analyze response to model: {}", e);
			serviceErrors.addError(new ServiceErrorImpl("Error occured during conversion of Analyze response to model: {}", e));
		}
		
		return result;
	}

	private String getStatusDescriptionFromChallengeResponse(ChallengeResponse responseData)
	{
		return responseData.getCredentialChallengeList().getAcspChallengeResponseData().getCallStatus().getStatusDescription();
	}
	
	private String getStatusDescriptionFromAuthenticateResponse(AuthenticateResponse responseData)
	{
		return responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getCallStatus().getStatusDescription();
	}
	
	/**
	 * Try to return some meaningful error code from challenge response data.
	 * SAFI generally returns something descriptive - making the assumption that the error code (in upper case) is always returned
	 * If error code not found then default to the generic error code: usually this is ERROR
	 * 
	 * @param responseData challenge response data object
	 * @return error code
	 */
	private String getStatusCodeFromChallengeResponse(ChallengeResponse responseData)
	{
		String desc = getStatusDescriptionFromChallengeResponse(responseData);
		String errorCode = responseData.getCredentialChallengeList().getAcspChallengeResponseData().getCallStatus().getStatusCode();
		int errCodeStart = desc.indexOf("- ");
		
		if (errCodeStart >= 0)
		{		
			errCodeStart+= 2;
			errorCode = desc.substring(errCodeStart, desc.indexOf(" ", errCodeStart));
		}

		return errorCode;
	}
	
	
	private String getStatusCodeFromAuthenticateResponse(AuthenticateResponse responseData)
	{
		String desc = getStatusDescriptionFromAuthenticateResponse(responseData);
		String errorCode = responseData.getCredentialAuthResultList().getAcspAuthenticationResponseData().getCallStatus().getStatusCode();
		int errCodeStart = desc.indexOf("- ");
		
		if (errCodeStart >= 0)
		{		
			errCodeStart+= 2;
			errorCode = desc.substring(errCodeStart, desc.indexOf(" ", errCodeStart));
		}

		return errorCode;
	}
}
