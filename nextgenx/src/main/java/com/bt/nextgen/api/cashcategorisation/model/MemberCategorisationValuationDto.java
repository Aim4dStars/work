package com.bt.nextgen.api.cashcategorisation.model;


import com.bt.nextgen.core.api.model.Dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MemberCategorisationValuationDto implements Dto
{
	private String personId;

	private String firstName;

	private String lastName;


	private List<CategorisedTransactionValuationDto> categorisations = new ArrayList<>();

	private int numberOfTransactions;

	public String getPersonId()
	{
		return personId;
	}

	public void setPersonId(String personId)
	{
		this.personId = personId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@JsonProperty("categorisedTransactionValuation")
	public List<CategorisedTransactionValuationDto> getCategorisedTransactionValuation()
	{
		return categorisations;
	}

	public void setCategorisations(List<CategorisedTransactionValuationDto> categorisations)
	{
		this.categorisations = categorisations;
	}

	@Override
	public String getType()
	{
		return "MemberCategorisationValuationDto";
	}

	public BigDecimal getTotalAmount()
	{
		BigDecimal amount = BigDecimal.ZERO;

		for (CategorisedTransactionValuationDto categorisation : categorisations)
		{
			amount = amount.add(categorisation.getTotalAmount());
		}

		return amount;
	}

	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}
}
