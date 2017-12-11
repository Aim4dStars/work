package com.bt.nextgen.service.safi.model;

import com.bt.nextgen.service.security.model.HttpRequestParams;

public class SafiAuthenticateRequest implements TwoFactorAuthenticationBasicRequest 
{
	private HttpRequestParams httpRequestParams;
	private SafiAnalyzeAndChallengeResponse analyzeResult;
	private String smsCode;
	
	
	public SafiAuthenticateRequest(HttpRequestParams httpRequestParams, SafiAnalyzeAndChallengeResponse safiAnalyzeResult, String smsCode)
	{
		setHttpRequestParams(httpRequestParams);
		setAnalyzeResult(safiAnalyzeResult);
		setSmsCode(smsCode);
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

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
}
