package com.bt.nextgen.service.integration.cashcategorisation.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

public class MemberContributionDto extends BaseDto
{
    private String docId;
	private String personId;
	private String fullName;
	private String transactionCategory;
	private String contributionSubType;
	private BigDecimal amount;
    private String transactionType;
    private String transactionDate;
    private DateTime sortDate;
    private String description;

	public String getPersonId()
	{
		return personId;
	}

	public void setPersonId(String personId)
	{
		this.personId = personId;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getTransactionCategory()
	{
		return transactionCategory;
	}

	public void setTransactionCategory(String transactionCategory)
	{
		this.transactionCategory = transactionCategory;
	}

	public String getContributionSubType()
	{
		return contributionSubType;
	}

	public void setContributionSubType(String contributionSubType)
	{
		this.contributionSubType = contributionSubType;
	}

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getSortDate() {
        return sortDate;
    }

    public void setSortDate(DateTime sortDate) {
        this.sortDate = sortDate;
    }
}
