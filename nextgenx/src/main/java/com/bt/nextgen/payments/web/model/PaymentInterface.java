package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.addressbook.PayeeModel;

public interface PaymentInterface {

	public abstract String getOrderId();

	public abstract void setOrderId(String orderId);

	public abstract boolean isSystemTransaction();

	public abstract void setSystemTransaction(boolean isSystemTransaction);

	public abstract String getMaccId();

	public abstract void setMaccId(String maccId);

	public abstract boolean isRecurring();

	public abstract void setRecurring(boolean recurring);

	public abstract String getRepeatEnds();

	public abstract void setRepeatEnds(String repeatEnds);

	public abstract String getFrequency();

	public abstract void setFrequency(String frequency);

	public abstract String getPaymentEndDate();

	public abstract void setPaymentEndDate(String paymentEndDate);

	public abstract String getFailureReason();

	public abstract void setFailureReason(String failureReason);

	public abstract String getLastPaymentStatus();

	public abstract void setLastPaymentStatus(String lastPaymentStatus);

	public abstract String getFirstPayment();

	public abstract void setFirstPayment(String firstPayment);

	public abstract String getLastPayment();

	public abstract void setLastPayment(String lastPayment);

	public abstract String getInitiator();

	public abstract void setInitiator(String initiator);

	public abstract String getAmount();

	public abstract void setAmount(String amount);

	public abstract String getReceiptNumber();

	public abstract PayeeModel getTo();

	public abstract void setTo(PayeeModel to);

	public abstract PayeeModel getFrom();

	public abstract void setFrom(PayeeModel from);

	public abstract void setReceiptNumber(String receiptNumber);

	public abstract String getSystemDescription();

	public abstract void setSystemDescription(String systemDescription);

	public abstract String getPayerDescription();

	public abstract void setPayerDescription(String payerDescription);

	public abstract String getDescription();

	public abstract void setDescription(String description);

	public abstract String getPaymentStatus();

	public abstract void setPaymentStatus(String paymentStatus);

	public abstract String getPaymentType();

	public abstract void setPaymentType(String paymentType);

	public abstract String getDate();

	public abstract void setDate(String date);

	public abstract String getRepeatLine1();

	public abstract void setRepeatLine1(String repeatLine1);

	public abstract String getRepeatLine2();

	public abstract void setRepeatLine2(String repeatLine2);

	public abstract String getPaymentId();

	public abstract void setPaymentId(String paymentId);

	public abstract String getCategory();

	public abstract void setCategory(String category);

	public abstract String getSubCategory();

	public abstract void setSubCategory(String subCategory);

	public abstract String getPaymentMethod();

	public abstract void setPaymentMethod(String paymentMethod);

	public abstract String getPaymentMaxCount();

	public abstract void setPaymentMaxCount(String paymentMaxCount);

	public abstract String getTransactionDate();

	public abstract void setTransactionDate(String transactionDate);
	
	public abstract String getTransactionType();
	
	public abstract void setTransactionType(String transactionType);

}