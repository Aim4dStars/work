package com.bt.nextgen.service.safi.model;

import com.bt.nextgen.web.validator.ValidationErrorCode;

public interface SafiAuthenticateBasicResponse
{
	public boolean isSuccessFlag();
	
	public void setSuccessFlag(boolean successFlag);
	
	public String getStatusCode();
	
	public void setStatusCode(String statusCode);	
	
	public String getDisplayMessageCode();
	
    public String getUsername();

    public void setUsername(String username);


}