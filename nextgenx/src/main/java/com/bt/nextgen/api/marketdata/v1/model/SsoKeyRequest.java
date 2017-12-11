package com.bt.nextgen.api.marketdata.v1.model;

public class SsoKeyRequest
{
	private UrlParam userId;
	private UrlParam userTimestamp;
	private UrlParam userTier;
	private UrlParam shareEnabled;
	
	private final static String PARAM_JOIN = "&";

	private static final String USER_ID = "User_ID";
	private static final String USER_TIER = "User_Tier";
	private static final String USER_TIMESTAMP = "User_TimeStamp";
	private static final String SHARE_ENABLED = "enableShare";

	private SsoKeyRequest()
	{

	}

	public SsoKeyRequest(String userId, String userTier, String userTimestamp, String shareEnabled)
	{
		this.userId = new UrlParam(USER_ID,userId);
		this.userTier = new UrlParam(USER_TIER,userTier);
		this.userTimestamp = new UrlParam(USER_TIMESTAMP,userTimestamp);
		this.shareEnabled = new UrlParam(SHARE_ENABLED, shareEnabled);
	}

	public String getFullRequestString()
	{
	    return this.userId.getUrlParam() + PARAM_JOIN + this.userTier.getUrlParam() + PARAM_JOIN
	            + this.userTimestamp.getUrlParam() + PARAM_JOIN + this.shareEnabled.getUrlParam();
	}

}
