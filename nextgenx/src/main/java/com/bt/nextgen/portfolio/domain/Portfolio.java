package com.bt.nextgen.portfolio.domain;

import com.bt.nextgen.clients.domain.Client;
import com.bt.nextgen.core.domain.Money;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.termdeposit.domain.TermDepositAccount;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Deprecated
public class Portfolio
{

	private String accountName;
	private String accountId;
	private String accountType;
	private List <Client> accountHolders;
	private Client primaryHolder;
	private AccountPermissions holderPermission;
	private Date registerDate;
	private Money portfolio;
	private CashAccount cashAccount;
	private List <TermDepositAccount> termDepositAccounts;
	private String productName;

	private Date fromDate;
	private Date toDate;
	private Date lastPortfolioCalDate;
	
	//TODO: Added for navigation, need more clarity on this.
	private EncodedString portfolioId;
	private EncodedString cashAccountId;
	
	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public Money getPortfolio()
	{
		return portfolio;
	}

	public void setPortfolio(Money portfolio)
	{
		this.portfolio = portfolio;
	}

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

	public List <Client> getAccountHolders()
	{
		return accountHolders;
	}

	public void setAccountHolders(List <Client> accountHolders)
	{
		this.accountHolders = accountHolders;
	}

	public Client getPrimaryHolder()
	{
		return primaryHolder;
	}

	public void setPrimaryHolder(Client primaryHolder)
	{
		this.primaryHolder = primaryHolder;
	}

	public AccountPermissions getHolderPermission()
	{
		return holderPermission;
	}

	public void setHolderPermission(AccountPermissions holderPermission)
	{
		this.holderPermission = holderPermission;
	}

	public Date getRegisterDate()
	{
		return registerDate;
	}

	public void setRegisterDate(Date registerDate)
	{
		this.registerDate = registerDate;
	}

	/**
	 * This shouldn't be used as we break encapsulation!!!
	 * @return
	 */
	@Deprecated
	public CashAccount getCashAccount()
	{
		return cashAccount;
	}

	public void setCashAccount(CashAccount cashAccount)
	{
		this.cashAccount = cashAccount;
	}

	public List <TermDepositAccount> getTermDepositAccounts()
	{
		return termDepositAccounts;
	}

	public void setTermDepositAccounts(List <TermDepositAccount> termDepositAccounts)
	{
		this.termDepositAccounts = termDepositAccounts;
	}

	public Date getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(Date fromDate)
	{
		this.fromDate = fromDate;
	}

	public Date getToDate()
	{
		return toDate;
	}

	public void setToDate(Date toDate)
	{
		this.toDate = toDate;
	}

	public Date getLastPortfolioCalDate()
	{
		return lastPortfolioCalDate;
	}

	public void setLastPortfolioCalDate(Date lastPortfolioCalDate)
	{
		this.lastPortfolioCalDate = lastPortfolioCalDate;
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

	public BigDecimal getAvailableBalance()
	{
		return cashAccount.getAvailableBalance();
	}
}
