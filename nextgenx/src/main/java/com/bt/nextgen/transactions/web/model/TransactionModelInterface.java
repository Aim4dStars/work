package com.bt.nextgen.transactions.web.model;

import com.bt.nextgen.payments.domain.PaymentStatus;
import com.bt.nextgen.payments.web.model.PaymentModel;

//Use TransactionInterface Instead.
@Deprecated
public interface TransactionModelInterface {

	public abstract String getTransactionType();

	public abstract void setTransactionType(String transactionType);

	public abstract String getErrorCode();

	public abstract void setErrorCode(String errorCode);
	
	public abstract String getResponseMsg();
	
	public abstract void setResponseMsg(String responseMsg);

	public abstract String getErrorCodeStop();

	public abstract void setErrorCodeStop(String errorCodeStop);

	public abstract boolean isSuccessful();

	public abstract void setSuccessful(boolean isSuccessful);

	public abstract String getBalance();

	public abstract PaymentModel getPaymentDetails();

	public abstract void setPaymentDetails(PaymentModel paymentDetails);

	public abstract void setBalance(String balance);

	public abstract String getJsonDetails();

	public abstract void setJsonDetails(String jsonDetails);

	public abstract String getDueDay();

	public abstract void setDueDay(String dueDay);

	public abstract String getDueMonth();

	public abstract void setDueMonth(String dueMonth);

	public abstract String getDueYear();

	public abstract void setDueYear(String dueYear);

	public abstract String getInitiatedBy();

	public abstract void setInitiatedBy(String initiatedBy);

	public abstract PaymentStatus getPaymentStatus();

	public abstract void setPaymentStatus(PaymentStatus paymentStatus);

	public abstract String getCredit();

	public abstract void setCredit(String credit);

	public abstract String getDebit();

	public abstract void setDebit(String debit);

	public abstract String getDescriptionMain();

	public abstract void setDescriptionMain(String descriptionMain);

	public abstract String getDescriptionMinor();

	public abstract void setDescriptionMinor(String descriptionMinor);
	
	public abstract String getPortfolioId();
	
	public abstract void setPortfolioId(String portfolioId);

}