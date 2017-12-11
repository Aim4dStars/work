package com.bt.nextgen.login.web.model;

import com.bt.nextgen.core.domain.BaseObject;
import com.bt.nextgen.service.group.customer.CustomerPasswordUpdateRequest;
import com.bt.nextgen.service.group.customer.CustomerUsernameUpdateRequest;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.bt.nextgen.web.validator.annotation.Password;
import com.bt.nextgen.web.validator.annotation.RegistrationPolicy;
import com.bt.nextgen.web.validator.annotation.Username;

@RegistrationPolicy
public class RegistrationModel extends BaseObject implements CustomerUsernameUpdateRequest, CustomerPasswordUpdateRequest
{
	private static final long serialVersionUID = 4277209088035473092L;


	private String userCode;

	@Password(message = ValidationErrorCode.INVALID_PASSWORD)
	private String password;

	@Password
	private String confirmPassword;
	
	private String halgm;
	
	private byte[] halgmInBytes;

    @Username
	private String newUserName;

	private String credentialId;
	
	private String requestedAction;
	
	private boolean tncAccepted;
	
	public String getUserCode()
	{
		return userCode;
	}

	public void setUserCode(String userCode)
	{
		this.userCode = userCode;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
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

	public String getHalgm() {
		return halgm;
	}

	public void setHalgm(String halgm) {
		this.halgm = halgm;
	}

	public String getNewUserName() {
		return newUserName;
	}
	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}

	public String getCredentialId() {
		return credentialId;
	}

    public void setCredentialId(String credentialId)
    {
        this.credentialId = credentialId;
    }

	@Override
	public String getRequestedAction() {
		return requestedAction;
	}

	@Override
	public void setRequestedAction(String requestedAction) {
		this.requestedAction = requestedAction;
	}

	/**
	 * @return the tncAccepted
	 */
	public boolean isTncAccepted() {
		return tncAccepted;
	}

	/**
	 * @param tncAccepted the tncAccepted to set
	 */
	public void setTncAccepted(boolean tncAccepted) {
		this.tncAccepted = tncAccepted;
	}

	@Override
	public String toString()
	{
		StringBuilder buff = new StringBuilder();
		buff.append("Username: ");
		buff.append(getNewUserName());
		buff.append(", Password: ");
		buff.append(getPassword());
		buff.append(", Confirm Password: ");
		buff.append(getConfirmPassword());

		return buff.toString();
	}

}
