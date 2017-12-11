package com.bt.nextgen.payments.domain;

public class BpayPayee extends Payee{

	private BpayBiller biller;
	private String customerReference;
	private CRNType crnType;

	public BpayBiller getBiller()
	{
		return biller;
	}

	public void setBiller(BpayBiller biller)
	{
		this.biller = biller;
	}

	public String getCustomerReference()
	{
		return customerReference;
	}

	public void setCustomerReference(String customerReference)
	{
		this.customerReference = customerReference;
	}

	public CRNType getCrnType()
	{
		return crnType;
	}

	public void setCrnType(CRNType crnType)
	{
		this.crnType = crnType;
	}
}
