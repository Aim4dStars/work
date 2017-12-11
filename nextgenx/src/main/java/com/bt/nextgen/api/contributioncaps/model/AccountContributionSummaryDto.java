package com.bt.nextgen.api.contributioncaps.model;


import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class AccountContributionSummaryDto implements KeyedDto<AccountKey>
{
	private AccountKey accountKey;

	private List<MemberContributionCapValuationDto> memberContributionSummary;


	public List<MemberContributionCapValuationDto> getMemberContributionSummary() {
		return memberContributionSummary;
	}

	public void setMemberContributionSummary(List<MemberContributionCapValuationDto> memberContributionSummary) {
		this.memberContributionSummary = memberContributionSummary;
	}

	public AccountKey getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(AccountKey accountKey) {
		this.accountKey = accountKey;
	}

	@Override
	public AccountKey getKey()
	{
		return accountKey;
	}

	public String getType()
	{
		return "AccountContributionSummaryDto";
	}
}
