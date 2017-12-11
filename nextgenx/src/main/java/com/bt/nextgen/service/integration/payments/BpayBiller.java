package com.bt.nextgen.service.integration.payments;

@Deprecated
public interface BpayBiller
{
	String getBillerCode();

	void setBillerCode(String billercode);

	String getCustomerReferenceNo();

	void setCustomerReferenceNo(String customerReferenceNo);

    public void setPayeeName(String payeeName);

}
