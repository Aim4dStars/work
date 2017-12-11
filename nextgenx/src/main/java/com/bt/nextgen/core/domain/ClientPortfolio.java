package com.bt.nextgen.core.domain;

import java.util.List;

public class ClientPortfolio {

	private String advisorName;
	private String portfolioValue;
	private String balance;
	private List<LinkedAccount> linkedAccount;
	private String accountId;
	private String accountType;
	private String accountName;
	private String firstName;
	private String lastName;
	private String primaryContact;
	
	public List<LinkedAccount> getLinkedAccount() {
		return linkedAccount;
	}
	public void setLinkedAccount(List<LinkedAccount> linkedAccount) {
		this.linkedAccount = linkedAccount;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
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
	
}
