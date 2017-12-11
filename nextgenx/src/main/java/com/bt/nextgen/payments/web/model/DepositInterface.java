package com.bt.nextgen.payments.web.model;

import java.math.BigDecimal;


import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface DepositInterface {

	public abstract String getPaymentToken();

	public abstract String getPayName();

	public abstract String getToName();

	public abstract void setToName(String toName);

	public abstract String getToBsb();

	public abstract void setToBsb(String toBsb);

	public abstract String getToAccount();

	public abstract void setToAccount(String toAccount);

	public abstract String getPayCode();

	public abstract void setPaymentToken(String paymentToken);

	public abstract String getPayReference();

	@JsonIgnore
	public abstract BigDecimal getAmount();

	/**
	 * Used when serialising this object to json, allows formatting of the amount
	 * field correctly.
	 *
	 * @return
	 */
	@JsonProperty("amount")
	public abstract String getFormattedAmount();

	public abstract String getDate();

	public abstract void setDate(String date);

	public abstract String getDescription();

	public abstract String getRepeatLine1();

	public abstract String getRepeatLine2();

	public abstract String getEndRepeat();

	public abstract boolean isRecurring();

	public abstract String getPaymentState();

	public abstract void setPaymentState(String paymentState);

	public abstract MoveMoneyModel getConversation();

	public abstract void setConversation(MoveMoneyModel conversation);

	public abstract PayeeModel getDepositAccount();

	public abstract void setDepositAccount(PayeeModel depositAccount);

	public abstract CashAccountModel getAccount();

	public abstract void setAccount(CashAccountModel account);

	public abstract void setAmount(BigDecimal amount);

	public abstract String getPayType();

	public abstract void setPayType(String payType);

	public abstract String getPayeeDescription();

	public abstract void setPayeeDescription(String payeeDescription);

	public abstract String getPaymentDate();

	public abstract void setPaymentDate(String paymentDate);

	public abstract String getPaymentFrequency();

	public abstract void setPaymentFrequency(String paymentFrequency);

	public abstract String getFrequency();

	public abstract void setFrequency(String frequency);

	public abstract String getPaymentEndDate();

	public abstract void setPaymentEndDate(String paymentEndDate);

	public abstract String getRepeatEnds();

	public abstract void setRepeatEnds(String repeatEnds);

	public abstract String getPaymentMaxCount();

	public abstract void setPaymentMaxCount(String paymentMaxCount);

	public abstract String getRecieptNumber();

	public abstract void setRecieptNumber(String recieptNumber);

	public abstract String getMaccId();

	public abstract void setMaccId(String maccId);

	public abstract String getPortfolioId(); 
	
	public abstract void setPortfolioId(String portfolioId);
	
}