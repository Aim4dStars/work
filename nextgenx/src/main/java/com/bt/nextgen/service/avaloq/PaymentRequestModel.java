package com.bt.nextgen.service.avaloq;

import java.math.BigDecimal;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.service.ServiceErrors;

public class PaymentRequestModel implements PaymentRequest
{
	String portfolioId;
	String paymentId;
	BigDecimal limit;
	PayeeType payeeType;
	
	public String getPortfolioId()
	{
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId)
	{
		this.portfolioId = portfolioId;
	}	
	
	public String getPaymentId()
	{
		return paymentId;
	}
	public void setPaymentId(String paymentId)
	{
		this.paymentId = paymentId;
	}
	public BigDecimal getLimit()
	{
		return limit;
	}
	public void setLimit(BigDecimal limit)
	{
		this.limit = limit;
	}
	public PayeeType getPayeeType()
	{
		return payeeType;
	}
	public void setPayeeType(PayeeType payeeType)
	{
		this.payeeType = payeeType;
	}
	
}
