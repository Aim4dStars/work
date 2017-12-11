package com.bt.nextgen.portfolio.web.model;


import com.bt.nextgen.clients.util.EncodedStringtoString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Deprecated
public class AccountModel implements Comparable <AccountModel>
{
	private String accountName;
	private String accountId;
	private String accountType;
	private String adviser;
	private String balance;
	private String portfolio;
	//private Contact contact;
	
	//TODO: Added for navigation, need more clarity on this.
	@JsonSerialize(using=EncodedStringtoString.class)
	private EncodedString portfolioId;
	@JsonSerialize(using=EncodedStringtoString.class)
	private EncodedString clientIdEncoded;
	@JsonSerialize(using=EncodedStringtoString.class)
	private EncodedString cashAccountId;
	private String clientId;
	private String clientName;
	private String cashAccountNumber;
	
	private String adviserId;
	private String adviserFirstName;
	private String adviserLastName;
    private String adviserPermission;
    private String productName;
    private Boolean isExactMatchForSearchCriteria;
    
    private String primaryContactName;
	private String primaryContactClientId;
	private String legalClientId;
	private String displayName;

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getAdviser()
	{
		return adviser;
	}

	public void setAdviser(String adviser)
	{
		this.adviser = adviser;
	}

	public String getBalance()
	{
		return balance;
	}

	public void setBalance(String balance)
	{
		this.balance = balance;
	}

	public String getPortfolio()
	{
		return portfolio;
	}

	public void setPortfolio(String portfolio)
	{
		this.portfolio = portfolio;
	}

	
	

	@Override
	public int compareTo(AccountModel accountModel)
	{
		int result;
		result = accountName.compareTo(accountModel.getAccountName());
		if (result == 0)
		{
			return accountName.compareTo(accountModel.getAccountName());
		}
		return result;
	}

	public EncodedString getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(EncodedString portfolioId) {
		this.portfolioId = portfolioId;
	}

	public EncodedString getClientIdEncoded() {
		return clientIdEncoded;
	}

	public void setClientIdEncoded(EncodedString clientIdEncoded) {
		this.clientIdEncoded = clientIdEncoded;
	}

	public EncodedString getCashAccountId() {
		return cashAccountId;
	}

	public void setCashAccountId(EncodedString cashAccountId) {
		this.cashAccountId = cashAccountId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getCashAccountNumber() {
		return cashAccountNumber;
	}

	public void setCashAccountNumber(String cashAccountNumber) {
		this.cashAccountNumber = cashAccountNumber;
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

	public String getAdviserPermission() {
		return adviserPermission;
	}

	public void setAdviserPermission(String adviserPermission) {
		this.adviserPermission = adviserPermission;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public Boolean getIsExactMatchForSearchCriteria() 
	{
		return isExactMatchForSearchCriteria;
	}

	public void setIsExactMatchForSearchCriteria(
			Boolean isExactMatchForSearchCriteria) 
	{
		this.isExactMatchForSearchCriteria = isExactMatchForSearchCriteria;
	}

	public String getPrimaryContactName() {
		return primaryContactName;
	}

	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}

	public String getPrimaryContactClientId() {
		return primaryContactClientId;
	}

	public void setPrimaryContactClientId(String primaryContactClientId) {
		this.primaryContactClientId = primaryContactClientId;
	}

	public String getLegalClientId() {
		return legalClientId;
	}

	public void setLegalClientId(String legalClientId) {
		this.legalClientId = legalClientId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}
