package com.bt.nextgen.payments.repository;

import com.bt.nextgen.payments.domain.CRNType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "BPAY_BILLER")
@Cache(region = "bpayBiller", usage = CacheConcurrencyStrategy.READ_ONLY)
public class BpayBiller implements Serializable
{
	@Id()
	@Column(name = "BILLER_CODE")
	private String billerCode;

	@Column(name = "BILLER_NAME")
	private String billerName;

	@Column(name = "CRN_CHECK_DIGIT_ROUTINE")
	private String crnCheckDigitRoutine;

	@Column(name = "CRN_FIXED_DIGITS_MASK")
	private String crnFixedDigitsMask;

	@Column(name = "CRN_VALID_LENGTHS")
	private String crnValidLengths;

	@Transient
	@Column(name = "CRN_TYPE")
	@Enumerated(EnumType.STRING)
	private CRNType crnType;

	public BpayBiller()
	{}

	public BpayBiller(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getBillerCode()
	{
		return billerCode;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getBillerName()
	{
		return billerName;
	}

	public void setBillerName(String billerName)
	{
		this.billerName = billerName;
	}

	public String getCrnCheckDigitRoutine()
	{
		return crnCheckDigitRoutine;
	}

	public void setCrnCheckDigitRoutine(String crnCheckDigitRoutine)
	{
		this.crnCheckDigitRoutine = crnCheckDigitRoutine;
	}

	public String getCrnFixedDigitsMask()
	{
		return crnFixedDigitsMask != null ? crnFixedDigitsMask : "                    ";
	}

	public void setCrnFixedDigitsMask(String crnFixedDigitsMask)
	{
		this.crnFixedDigitsMask = crnFixedDigitsMask;
	}

	public String getCrnValidLengths()
	{
		return crnValidLengths;
	}

	public void setCrnValidLengths(String crnValidLengths)
	{
		this.crnValidLengths = crnValidLengths;
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
