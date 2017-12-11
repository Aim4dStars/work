package com.bt.nextgen.service.avaloq.payments;

import com.bt.nextgen.service.integration.payments.BpayBiller;

@Deprecated
public class BpayBillerImpl implements BpayBiller
{

	private String billerCode;
	private String customerReferenceNo;
	private String payeeName;
	private String nickName;

	public String getPayeeName()
	{
		return payeeName;
	}

	public void setPayeeName(String payeeName)
	{
		this.payeeName = payeeName;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	/**
	 * @return the billerCode
	 */
	public String getBillerCode()
	{
		return billerCode;
	}

	/**
	 * @param billerCode the billerCode to set
	 */
	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	/**
	 * @return the customerReferenceNo
	 */
	public String getCustomerReferenceNo()
	{
		return customerReferenceNo;
	}

	/**
	 * @param customerReferenceNo the customerReferenceNo to set
	 */
	public void setCustomerReferenceNo(String customerReferenceNo)
	{
		this.customerReferenceNo = customerReferenceNo;
	}

}
