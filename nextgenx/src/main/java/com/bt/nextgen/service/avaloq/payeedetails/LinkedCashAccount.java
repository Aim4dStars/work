package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath="cont")
public class LinkedCashAccount
{
	@ServiceElement(xpath="cont_type_id/val", converter=ContainerTypeConverter.class)
	private String cashAccountType;
	
	@ServiceElement(xpath="macc_list/macc/macc_id/val")
	private String maccId;
	
	public String getCashAccountType()
	{
		return cashAccountType;
	}
	public void setCashAccountType(String cashAccountType)
	{
		this.cashAccountType = cashAccountType;
	}
	public String getMaccId()
	{
		return maccId;
	}
	public void setMaccId(String maccId)
	{
		this.maccId = maccId;
	}
	
}
