package com.bt.nextgen.service.safi;

import java.util.Map;

import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.rsa.csd.ws.AnalyzeType;
import com.rsa.csd.ws.AuthenticateType;
import com.rsa.csd.ws.ChallengeType;

public interface TwoFactorAuthenticationDocumentService 
{
	public AnalyzeType createAnalyzeRequest(HttpRequestParams requestParams, String devicePrint, String clientSessionId, 
											String clientTransactionId, Map<String, String> facts, Map<String, String> eventType, String username);
	
	public ChallengeType createChallengeRequest(SafiAnalyzeAndChallengeResponse safiResponse, HttpRequestParams requestParams, 
												String avaloqId, String safiDeviceId, String samlToken, String gcmId);
	
	public AuthenticateType createAuthenticateRequest(String smsCode, HttpRequestParams requestParams, SafiAnalyzeAndChallengeResponse safiResponse, 
														String avaloqId, String safiDeviceId, String samlAssertion, String customerId, String gcmId);
	
	public SafiAnalyzeAndChallengeResponse toChallengeResultModel(CorrelatedResponse correlatedResponse, SafiAnalyzeAndChallengeResponse analyzeResult, ServiceErrors serviceErrors);
	
	public SafiAuthenticateResponse toAuthenticateResultModel(CorrelatedResponse correlatedResponse, ServiceErrors serviceErrors);
}
