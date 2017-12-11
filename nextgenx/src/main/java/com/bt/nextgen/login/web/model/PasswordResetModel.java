package com.bt.nextgen.login.web.model;

import com.bt.nextgen.core.domain.BaseObject;

public class PasswordResetModel extends BaseObject
{
	private static final long serialVersionUID = -3639526200936284568L;

	private String newPassword;
	private String confirmPassword;

	public String getConfirmPassword()
	{
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword)
	{
		this.confirmPassword = confirmPassword;
	}

	public String getNewPassword()
	{
		return newPassword;
	}

	public void setNewPassword(String newPassword)
	{
		this.newPassword = newPassword;
	}
}
