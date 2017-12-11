package com.bt.nextgen.portfolio.domain;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
public class PortfolioDomain
{
	@XmlElement
	private String accountName;
	@XmlElement	
	private String accountId;
	@XmlElement	
	private String accountType;
	@XmlElement
	private String registerDate;
	@XmlElement	
	private String cashAccountNumber;
	@XmlElement	
	private String availableBalance;
	@XmlElement
	private String adviser;
	@XmlElement
	private BigDecimal balance;
	@XmlElement
	private String portfolioBalance;
	@XmlElement
	private String portfolioId;
	@XmlElement
	private String clientId;
	@XmlElement
	private String adviserPermission;
	@XmlElement
	private String adviserId;
	@XmlElement
	private String adviserFirstName;
	@XmlElement
	private String adviserLastName;
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
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
	public String getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	public String getCashAccountNumber() {
		return cashAccountNumber;
	}
	public void setCashAccountNumber(String cashAccountNumber) {
		this.cashAccountNumber = cashAccountNumber;
	}
	public String getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(String availableBalance) {
		this.availableBalance = availableBalance;
	}
	public String getAdviser() {
		return adviser;
	}
	public void setAdviser(String adviser) {
		this.adviser = adviser;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getPortfolioBalance() {
		return portfolioBalance;
	}
	public void setPortfolioBalance(String portfolioBalance) {
		this.portfolioBalance = portfolioBalance;
	}
	public String getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getAdviserPermission() {
		return adviserPermission;
	}
	public void setAdviserPermission(String adviserPermission) {
		this.adviserPermission = adviserPermission;
	}
	public String getAdviserId() {
		return adviserId;
	}
	public void setAdviserId(String adviserId) {
		this.adviserId = adviserId;
	}
	public String getAdviserFirstName() {
		return adviserFirstName;
	}
	public void setAdviserFirstName(String adviserFirstName) {
		this.adviserFirstName = adviserFirstName;
	}
	public String getAdviserLastName() {
		return adviserLastName;
	}
	public void setAdviserLastName(String adviserLastName) {
		this.adviserLastName = adviserLastName;
	}

}
