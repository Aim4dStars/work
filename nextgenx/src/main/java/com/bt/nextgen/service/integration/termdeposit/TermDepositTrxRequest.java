package com.bt.nextgen.service.integration.termdeposit;

public interface TermDepositTrxRequest
{
	String getPortfolio();
	void setPortfolio(String portfolio); // Asset or Position
	String getAsset();
	void setAsset(String asset);
	String getAmount();
	void setAmount(String amount); 
	String getRenewMode();
	void setRenewMode(String renewMode); // Pick from static code
	String getCurrencyCode();
	void setCurrencyCode(String currencyCode); // Pick from static code (1009 for AUD)
	String getRenewAmount(); 
	void setRenewAmount(String renewAmount); 
	String getRenewCount(); 
	void setRenewCount(String renewCount);
}
