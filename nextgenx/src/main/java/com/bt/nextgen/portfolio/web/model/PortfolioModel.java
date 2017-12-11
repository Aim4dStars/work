package com.bt.nextgen.portfolio.web.model;

import com.bt.nextgen.blockcodes.model.Blockcodes;
import com.bt.nextgen.clients.util.EncodedStringtoString;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.Format;
import com.bt.nextgen.core.web.model.Person;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.termdeposit.web.model.TermDepositAccountModel;
import com.bt.nextgen.termdeposit.web.model.TermDepositModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.util.MoneyUtil.*;

@Deprecated
public class PortfolioModel implements Comparable <PortfolioModel>, PortfolioInterface
{
	private String accountName;
	private String accountId;
	private String accountType;
	private DateTime accountStartDate;
	private String accountStatus;
	private ClientModel primaryHolder;
	private String holderPermission;
	private String registerDate;
	private List <CashAccountModel> cashAccounts;
	private List <TermDepositAccountModel> termDepositAccounts;
	private List <PhoneModel> mobileList;
	private String adviser;
	private BigDecimal balance;
	private String portfolioBalance;
	private TermDepositModel termDepositModel;
	private CashAccountModel cashAccount;

	private String primaryContact;

	private String totalUnreadNessages;

	//TODO: Added for navigation, need more clarity on this.
	@JsonSerialize(using = EncodedStringtoString.class)
	private EncodedString portfolioId;
	@JsonSerialize(using = EncodedStringtoString.class)
	private EncodedString clientId;
	private String productName;
	private List <ClientModel> otherAcccountHolders;
	private String generateDate;
	private String totalTermDepositBalance;
	private String currentDate;
	private String fmtCurrentDate;

	private Map <String, String> clientPermission;

	//Added for DismissAll operation of investor in dashboard.
	private String listofMsgIds;

	private List <Blockcodes> blockcodeList;

	private String adviserPermission;

	private String adviserId;
	private String adviserFirstName;
	private String adviserLastName;

	private List <Person> accountHolders;
	// Total portfolio value for idssnapshotReport
	private String portfolioValue;
	private String legalClientPersonId;

	public String getLegalClientPersonId()
	{
		return legalClientPersonId;
	}

	public void setLegalClientPersonId(String legalClientPersonId)
	{
		this.legalClientPersonId = legalClientPersonId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#compareTo(com.bt.nextgen.portfolio.web.model.PortfolioModel)
	 */
	@Override
	public int compareTo(PortfolioModel other)
	{
		return accountType.compareTo(other.getAccountType());
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getGenerateDate()
	 */
	@Override
	public String getGenerateDate()
	{
		return generateDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setGenerateDate(java.lang.String)
	 */
	@Override
	public void setGenerateDate(String generateDate)
	{
		this.generateDate = generateDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getCurrentDate()
	 */
	@Override
	public String getCurrentDate()
	{
		return currentDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setCurrentDate(java.lang.String)
	 */
	@Override
	public void setCurrentDate(String currentDate)
	{
		this.currentDate = currentDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getFmtCurrentDate()
	 */
	@Override
	public String getFmtCurrentDate()
	{
		return fmtCurrentDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setFmtCurrentDate(java.lang.String)
	 */
	@Override
	public void setFmtCurrentDate(String fmtCurrentDate)
	{
		this.fmtCurrentDate = fmtCurrentDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getTotalTermDepositBalance()
	 */
	@Override
	public String getTotalTermDepositBalance()
	{
		return totalTermDepositBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setTotalTermDepositBalance(java.lang.String)
	 */
	@Override
	public void setTotalTermDepositBalance(String totalTermDepositBalance)
	{
		this.totalTermDepositBalance = totalTermDepositBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getDollarAmount()
	 */
	@Override
	public String getDollarAmount()
	{
		return getDollarPart(balance);
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getCentAmount()
	 */
	@Override
	public String getCentAmount()
	{
		return getCentPart(balance);
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getTermDepositModel()
	 */
	@Override
	public TermDepositModel getTermDepositModel()
	{
		return termDepositModel;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setTermDepositModel(com.bt.nextgen.termdeposit.web.model.TermDepositModel)
	 */
	@Override
	public void setTermDepositModel(TermDepositModel termDepositModel)
	{
		this.termDepositModel = termDepositModel;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getBalance()
	 */
	@Override
	public String getBalance()
	{
		return ApiFormatter.asDecimal(balance);
	}

	@Override
	public String getPortfolioBalance()
	{
		return portfolioBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getRawBalance()
	 */
	@Override
	public BigDecimal getRawBalance()
	{
		return balance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setBalance(java.math.BigDecimal)
	 */
	@Override
	public void setBalance(BigDecimal balance)
	{
		this.balance = balance;
	}

	@Override
	public void setPortfolioBalance(String portfolioBalance)
	{
		this.portfolioBalance = portfolioBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getAdviser()
	 */
	@Override
	public String getAdviser()
	{
		return adviser;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setAdviser(java.lang.String)
	 */
	@Override
	public void setAdviser(String adviser)
	{
		this.adviser = adviser;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getAccountName()
	 */
	@Override
	public String getAccountName()
	{
		return accountName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setAccountName(java.lang.String)
	 */
	@Override
	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getAccountId()
	 */
	@Override
	public String getAccountId()
	{
		return accountId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setAccountId(java.lang.String)
	 */
	@Override
	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getInvestorAccountType()
	 */
	@Override
	public String getAccountType()
	{
		return accountType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setAccountType(java.lang.String)
	 */
	@Override
	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getPrimaryHolder()
	 */
	@Override
	public ClientModel getPrimaryHolder()
	{
		return primaryHolder;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setPrimaryHolder(com.bt.nextgen.clients.web.model.ClientModel)
	 */
	@Override
	public void setPrimaryHolder(ClientModel primaryHolder)
	{
		this.primaryHolder = primaryHolder;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getHolderPermission()
	 */
	@Override
	public String getHolderPermission()
	{
		return holderPermission;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setHolderPermission(java.lang.String)
	 */
	@Override
	public void setHolderPermission(String holderPermission)
	{
		this.holderPermission = holderPermission;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getRegisterDate()
	 */
	@Override
	public String getRegisterDate()
	{
		return registerDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setRegisterDate(java.lang.String)
	 */
	@Override
	public void setRegisterDate(String registerDate)
	{
		this.registerDate = registerDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getPortfolio()
	 */
	@Override
	@Deprecated
	public String getPortfolio()
	{
		return getBalance();
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getCashAccounts()
	 */
	@Override
	public List <CashAccountModel> getCashAccounts()
	{
		return cashAccounts;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setCashAccounts(java.util.List)
	 */
	@Override
	public void setCashAccounts(List <CashAccountModel> cashAccounts)
	{
		this.cashAccounts = cashAccounts;
	}

	@Override
	public List <PhoneModel> getMobileList()
	{
		return mobileList;
	}

	@Override
	public void setMobileList(List <PhoneModel> mobileList)
	{
		this.mobileList = mobileList;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getTermDepositAccounts()
	 */
	@Override
	public List <TermDepositAccountModel> getTermDepositAccounts()
	{
		return termDepositAccounts;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setTermDepositAccounts(java.util.List)
	 */
	@Override
	public void setTermDepositAccounts(List <TermDepositAccountModel> termDepositAccounts)
	{
		this.termDepositAccounts = termDepositAccounts;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getTotalUnreadNessages()
	 */
	@Override
	public String getTotalUnreadNessages()
	{
		return totalUnreadNessages;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setTotalUnreadNessages(java.lang.String)
	 */
	@Override
	public void setTotalUnreadNessages(String totalUnreadNessages)
	{
		this.totalUnreadNessages = totalUnreadNessages;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getPortfolioId()
	 */
	@Override
	public EncodedString getPortfolioId()
	{
		return portfolioId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setPortfolioId(com.btfin.panorama.core.security.encryption.EncodedString)
	 */
	@Override
	public void setPortfolioId(EncodedString portfolioId)
	{
		this.portfolioId = portfolioId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getProductName()
	 */
	@Override
	public String getProductName()
	{
		return productName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setProductName(java.lang.String)
	 */
	@Override
	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getPrimaryContact()
	 */
	@Override
	public String getPrimaryContact()
	{
		return primaryContact;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setPrimaryContact(java.lang.String)
	 */
	@Override
	public void setPrimaryContact(String primaryContact)
	{
		this.primaryContact = primaryContact;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getCashAccount()
	 */
	@Override
	public CashAccountModel getCashAccount()
	{
		return cashAccount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setCashAccount(com.bt.nextgen.portfolio.web.model.CashAccountModel)
	 */
	@Override
	public void setCashAccount(CashAccountModel cashAccount)
	{
		this.cashAccount = cashAccount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getClientId()
	 */
	@Override
	public EncodedString getClientId()
	{
		return clientId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setClientId(com.btfin.panorama.core.security.encryption.EncodedString)
	 */
	@Override
	public void setClientId(EncodedString clientId)
	{
		this.clientId = clientId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#getOtherAcccountHolders()
	 */
	@Override
	public List <ClientModel> getOtherAcccountHolders()
	{
		return otherAcccountHolders;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.portfolio.web.model.PortfolioInterface#setOtherAcccountHolders(java.util.List)
	 */
	@Override
	public void setOtherAcccountHolders(List <ClientModel> otherAcccountHolders)
	{
		this.otherAcccountHolders = otherAcccountHolders;
	}

	@Override
	public String getListofMsgIds()
	{
		return listofMsgIds;
	}

	@Override
	public void setListofMsgIds(String listofMsgIds)
	{
		this.listofMsgIds = listofMsgIds;
	}

	@Override
	public List <Blockcodes> getBlockcodeList()
	{
		return blockcodeList;
	}

	@Override
	public void setBlockcodeList(List <Blockcodes> blockcodeList)
	{
		this.blockcodeList = blockcodeList;
	}

	@Override
	public String getAdviserPermission()
	{
		return adviserPermission;
	}

	@Override
	public void setAdviserPermission(String adviserPermission)
	{
		this.adviserPermission = adviserPermission;
	}

	@Override
	public String getAdviserId()
	{
		return adviserId;
	}

	@Override
	public void setAdviserId(String adviserId)
	{
		this.adviserId = adviserId;
	}

	@Override
	public String getAdviserFirstName()
	{
		return adviserFirstName;
	}

	@Override
	public void setAdviserFirstName(String adviserFirstName)
	{
		this.adviserFirstName = adviserFirstName;
	}

	@Override
	public String getAdviserLastName()
	{
		return adviserLastName;
	}

	@Override
	public void setAdviserLastName(String adviserLastName)
	{
		this.adviserLastName = adviserLastName;
	}

	public List <Person> getAccountHolders()
	{
		return accountHolders;
	}

	public void setAccountHolders(List <Person> accountHolders)
	{
		this.accountHolders = accountHolders;
	}

	public String getFormattedBalance()
	{
		return Format.asCurrency(balance);
	}
	public String getPortfolioValue() 
	{
		return portfolioValue;
	}

	public void setPortfolioValue(String portfolioValue) 
	{
		this.portfolioValue = portfolioValue;
	}




	public DateTime getAccountStartDate()
	{
		return accountStartDate;
	}

	public void setAccountStartDate(DateTime accountStartDate)
	{
		this.accountStartDate = accountStartDate;
	}

	public String getAccountStatus()
	{
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus)
	{
		this.accountStatus = accountStatus;
	}

	public void setClientPermission(Map <String, String> permissions)
	{
		this.clientPermission = permissions;
	}

	public Map <String, String> getClientPermission()
	{
		return this.clientPermission;
	}

}
