package com.bt.nextgen.api.cashcategorisation.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class CategorisedTransactionDto extends BaseDto
{
	private String personId;

	private String contributionSubType;

	private BigDecimal amount;

	private String date;

	private String description;


	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getContributionSubType() {
		return contributionSubType;
	}

	public void setContributionSubType(String contributionSubType) {
		this.contributionSubType = contributionSubType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}