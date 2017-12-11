package com.bt.nextgen.service.integration.payments;

import java.math.BigDecimal;
import java.util.Date;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.RecurringTransaction;

@Deprecated
public interface PaymentDetails
{

	MoneyAccountIdentifier getMoneyAccount();

	void setMoneyAccount(MoneyAccountIdentifier moneyAccount);

	PayAnyoneAccountDetails getPayAnyoneBeneficiary();

	void setPayAnyoneBeneficiary(PayAnyoneAccountDetails payAnyoneBeneficiary);

	BpayBiller getBpayBiller();

	void setBpayBiller(BpayBiller bpayBiller);

	BigDecimal getAmount();

	void setAmount(BigDecimal amount);

	CurrencyType getCurrencyType();

	void setCurrencyType(CurrencyType currencyType);

	void setReceiptNumber(String receiptNumber);

	String getReceiptNumber();

	String getBenefeciaryInfo();

	void setBenefeciaryInfo(String benefeciaryInfo);

	Date getTransactionDate();

	void setTransactionDate(Date transactionaDate);

	String getPayeeName();

	void setPayeeName(String payeeName);

	Date getPaymentDate();

	void setPaymentDate(Date paymentDate);

	// Position Id Used for cancellation of the Standing order payments

	public String getPositionId();

	public void setPositionId(String positionId);

	void setBusinessChannel(String businessChannel);

	String getBusinessChannel();

	void setClientIp(String clientIp);

	String getClientIp();

}
