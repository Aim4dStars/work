package com.bt.nextgen.userauthority.web;

public enum Action 
{
	SIGN_IN_AS_USER("Sign in as this user"),
	RESET_PASSWORD("Reset Password"),
	BLOCK_ACCESS("Block access"),
	CONFIRM_SECURITY_MOBILE_NUMBER("Confirm security mobile number"),
	UNLOCK_SECURITY_MOBILE_NUMBER("Unlock security mobile number"),
	MOBILE_SECURITY_EXEMPTION("Mobile security exemption"),
	UNBLOCK_ACCESS("Unblock access"),
	RESEND_REGISTRATION_EMAIL("Send email with new registration code"),
	RESEND_EXISTING_REGISTRATION_CODE("Resend existing registration code"),
	CREATE_ACCOUNT("Create account"),
    PROVISION_MFA_DEVICE("Setup mobile with SAFI"),
	UPDATE_PPID("Update PPID");
		
	String name;

	public String getName()
	{
		return name;
	}

	Action(String name)
	{
		this.name = name;
	}
}
