package com.bt.nextgen.core.security;

import java.util.HashMap;
import java.util.Map;

public enum TamOperationCode 
{
	LOGIN_SUCCESS("login_success"),// User is authenticated and should be re-directed to the secure part of the site
	LOGIN_BLOCKED("blocked","Err0083"), //Users EAM status indicates that their access is blocked
	TEMP_PASSWORD("passwd_exp"), //returned to force user to perform password reset (as a result of a call to the service ops team)
	AUTH_INFO("auth_info","Err.IP-0313"),
	AUTH_FAILURE("auth_failure","Err.IP-0318"), //Username doesn't exist or password is incorrect
	AUTH_SUSP("auth_susp","Err.IP-0313"), // User is in a suspended state
	AUTH_TIMEOUT("auth_timeout","Err.IP-0314"), //The Encryption used has expired, the user has been on the login page to long
	EAI_AUTH_ERROR("eai_auth_error","Err.IP-0315"),
	ERROR("error","Err.IP-0314"),
	HELP("help"),
	LOGIN("login"),
	LOGOUT("logout"),
	PASSWD_REP_FAILURE("passwd_rep_failure","Err.IP-0323"), // Password change request failed.
	PASSWD_POLICY_INHIST("passwd_policy_inHist","Err.IP-0324"), // Password is in the users history
	PASSWD_POLICY_MAXCONREPCHAR("passwd_policy_maxConRepChar", "Err.IP-0325"), // Too many consecutive repeated characters
	PASSWD_POLICY_MINOTHER("passwd_policy_minOther","Err.IP-0326"),  // Not enough special characters
	PASSWD_POLICY_MINALPHA("passwd_policy_minAlpha","Err.IP-0326"),  // Not enough Alphas
	PASSWD_POLICY_MINLENGTH("passwd_policy_minLength","Err.IP-0326"), // Too short
	PASSWD_REP_SUCCESS("passwd_rep_success"), // Password change request succeeded. 
	PASSWD_WARN("PASSWD_WARN"), // Password is soon to expire. 
	TEMP_PASSWD_EXP("temp_passwd_exp","Err.IP-0317"), //If their temporary password has expired.
	STEPUP("stepup") //User is authenticated but doesn't have the correct permission to access the site/url which they are attempting to.
	;
	
	private static final Map<String, TamOperationCode> lookup = new HashMap<>(); 
	
	static
	{
		for (TamOperationCode tamOp : TamOperationCode.values())
		{
			lookup.put(tamOp.getTamOperationCode(), tamOp);
		}
	}
	
	public static TamOperationCode get(String tamOperationCode)
	{
		return lookup.get(tamOperationCode);
	}
	
	private String TamOperationCodeValue = "";
	private String tamMessageId = "";
	
	TamOperationCode(String tamOperationCodeValue)
	{
		this.TamOperationCodeValue = tamOperationCodeValue;
	}

	TamOperationCode(String tamOperationCodeValue, String tamMessageId)
	{
		this.TamOperationCodeValue = tamOperationCodeValue;
		this.tamMessageId = tamMessageId;
	}

	public String getTamMessageId()
	{
		return this.tamMessageId;
	}

	public String getTamOperationCode()
	{
		return TamOperationCodeValue;
	}
}
