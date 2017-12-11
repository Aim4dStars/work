package com.bt.nextgen.api.contributioncaps.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;

import java.math.BigDecimal;
import java.util.List;

/**
 * Subtotals for member contributions classifications: concessional, non-concessional, others
 */
public class MemberContributionCapValuationDto extends BaseDto
{
	private String personId;

	private String firstName;

	private String lastName;

	private List<ContributionSubtypeValuationDto> contributionSubtypeValuationDto;

	private MemberContributionsCapDto memberContributionsCapDto;

	private int numberOfTransactions;


	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}



	// Retrieve total value of concessional contributions
	public BigDecimal getConcessionalTotal()
	{
		BigDecimal total = BigDecimal.ZERO;

		for (ContributionSubtypeValuationDto subTypeValuation : contributionSubtypeValuationDto)
		{
			if (subTypeValuation.getContributionClassification().equalsIgnoreCase(ContributionClassification.CONCESSIONAL.getAvaloqInternalId()))
			{
				total = total.add(subTypeValuation.getAmount());
			}
		}

		return total;
	}

	public BigDecimal getConcessionalAvailableBalance()
	{
		BigDecimal total = BigDecimal.ZERO;

		if (memberContributionsCapDto != null && memberContributionsCapDto.getConcessionalCap() != null)
		{
			total = memberContributionsCapDto.getConcessionalCap().subtract(getConcessionalTotal());
		}

		return total;
	}

	// Retrieve total value of non-concessional contributions
	public BigDecimal getNonConcessionalTotal()
	{
		BigDecimal total = BigDecimal.ZERO;

		for (ContributionSubtypeValuationDto subTypeValuation : contributionSubtypeValuationDto)
		{
			if (subTypeValuation.getContributionClassification().equalsIgnoreCase(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId()))
			{
				total = total.add(subTypeValuation.getAmount());
			}
		}

		return total;
	}

	// Available non-concessional balance
	public BigDecimal getNonConcessionalAvailableBalance()
	{
		BigDecimal total = BigDecimal.ZERO;

		if (memberContributionsCapDto != null && memberContributionsCapDto.getNonConcessionalCap() != null)
		{
			total = memberContributionsCapDto.getNonConcessionalCap().subtract(getNonConcessionalTotal());
		}

		return total;
	}


	// Retrieve total value of "other" contributions
	public BigDecimal getOtherContributionsTotal()
	{
		BigDecimal total = BigDecimal.ZERO;

		for (ContributionSubtypeValuationDto subTypeValuation : contributionSubtypeValuationDto)
		{
			if (subTypeValuation.getContributionClassification().equalsIgnoreCase(ContributionClassification.OTHER.getAvaloqInternalId()))
			{
				total = total.add(subTypeValuation.getAmount());
			}
		}

		return total;
	}

	// Retrieve total value of all contributions
	public BigDecimal getTotalContributions()
	{
		return getConcessionalTotal().add(getNonConcessionalTotal()).add(getOtherContributionsTotal());
	}


	public List<ContributionSubtypeValuationDto> getContributionSubtypeValuationDto() {
		return contributionSubtypeValuationDto;
	}

	public void setContributionSubtypeValuationDto(List<ContributionSubtypeValuationDto> contributionSubtypeValuationDto) {
		this.contributionSubtypeValuationDto = contributionSubtypeValuationDto;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public MemberContributionsCapDto getMemberContributionsCapDto() {
		return memberContributionsCapDto;
	}

	public void setMemberContributionsCapDto(MemberContributionsCapDto memberContributionsCapDto) {
		this.memberContributionsCapDto = memberContributionsCapDto;
	}

	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}

	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}

}
