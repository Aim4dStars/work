package com.bt.nextgen.payments.repository;

import com.bt.nextgen.payments.domain.PayeeType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PAYEE")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Payee implements Serializable
{
	@Id() @Column(name = "PAYEE_SEQ") @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYEE_SEQ")
	@SequenceGenerator(name = "PAYEE_SEQ", sequenceName = "PAYEE_SEQ")
	private long id;

	@Column(name = "CASH_ACCOUNT_ID")
	private String cashAccountId;

	@Column(name = "NICKNAME")
	private String nickname;

	@Column(name = "PAYEE_TYPE") @Enumerated(EnumType.STRING)
	private PayeeType payeeType;

	public Payee(String cashAccountId, String nickname, PayeeType payeeType)
	{
		this();
		this.cashAccountId = cashAccountId;
		this.nickname = nickname;
		this.payeeType = payeeType;
	}

	public Payee(PayeeType payeeType)
	{
		this();
		this.payeeType = payeeType;
	}

	public Payee()
	{
		super();
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getCashAccountId()
	{
		return cashAccountId;
	}

	public void setCashAccountId(String cashAccountId)
	{
		this.cashAccountId = cashAccountId;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public PayeeType getPayeeType()
	{
		return payeeType;
	}

	public void setPayeeType(PayeeType payeeType)
	{
		this.payeeType = payeeType;
	}

	/**
	 * This is the name of the payee as to be displayed.
	 *
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * This is the code of the payee - either bsb or biller code currently
	 *
	 * @return
	 */
	public abstract String getCode();

	/**
	 * This is the customer reference for this payee - currently either account no or customer reference number;
	 *
	 * @return
	 */
	public abstract String getReferenceCode();
}
