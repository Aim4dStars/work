package com.bt.nextgen.service.avaloq;

import java.math.BigDecimal;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.service.ServiceErrors;

public interface PaymentRequest
{
	public String getPortfolioId();
	public void setPortfolioId(String portfolioId);
	public String getPaymentId();
	public void setPaymentId(String paymentId);
	public BigDecimal getLimit();
	public void setLimit(BigDecimal limit);
	public PayeeType getPayeeType();
	public void setPayeeType(PayeeType payeeType);
}
