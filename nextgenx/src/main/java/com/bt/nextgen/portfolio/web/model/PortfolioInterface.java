package com.bt.nextgen.portfolio.web.model;

import com.bt.nextgen.blockcodes.model.Blockcodes;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.termdeposit.web.model.TermDepositAccountModel;
import com.bt.nextgen.termdeposit.web.model.TermDepositModel;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Deprecated
public interface PortfolioInterface
{
	String getGenerateDate();

	void setGenerateDate(String generateDate);

	String getCurrentDate();

	void setCurrentDate(String currentDate);

	String getFmtCurrentDate();

	void setFmtCurrentDate(String fmtCurrentDate);

	String getTotalTermDepositBalance();

	void setTotalTermDepositBalance(String totalTermDepositBalance);

	String getDollarAmount();

	String getCentAmount();

	TermDepositModel getTermDepositModel();

	void setTermDepositModel(TermDepositModel termDepositModel);

	String getBalance();

	String getPortfolioBalance();

	DateTime getAccountStartDate();

	BigDecimal getRawBalance();

	void setBalance(BigDecimal balance);

	void setPortfolioBalance(String PortfolioBalance);

	String getAdviser();

	void setAdviser(String adviser);

	String getAccountName();

	void setAccountName(String accountName);

	String getAccountId();

	void setAccountId(String accountId);

	String getAccountType();

	void setAccountType(String accountType);

	ClientModel getPrimaryHolder();

	void setPrimaryHolder(ClientModel primaryHolder);

	String getHolderPermission();

	void setHolderPermission(String holderPermission);

	String getRegisterDate();

	void setRegisterDate(String registerDate);


	/**
	 * I think this needs to be deprecated, what a mess
	 * @return
	 */
	@Deprecated
	String getPortfolio();

	List <CashAccountModel> getCashAccounts();

	void setCashAccounts(List <CashAccountModel> cashAccounts);

	List <PhoneModel> getMobileList();

	void setMobileList(List <PhoneModel> mobileList);

	List <TermDepositAccountModel> getTermDepositAccounts();

	void setTermDepositAccounts(List <TermDepositAccountModel> termDepositAccounts);

	String getTotalUnreadNessages();

	void setTotalUnreadNessages(String totalUnreadNessages);

	EncodedString getPortfolioId();

	void setPortfolioId(EncodedString portfolioId);

	String getProductName();

	void setProductName(String productName);

	String getPrimaryContact();

	void setPrimaryContact(String primaryContact);

	CashAccountModel getCashAccount();

	void setCashAccount(CashAccountModel cashAccount);

	EncodedString getClientId();

	void setClientId(EncodedString clientId);

	List <ClientModel> getOtherAcccountHolders();

	void setOtherAcccountHolders(List <ClientModel> otherAcccountHolders);

	String getListofMsgIds();

	void setListofMsgIds(String listofMsgIds);

	List <Blockcodes> getBlockcodeList();

	void setBlockcodeList(List <Blockcodes> blockcodeList);

	String getAdviserPermission();

	void setAdviserPermission(String adviserPermission);

	String getAdviserId();

	void setAdviserId(String adviserId);

	String getAdviserFirstName();

	void setAdviserFirstName(String adviserFirstName);

	String getAdviserLastName();

	void setAdviserLastName(String adviserLastName);

	/**
	 * Set permission of client on the Account (avaloqpermission id, permissions)
	 * @param permissions
	 */
	public void setClientPermission(Map <String, String> permissions);

	/**
	 * get permission of client on the Account (avaloqpermission id, permissions)
	 */
	public Map <String, String> getClientPermission();

    //TODO - evaluate if this is at the correct level
	public String getFormattedBalance();
	 
	String getLegalClientPersonId();
	
	void setLegalClientPersonId(String legalClientPersonId);
	
}