package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.service.integration.account.Biller;

@ServiceBean(xpath="reg_biller")
public class BillerPayeeImpl implements Biller
{
	@ServiceElement(xpath="reg_biller_payee_ident")
	private String billerCode;
	
	@ServiceElement(xpath="reg_biller_payee_acc")
	private String cRNNumber;
	
	@ServiceElement(xpath="reg_biller_acc_name")
	private String name;
	
	@ServiceElement(xpath="reg_biller_acc_nick_name")
	private String nickName;

    private String crnType;

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
	public String getBillerCode()
	{
		return billerCode;
	}

	@Override
	public String getCRN()
	{
		return cRNNumber;
	}

	public String getcRNNumber()
	{
		return cRNNumber;
	}

	public void setcRNNumber(String cRNNumber)
	{
		this.cRNNumber = cRNNumber;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

    public String getCRNType() {
        return crnType;
    }

    public void setCRNType(String crnType) {
        this.crnType = crnType;
    }

}
