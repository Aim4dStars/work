package com.bt.nextgen.transactions.web.model;

import java.util.Date;

import com.bt.nextgen.core.web.model.BaseObject;
import com.bt.nextgen.payments.domain.PaymentStatus;
import com.bt.nextgen.payments.web.model.PaymentModel;

public class TransactionModel extends BaseObject implements TransactionInterface
{
	private String dueDay;
	private String dueMonth;
	private String dueYear;
	private String descriptionMain;
	private String descriptionMinor;
	private String initiatedBy;
	private String credit;
	private String debit;
	private String balance;
	private PaymentStatus paymentStatus;

	private PaymentModel paymentDetails;
	private String jsonDetails;
	private boolean isSuccessful;
	private String errorCode;
	private String errorCodeStop;
	private String transactionType;
	private String portfolioId;
	private String responseMsg;
	private Date transactionDate;
	private String fromDate;
	private String toDate;
	private String tdPrincipal;
	private String brandName;
	private String brandClass;
	private String grossIncome;
	private String withHoldingTax;
	private boolean isBTCashAccWithTax;
	
	private  Date dueDate;
	
	public Date getDueDate()
	{
		return dueDate;
	}
	
	public void setDueDate(Date dueDate)
	{
		this.dueDate = dueDate;
	}

	public String getPortfolioId()
	{
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId)
	{
		this.portfolioId = portfolioId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getTransactionType()
	 */
	@Override
	public String getTransactionType()
	{
		return transactionType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setTransactionType(java.lang.String)
	 */
	@Override
	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getErrorCode()
	 */
	@Override
	public String getErrorCode()
	{
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setErrorCode(java.lang.String)
	 */
	@Override
	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getErrorCodeStop()
	 */
	@Override
	public String getErrorCodeStop()
	{
		return errorCodeStop;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setErrorCodeStop(java.lang.String)
	 */
	@Override
	public void setErrorCodeStop(String errorCodeStop)
	{
		this.errorCodeStop = errorCodeStop;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#isSuccessful()
	 */
	@Override
	public boolean isSuccessful()
	{
		return isSuccessful;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setSuccessful(boolean)
	 */
	@Override
	public void setSuccessful(boolean isSuccessful)
	{
		this.isSuccessful = isSuccessful;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getBalance()
	 */
	@Override
	public String getBalance()
	{
		return balance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getPaymentDetails()
	 */
	@Override
	public PaymentModel getPaymentDetails()
	{
		return paymentDetails;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setPaymentDetails(com.bt.nextgen.payments.web.model.PaymentModel)
	 */
	@Override
	public void setPaymentDetails(PaymentModel paymentDetails)
	{
		this.paymentDetails = paymentDetails;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setBalance(java.lang.String)
	 */
	@Override
	public void setBalance(String balance)
	{
		this.balance = balance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getJsonDetails()
	 */
	@Override
	public String getJsonDetails()
	{
		return jsonDetails;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setJsonDetails(java.lang.String)
	 */
	@Override
	public void setJsonDetails(String jsonDetails)
	{
		this.jsonDetails = jsonDetails;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getDueDay()
	 */
	@Override
	public String getDueDay()
	{
		return dueDay;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setDueDay(java.lang.String)
	 */
	@Override
	public void setDueDay(String dueDay)
	{
		this.dueDay = dueDay;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getDueMonth()
	 */
	@Override
	public String getDueMonth()
	{
		return dueMonth;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setDueMonth(java.lang.String)
	 */
	@Override
	public void setDueMonth(String dueMonth)
	{
		this.dueMonth = dueMonth;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getDueYear()
	 */
	@Override
	public String getDueYear()
	{
		return dueYear;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setDueYear(java.lang.String)
	 */
	@Override
	public void setDueYear(String dueYear)
	{
		this.dueYear = dueYear;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getInitiatedBy()
	 */
	@Override
	public String getInitiatedBy()
	{
		return initiatedBy;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setInitiatedBy(java.lang.String)
	 */
	@Override
	public void setInitiatedBy(String initiatedBy)
	{
		this.initiatedBy = initiatedBy;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getPaymentStatus()
	 */
	@Override
	public PaymentStatus getPaymentStatus()
	{
		return paymentStatus;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setPaymentStatus(com.bt.nextgen.payments.domain.PaymentStatus)
	 */
	@Override
	public void setPaymentStatus(PaymentStatus paymentStatus)
	{
		this.paymentStatus = paymentStatus;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getCredit()
	 */
	@Override
	public String getCredit()
	{
		return credit;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setCredit(java.lang.String)
	 */
	@Override
	public void setCredit(String credit)
	{
		this.credit = credit;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getDebit()
	 */
	@Override
	public String getDebit()
	{
		return debit;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setDebit(java.lang.String)
	 */
	@Override
	public void setDebit(String debit)
	{
		this.debit = debit;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getDescriptionMain()
	 */
	@Override
	public String getDescriptionMain()
	{
		return descriptionMain;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setDescriptionMain(java.lang.String)
	 */
	@Override
	public void setDescriptionMain(String descriptionMain)
	{
		this.descriptionMain = descriptionMain;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#getDescriptionMinor()
	 */
	@Override
	public String getDescriptionMinor()
	{
		return descriptionMinor;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.transactions.web.model.TransactionModelInterface#setDescriptionMinor(java.lang.String)
	 */
	@Override
	public void setDescriptionMinor(String descriptionMinor)
	{
		this.descriptionMinor = descriptionMinor;
	}

	@Override
	public String getResponseMsg()
	{

		return responseMsg;
	}

	@Override
	public void setResponseMsg(String responseMsg)
	{
		this.responseMsg = responseMsg;

	}

	@Override
	public Date getTransactionDate()
	{
		return transactionDate;
	}

	@Override
	public void setTransactionDate(Date transactionDate)
	{
		this.transactionDate = transactionDate;
	}

	public String getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getToDate()
	{
		return toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public String getTdPrincipal()
	{
		return tdPrincipal;
	}

	public void setTdPrincipal(String tdPrincipal)
	{
		this.tdPrincipal = tdPrincipal;
	}

	public String getBrandName()
	{
		return brandName;
	}

	public void setBrandName(String brandName)
	{
		this.brandName = brandName;
	}

	public String getBrandClass()
	{
		return brandClass;
	}

	public void setBrandClass(String brandClass)
	{
		this.brandClass = brandClass;
	}

	@Override
	public String getGrossIncome()
	{
		return grossIncome;
	}

	@Override
	public void setGrossIncome(String grossIncome)
	{
		this.grossIncome = grossIncome;
	}

	@Override
	public String getWithHoldingTax()
	{
		return withHoldingTax;
	}

	@Override
	public void setWithHoldingTax(String withHoldingTax)
	{
		this.withHoldingTax = withHoldingTax;
	}

	@Override
	public boolean isBTCashAccWithTax()
	{
		return isBTCashAccWithTax;
	}

	@Override
	public void setBTCashAccWithTax(boolean isBTCashAccWithTax)
	{
		this.isBTCashAccWithTax = isBTCashAccWithTax;

	}

}
