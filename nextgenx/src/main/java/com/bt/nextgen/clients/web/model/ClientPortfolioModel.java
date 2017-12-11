package com.bt.nextgen.clients.web.model;

import com.bt.nextgen.core.domain.LinkedAccount;
import com.btfin.panorama.core.security.encryption.EncodedString;

import java.util.List;

public class ClientPortfolioModel {

	private String advisorName;
	private String portfolioValue;
	private String balance;
	private String accountId;
	private String accountType;
	private String accountName;
	private String firstName;
	private String lastName;
	private String primaryContact;
	private List<LinkedAccount> linkedAccount;
	
	//TODO: Added for navigation, need more clarity on this.
	private EncodedString portfolioId;
	private EncodedString cashAccountId;
	
	public List<LinkedAccount> getLinkedAccount() {
		return linkedAccount;
	}
	public void setLinkedAccount(List<LinkedAccount> linkedAccount) {
		this.linkedAccount = linkedAccount;
	}
	public String getAdvisorName() {
		return advisorName;
	}
	public void setAdvisorName(String advisorName) {
		this.advisorName = advisorName;
	}
	public String getPortfolioValue() {
		return portfolioValue;
	}
	public void setPortfolioValue(String portfolioValue) {
		this.portfolioValue = portfolioValue;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPrimaryContact() {
		return primaryContact;
	}
	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}
	public EncodedString getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(EncodedString portfolioId) {
		this.portfolioId = portfolioId;
	}
	public EncodedString getCashAccountId() {
		return cashAccountId;
	}
	public void setCashAccountId(EncodedString cashAccountId) {
		this.cashAccountId = cashAccountId;
	}
	
}
