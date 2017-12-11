package com.bt.nextgen.payments.repository;

import com.bt.nextgen.payments.domain.CRNType;
import com.bt.nextgen.payments.domain.PayeeType;

import javax.persistence.*;

@Entity
@Table(name = "BPAY_PAYEE")
@PrimaryKeyJoinColumn(name = "PAYEE_SEQ")
public class BpayPayee extends Payee
{
	@ManyToOne
	@JoinColumn(name = "BILLER_CODE")
	private BpayBiller biller;

	@Column(name = "CUSTOMER_REFERENCE")
	private String customerReference;

	@Transient
	@Column(name = "CRN_TYPE")
	@Enumerated(EnumType.STRING)
	private CRNType crnType;

	public BpayPayee(String cashAccountId, String nickname, BpayBiller biller, String customerReference)
	{
		super(cashAccountId, nickname, PayeeType.BPAY);
		this.biller = biller;
		this.customerReference = customerReference;
	}

	public BpayPayee()
	{
		super(PayeeType.BPAY);
	}

	public BpayBiller getBiller()
	{
		return biller;
	}

	public void setBiller(BpayBiller biller)
	{
		this.biller = biller;
	}

	public String getCustomerReference()
	{
		return customerReference;
	}

	public void setCustomerReference(String customerReference)
	{
		this.customerReference = customerReference;
	}

	@Override
	public String getName()
	{
		return getBiller().getBillerName();
	}

	@Override
	public String getCode()
	{
		return getBiller().getBillerCode();
	}

	@Override
	public String getReferenceCode()
	{
		return getCustomerReference();
	}

	public CRNType getCrnType()
	{
		return crnType;
	}

	public void setCrnType(CRNType crnType)
	{
		this.crnType = crnType;
	}
}
