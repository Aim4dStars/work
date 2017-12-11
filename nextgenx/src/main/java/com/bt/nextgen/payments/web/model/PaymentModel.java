package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.addressbook.PayeeModel;

public class PaymentModel implements PaymentInterface
{
	private String amount;
	private String receiptNumber;
	private PayeeModel to;
	private PayeeModel from;
	private String systemDescription;
	private String payerDescription;
	private String description;
	private String paymentStatus;
	private String paymentType;
	private String date;
	private String repeatLine1;
	private String repeatLine2;
	private String paymentId;
	private String category;
	private String subCategory;
	private String paymentMethod;
	private String initiator;
	private String firstPayment;
	private String lastPayment;
	private String lastPaymentStatus;
	private String failureReason;
	private String paymentEndDate;
	private String frequency;
	private String paymentMaxCount;
	private String repeatEnds;
	private boolean recurring;
	private String transactionDate;
	private String maccId;
	private boolean isSystemTransaction;
	private String orderId;
	private String transactionType;

	public String getTransactionType()
	{
		return transactionType;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}
	
	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getOrderId()
	 */
	@Override
	public String getOrderId() {
		return orderId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setOrderId(java.lang.String)
	 */
	@Override
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#isSystemTransaction()
	 */
	@Override
	public boolean isSystemTransaction() 
	{
		return isSystemTransaction;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setSystemTransaction(boolean)
	 */
	@Override
	public void setSystemTransaction(boolean isSystemTransaction)
	{
		this.isSystemTransaction = isSystemTransaction;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getMaccId()
	 */
	@Override
	public String getMaccId() {
		return maccId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setMaccId(java.lang.String)
	 */
	@Override
	public void setMaccId(String maccId) {
		this.maccId = maccId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#isRecurring()
	 */
	@Override
	public boolean isRecurring() {
		return recurring;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setRecurring(boolean)
	 */
	@Override
	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getRepeatEnds()
	 */
	@Override
	public String getRepeatEnds() {
		return repeatEnds;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setRepeatEnds(java.lang.String)
	 */
	@Override
	public void setRepeatEnds(String repeatEnds) {
		this.repeatEnds = repeatEnds;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getFrequency()
	 */
	@Override
	public String getFrequency() {
		return frequency;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setFrequency(java.lang.String)
	 */
	@Override
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPaymentEndDate()
	 */
	@Override
	public String getPaymentEndDate() {
		return paymentEndDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPaymentEndDate(java.lang.String)
	 */
	@Override
	public void setPaymentEndDate(String paymentEndDate) {
		this.paymentEndDate = paymentEndDate;
	}


	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getFailureReason()
	 */
	@Override
	public String getFailureReason()
	{
		return failureReason;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setFailureReason(java.lang.String)
	 */
	@Override
	public void setFailureReason(String failureReason)
	{
		this.failureReason = failureReason;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getLastPaymentStatus()
	 */
	@Override
	public String getLastPaymentStatus()
	{
		return lastPaymentStatus;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setLastPaymentStatus(java.lang.String)
	 */
	@Override
	public void setLastPaymentStatus(String lastPaymentStatus)
	{
		this.lastPaymentStatus = lastPaymentStatus;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getFirstPayment()
	 */
	@Override
	public String getFirstPayment()
	{
		return firstPayment;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setFirstPayment(java.lang.String)
	 */
	@Override
	public void setFirstPayment(String firstPayment)
	{
		this.firstPayment = firstPayment;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getLastPayment()
	 */
	@Override
	public String getLastPayment()
	{
		return lastPayment;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setLastPayment(java.lang.String)
	 */
	@Override
	public void setLastPayment(String lastPayment)
	{
		this.lastPayment = lastPayment;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getInitiator()
	 */
	@Override
	public String getInitiator()
	{
		return initiator;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setInitiator(java.lang.String)
	 */
	@Override
	public void setInitiator(String initiator)
	{
		this.initiator = initiator;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getAmount()
	 */
	@Override
	public String getAmount()
	{
		return amount;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setAmount(java.lang.String)
	 */
	@Override
	public void setAmount(String amount)
	{
		this.amount = amount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getReceiptNumber()
	 */
	@Override
	public String getReceiptNumber()
	{
		return receiptNumber;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getTo()
	 */
	@Override
	public PayeeModel getTo()
	{
		return to;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setTo(com.bt.nextgen.addressbook.PayeeModel)
	 */
	@Override
	public void setTo(PayeeModel to)
	{
		this.to = to;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getFrom()
	 */
	@Override
	public PayeeModel getFrom()
	{
		return from;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setFrom(com.bt.nextgen.addressbook.PayeeModel)
	 */
	@Override
	public void setFrom(PayeeModel from)
	{
		this.from = from;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setReceiptNumber(java.lang.String)
	 */
	@Override
	public void setReceiptNumber(String receiptNumber)
	{
		this.receiptNumber = receiptNumber;
	}


	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getSystemDescription()
	 */
	@Override
	public String getSystemDescription()
	{
		return systemDescription;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setSystemDescription(java.lang.String)
	 */
	@Override
	public void setSystemDescription(String systemDescription)
	{
		this.systemDescription = systemDescription;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPayerDescription()
	 */
	@Override
	public String getPayerDescription()
	{
		return payerDescription;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPayerDescription(java.lang.String)
	 */
	@Override
	public void setPayerDescription(String payerDescription)
	{
		this.payerDescription = payerDescription;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description)
	{
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPaymentStatus()
	 */
	@Override
	public String getPaymentStatus()
	{
		return paymentStatus;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPaymentStatus(java.lang.String)
	 */
	@Override
	public void setPaymentStatus(String paymentStatus)
	{
		this.paymentStatus = paymentStatus;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPaymentType()
	 */
	@Override
	public String getPaymentType()
	{
		return paymentType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPaymentType(java.lang.String)
	 */
	@Override
	public void setPaymentType(String paymentType)
	{
		this.paymentType = paymentType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getDate()
	 */
	@Override
	public String getDate()
	{
		return date;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setDate(java.lang.String)
	 */
	@Override
	public void setDate(String date)
	{
		this.date = date;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getRepeatLine1()
	 */
	@Override
	public String getRepeatLine1()
	{
		return repeatLine1;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setRepeatLine1(java.lang.String)
	 */
	@Override
	public void setRepeatLine1(String repeatLine1)
	{
		this.repeatLine1 = repeatLine1;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getRepeatLine2()
	 */
	@Override
	public String getRepeatLine2()
	{
		return repeatLine2;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setRepeatLine2(java.lang.String)
	 */
	@Override
	public void setRepeatLine2(String repeatLine2)
	{
		this.repeatLine2 = repeatLine2;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPaymentId()
	 */
	@Override
	public String getPaymentId()
	{
		return paymentId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPaymentId(java.lang.String)
	 */
	@Override
	public void setPaymentId(String paymentId)
	{
		this.paymentId = paymentId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getCategory()
	 */
	@Override
	public String getCategory()
	{
		return category;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setCategory(java.lang.String)
	 */
	@Override
	public void setCategory(String category)
	{
		this.category = category;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getSubCategory()
	 */
	@Override
	public String getSubCategory()
	{
		return subCategory;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setSubCategory(java.lang.String)
	 */
	@Override
	public void setSubCategory(String subCategory)
	{
		this.subCategory = subCategory;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPaymentMethod()
	 */
	@Override
	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPaymentMethod(java.lang.String)
	 */
	@Override
	public void setPaymentMethod(String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getPaymentMaxCount()
	 */
	@Override
	public String getPaymentMaxCount() {
		return paymentMaxCount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setPaymentMaxCount(java.lang.String)
	 */
	@Override
	public void setPaymentMaxCount(String paymentMaxCount) {
		this.paymentMaxCount = paymentMaxCount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#getTransactionDate()
	 */
	@Override
	public String getTransactionDate() {
		return transactionDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.PaymentInterface#setTransactionDate(java.lang.String)
	 */
	@Override
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}


}
