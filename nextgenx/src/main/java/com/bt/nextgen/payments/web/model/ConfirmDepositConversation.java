package com.bt.nextgen.payments.web.model;

import java.math.BigDecimal;
import java.util.Date;

import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.core.domain.BaseObject;
import com.bt.nextgen.core.web.Format;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class ConfirmDepositConversation extends BaseObject implements DepositInterface
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3717531155906189475L;
private String toName;
	private String toBsb;
	private String toAccount;
	private String payType;
	private String payName;
	private String payCode;
	private String payReference;
	private BigDecimal amount;
	private String date;
	private String repeatLine1;
	private String repeatLine2;
	private boolean recurring;
	private String endRepeat;
	private String payeeDescription;
	private String description;
	private String paymentToken;
	private String paymentState;
	private MoveMoneyModel conversation;
	private PayeeModel depositAccount;
	private CashAccountModel account;
	private String paymentMaxCount;
	private String frequency;
	private String paymentFrequency;
	private String paymentDate;
	private String recieptNumber;
	private String maccId;
	// Added for clearing cache on deposit.
	private String portfolioId;

	public ConfirmDepositConversation()
	{}

	public ConfirmDepositConversation(MoveMoneyModel conversation, PayeeModel depositAccount, CashAccountModel account,DateTime bankdate,
		String token)
	{
		this.conversation = conversation;
		this.depositAccount = depositAccount;
		this.account = account;
		this.paymentToken = token;
		//To Account Id of Investor or Advisor
		toName = account.getIdpsAccountName();
		String bsb = account.getBsb();
		if (bsb != null && bsb.length() == 6)
		{
			bsb = bsb.substring(0, 3) + "-" + bsb.substring(3, 6);
		}
		toBsb = bsb;
		toAccount = account.getCashAccountNumber();
		maccId = account.getMaccId();

		//Other Linked Account of Investor or Advisor
		if (depositAccount != null)
		{
			payType = depositAccount.getPayeeType().name();

			payName = depositAccount.getName();
			payCode = ApiFormatter.formatBsb(depositAccount.getCode());
			payReference = depositAccount.getReference();
		}
		amount = conversation.getAmount();

		date = conversation.getDate();

		endRepeat = conversation.getEndRepeat().toString();
		recurring = conversation.isRecurring();

		if (conversation.isRecurring())
		{
			setPaymentFrequency(conversation.getFrequency().getName());

			repeatLine1 = paymentFrequency;

			switch (conversation.getEndRepeat())
			{
				case REPEAT_END_DATE:
					repeatLine2 = "ends on " + conversation.getRepeatEndDate();
					setRepeatEnds(PaymentRepeatsEnd.REPEAT_END_DATE.name());
					setPaymentEndDate(conversation.getRepeatEndDate());
					break;
				case REPEAT_NO_END:
					repeatLine2 = "no end date";
					setRepeatEnds(PaymentRepeatsEnd.REPEAT_NO_END.name());
					break;
				case REPEAT_NUMBER:
					repeatLine2 = "ends after " + conversation.getRepeatNumber() + " repeats";
					setRepeatEnds(PaymentRepeatsEnd.REPEAT_NUMBER.name());
					setPaymentMaxCount(conversation.getRepeatNumber());
					break;
			}
		}

		Date date = new Date(conversation.getDate());
		if (date.after(bankdate.toDate()) && !conversation.isRecurring())
		{
			setPaymentFrequency("Once");
		}

		setPaymentDate(conversation.getDate());
		description = conversation.getDescription();
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPaymentToken()
	 */
	@Override
	public String getPaymentToken()
	{
		return paymentToken;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPayName()
	 */
	@Override
	public String getPayName()
	{
		return payName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getToName()
	 */
	@Override
	public String getToName()
	{
		return toName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setToName(java.lang.String)
	 */
	@Override
	public void setToName(String toName)
	{
		this.toName = toName;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getToBsb()
	 */
	@Override
	public String getToBsb()
	{
		return toBsb;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setToBsb(java.lang.String)
	 */
	@Override
	public void setToBsb(String toBsb)
	{
		this.toBsb = toBsb;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getToAccount()
	 */
	@Override
	public String getToAccount()
	{
		return toAccount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setToAccount(java.lang.String)
	 */
	@Override
	public void setToAccount(String toAccount)
	{
		this.toAccount = toAccount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPayCode()
	 */
	@Override
	public String getPayCode()
	{
		return payCode;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPaymentToken(java.lang.String)
	 */
	@Override
	public void setPaymentToken(String paymentToken)
	{
		this.paymentToken = paymentToken;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPayReference()
	 */
	@Override
	public String getPayReference()
	{
		return payReference;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getAmount()
	 */
	@Override
	@JsonIgnore
	public BigDecimal getAmount()
	{
		return amount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getFormattedAmount()
	 */
	@Override
	@JsonProperty("amount")
	public String getFormattedAmount()
	{
		return Format.asCurrency(getAmount());
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getDate()
	 */
	@Override
	public String getDate()
	{
		return date;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setDate(java.lang.String)
	 */
	@Override
	public void setDate(String date)
	{
		this.date = date;
	}

	/*	public String getPayeeDescription()
		{
			return payeeDescription;
		}*/

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getRepeatLine1()
	 */
	@Override
	public String getRepeatLine1()
	{
		return repeatLine1;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getRepeatLine2()
	 */
	@Override
	public String getRepeatLine2()
	{
		return repeatLine2;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getEndRepeat()
	 */
	@Override
	public String getEndRepeat()
	{
		return endRepeat;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#isRecurring()
	 */
	@Override
	public boolean isRecurring()
	{
		return recurring;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPaymentState()
	 */
	@Override
	public String getPaymentState()
	{
		return paymentState;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPaymentState(java.lang.String)
	 */
	@Override
	public void setPaymentState(String paymentState)
	{
		this.paymentState = paymentState;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getConversation()
	 */
	@Override
	public MoveMoneyModel getConversation()
	{
		return conversation;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setConversation(com.bt.nextgen.payments.web.model.MoveMoneyModel)
	 */
	@Override
	public void setConversation(MoveMoneyModel conversation)
	{
		this.conversation = conversation;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getDepositAccount()
	 */
	@Override
	public PayeeModel getDepositAccount()
	{
		return depositAccount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setDepositAccount(com.bt.nextgen.addressbook.PayeeModel)
	 */
	@Override
	public void setDepositAccount(PayeeModel depositAccount)
	{
		this.depositAccount = depositAccount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getAccount()
	 */
	@Override
	public CashAccountModel getAccount()
	{
		return account;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setAccount(com.bt.nextgen.portfolio.web.model.CashAccountModel)
	 */
	@Override
	public void setAccount(CashAccountModel account)
	{
		this.account = account;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setAmount(java.math.BigDecimal)
	 */
	@Override
	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPayType()
	 */
	@Override
	public String getPayType()
	{
		return payType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPayType(java.lang.String)
	 */
	@Override
	public void setPayType(String payType)
	{
		this.payType = payType;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPayeeDescription()
	 */
	@Override
	public String getPayeeDescription()
	{
		return payeeDescription;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPayeeDescription(java.lang.String)
	 */
	@Override
	public void setPayeeDescription(String payeeDescription)
	{
		this.payeeDescription = payeeDescription;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPaymentDate()
	 */
	@Override
	public String getPaymentDate()
	{
		return paymentDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPaymentDate(java.lang.String)
	 */
	@Override
	public void setPaymentDate(String paymentDate)
	{
		this.paymentDate = paymentDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPaymentFrequency()
	 */
	@Override
	public String getPaymentFrequency()
	{
		return paymentFrequency;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPaymentFrequency(java.lang.String)
	 */
	@Override
	public void setPaymentFrequency(String paymentFrequency)
	{
		this.paymentFrequency = paymentFrequency;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getFrequency()
	 */
	@Override
	public String getFrequency()
	{
		return frequency;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setFrequency(java.lang.String)
	 */
	@Override
	public void setFrequency(String frequency)
	{
		this.frequency = frequency;
	}

	private String paymentEndDate;

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPaymentEndDate()
	 */
	@Override
	public String getPaymentEndDate()
	{
		return paymentEndDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPaymentEndDate(java.lang.String)
	 */
	@Override
	public void setPaymentEndDate(String paymentEndDate)
	{
		this.paymentEndDate = paymentEndDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getRepeatEnds()
	 */
	@Override
	public String getRepeatEnds()
	{
		return repeatEnds;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setRepeatEnds(java.lang.String)
	 */
	@Override
	public void setRepeatEnds(String repeatEnds)
	{
		this.repeatEnds = repeatEnds;
	}

	private String repeatEnds;

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getPaymentMaxCount()
	 */
	@Override
	public String getPaymentMaxCount()
	{
		return paymentMaxCount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setPaymentMaxCount(java.lang.String)
	 */
	@Override
	public void setPaymentMaxCount(String paymentMaxCount)
	{
		this.paymentMaxCount = paymentMaxCount;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getRecieptNumber()
	 */
	@Override
	public String getRecieptNumber()
	{
		return recieptNumber;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setRecieptNumber(java.lang.String)
	 */
	@Override
	public void setRecieptNumber(String recieptNumber)
	{
		this.recieptNumber = recieptNumber;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#getMaccId()
	 */
	@Override
	public String getMaccId()
	{
		return maccId;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.payments.web.model.DepositInterface#setMaccId(java.lang.String)
	 */
	@Override
	public void setMaccId(String maccId)
	{
		this.maccId = maccId;
	}

	public String getPortfolioId() 
	{
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) 
	{
		this.portfolioId = portfolioId;
	}
}
