package com.bt.nextgen.core.web.model;

import com.bt.nextgen.service.group.customer.CustomerPasswordUpdateRequest;
import com.bt.nextgen.web.validator.annotation.Password;
import com.bt.nextgen.web.validator.annotation.Username;

public class UserReset extends User implements CustomerPasswordUpdateRequest
{
	private String newpassword;

	private String confirmPassword;
	
	private byte[] halgmInBytes;
	
	private String halgm;
	
	@Username
	private String newUserName;
	
	private String requestedAction;

	public String getNewpassword()
	{
		return newpassword;
	}

	public void setNewpassword(String newpassword)
	{
		this.newpassword = newpassword;
	}

	public String getConfirmPassword()
	{
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}
	
	public byte[] getHalgmInBytes() 
	{
		return halgmInBytes;
	}

	public void setHalgmInBytes(byte[] halgmInBytes) 
	{
		this.halgmInBytes = halgmInBytes;
	}

	public String getNewUserName()
	{
		return newUserName;
	}

	public void setNewUserName(String newUserName)
	{
		this.newUserName = newUserName;
	}

	public String getRequestedAction() 
	{
		return requestedAction;
	}

	public void setRequestedAction(String requestedAction) 
	{
		this.requestedAction = requestedAction;
	}

	public String getHalgm() {
		return halgm;
	}

	public void setHalgm(String halgm) {
		this.halgm = halgm;
	}
	
	
}
