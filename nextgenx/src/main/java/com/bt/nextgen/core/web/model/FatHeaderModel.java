
package com.bt.nextgen.core.web.model;

import com.bt.nextgen.clients.web.model.ClientModel;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.portfolio.web.model.PortfolioInterface;

import java.util.Date;
import java.util.List;

public class FatHeaderModel
{

	private String accountId;
	private String adviser;
	private String bsb;
	private String accountNo;
	private String billerCode;
	private String crn;
	private String primaryContactName;
	private String primaryClientId;
	private String phoneNumber;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String totalCashBalance;
	private String availableCashBalance;
	private String idpsAccountName;
	private String productName;
	private String lastPortfolioCalculationDate;
	private String accountType;
	private String wrapAccountId;
	private String rate;
	private String incomeEarnFytd;
	private PortfolioInterface portfolioModel;
	//private CashAccountModel cashAccountModel;
	private List<ClientModel> members;
	private List<PortfolioInterface> idpsAccounts;
	private String registeredDate;
	//sets the primary holder clientId for that portfolio
	private EncodedString clientIdEncoded;
	private String adviserPhoneNumber;
	//sets the clientId present in the url
	private EncodedString clientIdFromUrl;
	private EncodedString legalClientId;
	
	public String getTotalPortfolioValue()
	{
		return portfolioModel.getBalance();
	}

	public FatHeaderModel()
	{}

	public String getAccountId()
	{
		return portfolioModel.getAccountId();
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getAdviser()
	{
		return portfolioModel.getAdviser();
	}

	public void setAdviser(String adviser)
	{
		this.adviser = adviser;
	}

	public String getBsb()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getBsb();
		}
		return null;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public String getAccountNo()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getCashAccountNumber();
		}
		return null;
	}

	public void setAccountNo(String accountNo)
	{
		this.accountNo = accountNo;
	}

	public String getBillerCode()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getBillerCode();
		}
		return null;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getCrn()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getCrn();
		}
		return null;
	}

	public void setCrn(String crn)
	{
		this.crn = crn;
	}

	public String getPrimaryContactName()
	{
		return primaryContactName;
	}

	public void setPrimaryContactName(String primaryContactName)
	{
		this.primaryContactName = primaryContactName;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getEmail()
	{
		if(portfolioModel.getPrimaryHolder() != null)
		{
			//return  portfolioModel.getPrimaryHolder().getEmail();
			return email;
		}
		return null;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getAddressLine1()
	{
		return addressLine1;
		
	}

	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	public String getTotalCashBalance()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getTotalBalance();
		}
		return null;
	}

	public void setTotalCashBalance(String totalCashBalance)
	{
		this.totalCashBalance = totalCashBalance;
	}

	public String getAvailableCashBalance()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getAvailableBalance();
		}
		return null;
	}

	public void setAvailableCashBalance(String availableCashBalance)
	{
		this.availableCashBalance = availableCashBalance;
	}


	public String getIdpsAccountName()
	{
		return portfolioModel.getAccountName();
	}

	public void setIdpsAccountName(String idpsAccountName)
	{
		this.idpsAccountName = idpsAccountName;
	}

	public String getProductName()
	{
		return portfolioModel.getProductName();
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getLastPortfolioCalculationDate()
	{
		return ApiFormatter.asShortDate(new Date());
	}

	public void setLastPortfolioCalculationDate(String lastPortfolioCalculationDate)
	{
		this.lastPortfolioCalculationDate = lastPortfolioCalculationDate;
	}

	public String getAccountType()
	{
		return portfolioModel.getAccountType();
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getWrapAccountId()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getWrapAccountId();
		}
		return null;
	}

	public void setWrapAccountId(String wrapAccountId)
	{
		this.wrapAccountId = wrapAccountId;
	}

	public List<ClientModel> getMembers()
	{
		return members;
	}

	public void setMembers(List<ClientModel> members)
	{
		this.members = members;
	}


	public String getPrimaryClientId()
	{
		return primaryClientId;
	}

	public void setPrimaryClientId(String primaryClientId)
	{
		this.primaryClientId = primaryClientId;
	}


	public  List<PortfolioInterface>  getIdpsAccounts()
	{
		return idpsAccounts;
	}

	public void setIdpsAccounts(List<PortfolioInterface>  idpsAccounts)
	{
		this.idpsAccounts = idpsAccounts;
	}

	public String getRate()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getInterestRate();
		}
		return null;
	}

	public void setRate(String rate)
	{
		this.rate = rate;
	}

	public String getIncomeEarnFytd()
	{
		if(portfolioModel.getCashAccount() != null)
		{
			return portfolioModel.getCashAccount().getFinancialYearToDate();
		}
		return null;
		
	}

	public void setIncomeEarnFytd(String incomeEarnFytd)
	{
		this.incomeEarnFytd = incomeEarnFytd;
	}

	public PortfolioInterface getPortfolioModel()
	{
		return portfolioModel;
	}

	public void setPortfolioModel(PortfolioInterface portfolioModel)
	{
		this.portfolioModel = portfolioModel;
	}

	public String getRegisteredDate()
	{
		return portfolioModel.getRegisterDate();
	}

	public void setRegisteredDate(String registeredDate) 
	{
		this.registeredDate = registeredDate;
	}

	public EncodedString getClientIdEncoded() {
		return clientIdEncoded;
	}

	public void setClientIdEncoded(EncodedString clientIdEncoded) {
		this.clientIdEncoded = clientIdEncoded;
	}

	public String getAdviserPhoneNumber()
	{
		return adviserPhoneNumber;
	}

	public void setAdviserPhoneNumber(String adviserPhoneNumber)
	{
		this.adviserPhoneNumber = adviserPhoneNumber;
	}

	public EncodedString getClientIdFromUrl() {
		return clientIdFromUrl;
	}

	public void setClientIdFromUrl(EncodedString clientIdFromUrl) {
		this.clientIdFromUrl = clientIdFromUrl;
	}
	
	public EncodedString getLegalClientId()
	{
		return legalClientId;
	}

	public void setLegalClientId(EncodedString legalClientId)
	{
		this.legalClientId = legalClientId;
	}

}
