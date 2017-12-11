package com.bt.nextgen.service.avaloq.payments;

import java.math.BigDecimal;
import java.util.Date;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.RecurringTransaction;
import com.bt.nextgen.service.integration.payments.BpayBiller;
import com.bt.nextgen.service.integration.payments.PaymentDetails;

@Deprecated
public class PaymentDetailsImpl implements PaymentDetails
{

	private MoneyAccountIdentifier moneyAccount;
	private PayAnyoneAccountDetails payAnyoneBeneficiary;
	private BpayBiller bpayBiller;
	private BigDecimal amount;
	private CurrencyType currencyType;
	private String receiptNumber;
	private String benefeciaryInfo;
	private Date transactionDate;
	private String payeeName;
	private Date paymentDate;
	private String businessChannel;
	private String clientIp;

	String positionId;

	/**
	 * @return the moneyAccountId
	 */
	public MoneyAccountIdentifier getMoneyAccount()
	{
		return moneyAccount;
	}

	/**
	 * @param moneyAccountId the moneyAccountId to set
	 */
	public void setMoneyAccount(MoneyAccountIdentifier moneyAccount)
	{
		this.moneyAccount = moneyAccount;
	}

	/**
	 * @return the payAnyoneBeneficiary
	 */
	public PayAnyoneAccountDetails getPayAnyoneBeneficiary()
	{
		return payAnyoneBeneficiary;
	}

	/**
	 * @param payAnyoneBeneficiary the payAnyoneBeneficiary to set
	 */
	public void setPayAnyoneBeneficiary(PayAnyoneAccountDetails payAnyoneBeneficiary)
	{
		this.payAnyoneBeneficiary = payAnyoneBeneficiary;
	}

	/**
	 * @return the bpayBiller
	 */
	public BpayBiller getBpayBiller()
	{
		return bpayBiller;
	}

	/**
	 * @param bpayBiller the bpayBiller to set
	 */
	public void setBpayBiller(BpayBiller bpayBiller)
	{
		this.bpayBiller = bpayBiller;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the currencyType
	 */
	public CurrencyType getCurrencyType()
	{
		return currencyType;
	}

	/**
	 * @param currencyType the currencyType to set
	 */
	public void setCurrencyType(CurrencyType currencyType)
	{
		this.currencyType = currencyType;
	}

	/**
	 * @return the receiptNumber
	 */
	public String getReceiptNumber()
	{
		return receiptNumber;
	}

	/**
	 * @param receiptNumber the receiptNumber to set
	 */
	public void setReceiptNumber(String receiptNumber)
	{
		this.receiptNumber = receiptNumber;
	}

	/**
	 * @return the benefeciaryInfo
	 */
	public String getBenefeciaryInfo()
	{
		return benefeciaryInfo;
	}

	/**
	 * @param benefeciaryInfo the benefeciaryInfo to set
	 */
	public void setBenefeciaryInfo(String benefeciaryInfo)
	{
		this.benefeciaryInfo = benefeciaryInfo;
	}

	/**
	 * @return the transactionDate
	 */
	public Date getTransactionDate()
	{
		return transactionDate;
	}

	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(Date transactionDate)
	{
		this.transactionDate = transactionDate;
	}

	/**
	 * @return the payeeName
	 */
	public String getPayeeName()
	{
		return payeeName;
	}

	/**
	 * @param payeeName the payeeName to set
	 */
	public void setPayeeName(String payeeName)
	{
		this.payeeName = payeeName;
	}

	/**
	 * @return the paymentDate
	 */
	public Date getPaymentDate()
	{
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate)
	{
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the positionId
	 */
	public String getPositionId()
	{
		return positionId;
	}

	/**
	 * @param positionId the positionId to set
	 */
	public void setPositionId(String positionId)
	{
		this.positionId = positionId;
	}

	public String getBusinessChannel() { return businessChannel; }

	public void setBusinessChannel(String businessChannel) { this.businessChannel = businessChannel; }

	public String getClientIp() { return clientIp; }

	public void setClientIp(String clientIp) { this.clientIp = clientIp; }
}
