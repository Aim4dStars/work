package com.bt.nextgen.service.integration.contributioncaps.model;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "top_head")
public class MemberContributionsCapImpl implements MemberContributionsCap
{

	@ServiceElement(xpath = "person_id/val")
	private String personId;

	@ServiceElement(xpath = "conc_amount/val", converter = BigDecimalConverter.class)
	private BigDecimal concessionalCap;

	@ServiceElement(xpath = "nconc_amount/val", converter = BigDecimalConverter.class)
	private BigDecimal nonConcessionalCap;

	@ServiceElement(xpath = "birth_date/val", converter = IsoDateTimeConverter.class)
	private DateTime dateOfBirth;

	@ServiceElement(xpath = "fy/val", converter = IsoDateTimeConverter.class)
	private DateTime financialYear;

	@ServiceElement(xpath = "person_age/val")
	private String age;

	@Override
	public String getPersonId()
	{
		// TODO Auto-generated method stub
		return personId;
	}

	@Override
	public void setPersonId(String personId)
	{
		this.personId = personId;

	}

	@Override
	public DateTime getDateOfBirth()
	{
		// TODO Auto-generated method stub
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(DateTime dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;

	}

	@Override
	public String getAge()
	{
		// TODO Auto-generated method stub
		return age;
	}

	@Override
	public void setAge(String age)
	{
		this.age = age;

	}

	@Override
	public DateTime getFinancialYear()
	{
		// TODO Auto-generated method stub
		return financialYear;
	}

	@Override
	public void setFinancialYear(DateTime financialYear)
	{
		this.financialYear = financialYear;

	}

	@Override
	public BigDecimal getConcessionalCap()
	{
		// TODO Auto-generated method stub
		return concessionalCap;
	}

	@Override
	public void setConcessionalCap(BigDecimal concessionalCap)
	{
		this.concessionalCap = concessionalCap;

	}

	@Override
	public BigDecimal getNonConcessionalCap()
	{
		// TODO Auto-generated method stub
		return nonConcessionalCap;
	}

	@Override
	public void setNonConcessionalCap(BigDecimal nonConcessionalCap)
	{
		this.concessionalCap = nonConcessionalCap;

	}

}
