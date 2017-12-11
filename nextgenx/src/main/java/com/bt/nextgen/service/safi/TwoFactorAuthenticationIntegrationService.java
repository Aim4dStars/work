package com.bt.nextgen.service.safi;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.model.HttpRequestParams;

public interface TwoFactorAuthenticationIntegrationService 
{
	public SafiAnalyzeAndChallengeResponse analyze(EventModel eventModel, HttpRequestParams requestParams, ServiceErrors serviceErrors);
	
	public SafiAnalyzeAndChallengeResponse challenge(SafiAnalyzeAndChallengeResponse safiResponse, HttpRequestParams requestParams, ServiceErrors serviceErrors);

	public SafiAnalyzeAndChallengeResponse challengeFromNotAuthCtx(SafiAnalyzeAndChallengeResponse safiResponse, HttpRequestParams requestParams, ServiceErrors serviceErrors);
	
	public SafiAuthenticateResponse authenticate(String smsCode, HttpRequestParams requestParams, SafiAnalyzeAndChallengeResponse safiResponse, ServiceErrors serviceErrors) throws Exception;
}
