package com.bt.nextgen.service.safi.model;

import com.bt.nextgen.service.security.model.HttpRequestParams;

public class SafiChallengeRequest implements TwoFactorAuthenticationBasicRequest 
{
	private HttpRequestParams httpRequestParams;
	private SafiAnalyzeAndChallengeResponse analyzeResult;
	
	
	public SafiChallengeRequest(HttpRequestParams httpRequestParams, SafiAnalyzeAndChallengeResponse analyzeRequest) {
		setHttpRequestParams(httpRequestParams);
		setAnalyzeResult(analyzeRequest);
	}
	
	
	@Override
	public HttpRequestParams getRequestParams() {
		return httpRequestParams;
	}

	@Override
	public void setHttpRequestParams(HttpRequestParams httpRequestParams) {
		this.httpRequestParams = httpRequestParams;
	}

	public SafiAnalyzeAndChallengeResponse getAnalyzeResult() {
		return analyzeResult;
	}

	public void setAnalyzeResult(SafiAnalyzeAndChallengeResponse analyzeResult) {
		this.analyzeResult = analyzeResult;
	}
}
