package com.bt.nextgen.api.cashcategorisation.model;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class CategorisableCashTransactionDto extends BaseDto implements KeyedDto <AccountKey>
{

	@NotNull
	private AccountKey key;

	@NotNull
	private String depositId;

	@NotNull
	@Pattern(regexp="\\Amember$|\\Afund$|\\Amembersubcat$")
	private String categorisationLevel;

	@NotNull
	private List <CategorisedTransactionDto> memberContributionDtoList;

	private String action;

	private String status;

	private String transactionCategory;

	private BigDecimal amount;

	@Override
	public AccountKey getKey()
	{
		return key;
	}

	public void setKey(AccountKey key)
	{
		this.key = key;
	}

	public String getDepositId()
	{
		return depositId;
	}

	public void setDepositId(String depositId)
	{
		this.depositId = depositId;
	}

	public List <CategorisedTransactionDto> getMemberContributionDtoList()
	{
		return memberContributionDtoList;
	}

	public void setMemberContributionDtoList(List <CategorisedTransactionDto> memberContributionDtoList)
	{
		this.memberContributionDtoList = memberContributionDtoList;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getTransactionCategory() {
		return transactionCategory;
	}

	public void setTransactionCategory(String transactionCategory) {
		this.transactionCategory = transactionCategory;
	}

	public String getCategorisationLevel() {
		return categorisationLevel;
	}

	public void setCategorisationLevel(String categorisationLevel) {
		this.categorisationLevel = categorisationLevel;
	}

	@JsonIgnore
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}