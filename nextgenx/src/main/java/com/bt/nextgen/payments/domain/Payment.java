package com.bt.nextgen.payments.domain;

import com.bt.nextgen.core.domain.BaseObject;
import com.bt.nextgen.core.domain.Money;
import com.bt.nextgen.payments.repository.Payee;
import com.bt.nextgen.service.cash.request.AccountInstruction;
import org.joda.time.DateTime;

public class Payment extends BaseObject
{
	private Money amount;
	private String receiptNumber;
	private Payee to;
	private Payee from;
	private String initiator;
	private String description;
	private PaymentStatus paymentStatus;
	private PaymentType paymentType;
	private PaymentSchedule paymentSchedule = new PaymentSchedule();
	private String paymentId;
	private String category;
	private String subCategory;
	private PaymentMethod paymentMethod;
	private String failureReasonCode;
	private DateTime todayDate;
	private AccountInstruction toAccount;
	private AccountInstruction fromAccount;
	private String paymentEndDate;
	private String frequency;
	private String paymentMaxCount;
	private String repeatEnds;
	private boolean recurring;

	public AccountInstruction getToAccount() {
		return toAccount;
	}

	public void setToAccount(AccountInstruction toAccount) {
		this.toAccount = toAccount;
	}

	public AccountInstruction getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(AccountInstruction fromAccount) {
		this.fromAccount = fromAccount;
	}

	public DateTime getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(DateTime todayDate) {
		this.todayDate = todayDate;
	}

	public String getFailureReasonCode()
	{
		return failureReasonCode;
	}

	public void setFailureReasonCode(String failureReasonCode)
	{
		this.failureReasonCode = failureReasonCode;
	}

	public Money getAmount()
	{
		return amount;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getSubCategory()
	{
		return subCategory;
	}

	public void setSubCategory(String subCategory)
	{
		this.subCategory = subCategory;
	}

	public void setAmount(Money amount)
	{
		this.amount = amount;
	}

	public String getPaymentId()
	{
		return paymentId;
	}

	public PaymentMethod getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public void setPaymentId(String paymentId)
	{
		this.paymentId = paymentId;
	}

	public PaymentType getPaymentType()
	{
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}

	public PaymentSchedule getPaymentSchedule()
	{
		return paymentSchedule;
	}

	public void setPaymentSchedule(PaymentSchedule paymentSchedule)
	{
		this.paymentSchedule = paymentSchedule;
	}

	public Payee getTo()
	{
		return to;
	}

	public String getReceiptNumber()
	{
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber)
	{
		this.receiptNumber = receiptNumber;
	}

	public void setTo(Payee to)
	{
		this.to = to;
	}

	public Payee getFrom()
	{
		return from;
	}

	public void setFrom(Payee from)
	{
		this.from = from;
	}

	public String getInitiator()
	{
		return initiator;
	}

	public void setInitiator(String initiator)
	{
		this.initiator = initiator;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public PaymentStatus getPaymentStatus()
	{
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus)
	{
		this.paymentStatus = paymentStatus;
	}
	
	public String getPaymentEndDate() {
		return paymentEndDate;
	}

	public void setPaymentEndDate(String paymentEndDate) {
		this.paymentEndDate = paymentEndDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getPaymentMaxCount() {
		return paymentMaxCount;
	}

	public void setPaymentMaxCount(String paymentMaxCount) {
		this.paymentMaxCount = paymentMaxCount;
	}

	public String getRepeatEnds() {
		return repeatEnds;
	}

	public void setRepeatEnds(String repeatEnds) {
		this.repeatEnds = repeatEnds;
	}

	public boolean isRecurring() {
		return recurring;
	}

	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}

}
