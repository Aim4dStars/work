package com.bt.nextgen.service.safi.model;

import com.bt.nextgen.web.validator.ValidationErrorCode;

public class SafiAuthenticateResponse implements SafiAuthenticateBasicResponse
{
	private boolean successFlag = false;
	private String statusCode = "";


    private String username = "";
	
	
	public boolean isSuccessFlag() {
		return successFlag;
	}
	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}		
	
	
	
	public String getDisplayMessageCode()	
	{
		return getDisplayMessageCode(getStatusCode());
	}
	
	
	/**
	 * Maps SAFI return codes from AUTHENTICATION to UI error codes.
	 * We can then obtain the descriptive error message using the UI error code.
	 * @param statusCode
	 * @return
	 */
	private String getDisplayMessageCode(String statusCode)	
	{
		String displayCode = "";
		
		if (statusCode != null && statusCode.equalsIgnoreCase("ACCESS_OK"))
		{
			// Display no error message
		}
		else if (statusCode != null && statusCode.equalsIgnoreCase("ACCESS_DENIED_OTP_INCORRECT"))
		{
			displayCode = ValidationErrorCode.INVALID_SMS_CODE;
		}
		else if (statusCode != null && statusCode.equalsIgnoreCase("ACCESS_DENIED_AUTHENTICATOR_SUSPENDED"))
		{
			displayCode = ValidationErrorCode.MAX_2FA_ATTEPTS_EXCEEDED;
		}
		else
		{
			//TODO: Correct error code required here
			displayCode = ValidationErrorCode.INVALID_SMS_CODE;
		}
		
		return displayCode;
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}