package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.account.PayAnyOne;

@ServiceBean(xpath="reg_payee")
public class PayAnyOneImpl implements PayAnyOne
{
	@ServiceElement(xpath="reg_payee_payee_ident")
	private String bsb;
	
	@ServiceElement(xpath="reg_payee_payee_acc")
	private String accountNumber;
	
	@ServiceElement(xpath="reg_payee_acc_name")
	private String name;
	
	@ServiceElement(xpath="reg_payee_acc_nick_name")
	private String nickName;

	@Override
	public String getAccountNumber()
	{
		return accountNumber;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getNickName()
	{
		return nickName;
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

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

}
