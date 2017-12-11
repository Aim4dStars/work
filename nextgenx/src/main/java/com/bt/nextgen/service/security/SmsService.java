package com.bt.nextgen.service.security;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.service.security.model.HttpRequestParams;



public interface SmsService
{
	String notifyUser() throws Exception;
	String queryUser() throws Exception;
	String createUser() throws Exception;
	String queryAuthStatus() throws Exception;
	String updateUser() throws Exception;
	@Deprecated
	boolean analyzeFromSafi(EventModel eventModel) throws Exception; 
	@Deprecated
	boolean sendSmsCodeFromSafi() throws Exception;
	@Deprecated
	boolean authenticateSmsCodeFromSafi(String smsCode) throws Exception;
	
	//Below code line in Cash 4
	SafiAnalyzeResult analyzeFromSafi(EventModel eventModel, HttpRequestParams requestParams) throws Exception; 
	SafiAnalyzeResult sendSmsCodeFromSafi(SafiAnalyzeResult safiResponse, HttpRequestParams requestParams, String... userName) throws Exception;
	SafiAuthenticateResult authenticateSmsCodeFromSafi(String smsCode, HttpRequestParams requestParams, SafiAnalyzeResult safiResponse) throws Exception;
	SafiAuthenticateResult authenticateSmsCodeFromSafi(String smsCode, HttpRequestParams requestParams, SafiAnalyzeResult safiResponse, String samlToken) throws Exception;
}
