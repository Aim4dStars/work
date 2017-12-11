package com.bt.nextgen.service.safi.model;

import com.bt.nextgen.service.security.model.HttpRequestParams;

public interface TwoFactorAuthenticationBasicRequest 
{
	public HttpRequestParams getRequestParams();
	public void setHttpRequestParams(HttpRequestParams httpRequestParams);
}
