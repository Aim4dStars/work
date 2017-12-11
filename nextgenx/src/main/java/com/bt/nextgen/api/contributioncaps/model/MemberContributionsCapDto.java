package com.bt.nextgen.api.contributioncaps.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Superannuation concessional and non concessional cap limits for an smsf member
 */
public class MemberContributionsCapDto extends BaseDto
{
	private String personId;

	private String dateOfBirth;

	private int age;

	private String financialYear;

	private BigDecimal concessionalCap;

	private BigDecimal nonConcessionalCap;

	public String getPersonId()
	{
		return personId;
	}

	public void setPersonId(String personId)
	{
		this.personId = personId;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public String getFinancialYear()
	{
		return financialYear;
	}

	public void setFinancialYear(String financialYear)
	{
		this.financialYear = financialYear;
	}

	public BigDecimal getConcessionalCap()
	{
		return concessionalCap;
	}

	public void setConcessionalCap(BigDecimal concessionalCap)
	{
		this.concessionalCap = concessionalCap;
	}

	public BigDecimal getNonConcessionalCap()
	{
		return nonConcessionalCap;
	}

	public void setNonConcessionalCap(BigDecimal nonConcessionalCap)
	{
		this.nonConcessionalCap = nonConcessionalCap;
	}
}