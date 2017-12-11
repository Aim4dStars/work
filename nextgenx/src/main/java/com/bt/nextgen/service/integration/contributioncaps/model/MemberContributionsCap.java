package com.bt.nextgen.service.integration.contributioncaps.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

public interface MemberContributionsCap
{
	String getPersonId();

	void setPersonId(String personId);

	DateTime getDateOfBirth();

	void setDateOfBirth(DateTime date);

	String getAge();

	void setAge(String age);

	DateTime getFinancialYear();

	void setFinancialYear(DateTime financialYear);

	BigDecimal getConcessionalCap();

	void setConcessionalCap(BigDecimal concessionalCap);

	BigDecimal getNonConcessionalCap();

	void setNonConcessionalCap(BigDecimal nonConcessionalCap);

}
