package com.bt.nextgen.api.cashcategorisation.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CategorisedTransactionValuationDto
{
	private String category;

	private List<CategorisedTransactionDto> categorisedTransactions = new ArrayList<>();


	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	@JsonProperty("categorisedTransactionDto")
	public List<CategorisedTransactionDto> getCategorisedTransactionDto()
	{
		return categorisedTransactions;
	}

	public void setCategorisedTransactions(List<CategorisedTransactionDto> categorisedTransactions)
	{
		this.categorisedTransactions = categorisedTransactions;
	}

	public BigDecimal getTotalAmount()
	{
		BigDecimal total = BigDecimal.ZERO;

		for (CategorisedTransactionDto transaction : categorisedTransactions)
		{
			total = total.add(transaction.getAmount());
		}

		return total;
	}
}