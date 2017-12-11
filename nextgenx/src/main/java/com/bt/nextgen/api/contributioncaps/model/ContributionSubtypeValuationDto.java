package com.bt.nextgen.api.contributioncaps.model;

import java.math.BigDecimal;

/**
 * Aggregate contribution amounts for a specific super contribution sub-type.
 * <p>
 * e.g. 52 weekly employee contributions totalling $10000
 */
public class ContributionSubtypeValuationDto
{
	private String contributionSubtype;

	private String contributionClassification;

	private BigDecimal amount;

	private int numberOfTransactions;


	public String getContributionSubtype() {
		return contributionSubtype;
	}

	public void setContributionSubtype(String contributionSubtype) {
		this.contributionSubtype = contributionSubtype;
	}

	public String getContributionClassification() {
		return contributionClassification;
	}

	public void setContributionClassification(String contributionClassification) {
		this.contributionClassification = contributionClassification;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}


}