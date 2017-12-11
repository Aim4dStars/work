package com.bt.nextgen.payments.repository;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BSB")
@Cache(region = "bsb", usage = CacheConcurrencyStrategy.READ_ONLY)
public class Bsb
{
	@Id() @Column(name = "BSB_CODE")
	private String bsbCode;

	@Column(name = "BANK_NAME")
	private String bankName;

	public Bsb()
	{
	}

	public Bsb(String bsbCode)
	{
		this.bsbCode = bsbCode;
	}

	public String getBsbCode()
	{
		return bsbCode;
	}

	public void setBsbCode(String bsbCode)
	{
		this.bsbCode = bsbCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
}
