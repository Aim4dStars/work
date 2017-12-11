package com.bt.nextgen.core.web.model;

import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.bt.nextgen.service.group.customer.CustomerUsernameUpdateRequest;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import org.opensaml.ws.wstrust.UseKey;

public class User implements CustomerUsernameUpdateRequest, CredentialRequest{
	
	private String accountStatus;
	private int bankDefinedLoginName;
	private String credentialId;
	//customerDefinedLoginName
	private String userName;
	private String signin;
	private String password;
	private String brand;
	private String halgm;
	private String samlToken;
	private String newUserName;
	private String gcmId;
    private UserKey userKey;
    private CISKey cisKey;
	
	
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public int getBankDefinedLoginName() {
		return bankDefinedLoginName;
	}
	public void setBankDefinedLoginName(int bankDefinedLoginName) {
		this.bankDefinedLoginName = bankDefinedLoginName;
	}
	public String getCredentialId() {
		return credentialId;
	}
	public void setCredentialId(String credentialId) {
		this.credentialId = credentialId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSignin() {
		return signin;
	}
	public void setSignin(String signin) {
		this.signin = signin;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getHalgm() {
		return halgm;
	}
	public void setHalgm(String halgm) {
		this.halgm = halgm;
	}
	public String getSamlToken() {
		return samlToken;
	}
	public void setSamlToken(String samlToken) {
		this.samlToken = samlToken;
	}
	public String getNewUserName() {
		return newUserName;
	}
	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}
	public String getBankReferenceId() {
		return gcmId;
	}
    public void setBankReferenceId(String gcmId) {
		this.gcmId = gcmId;
	}

    @Override
    public UserKey getBankReferenceKey() {
        if(null== userKey){
            userKey=UserKey.valueOf(gcmId);
        }
           return userKey;
    }

    @Override
    public CISKey getCISKey() {
        return cisKey;
    }
}
