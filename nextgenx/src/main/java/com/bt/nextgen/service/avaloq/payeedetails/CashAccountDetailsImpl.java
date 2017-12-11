package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.payeedetails.CashAccountDetails;

@ServiceBean(xpath="bp_head")
public class CashAccountDetailsImpl implements CashAccountDetails
{
	@ServiceElement(xpath="bp/val")
	private String accountName;

	@ServiceElement(xpath="bp_nr/val")
	private String accountNumber;
	
	@ServiceElement(xpath="dir_cont_bsb/val")
	private String bsb;
	
	@ServiceElement(xpath="dir_cont_bcode/val")
	private String billerCode;

	@Override
	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}
	
	@Override
	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	@Override
	public String getBsb()
	{
		return bsb;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	@Override
	public String getBillerCode()
	{
		return billerCode;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}
}
