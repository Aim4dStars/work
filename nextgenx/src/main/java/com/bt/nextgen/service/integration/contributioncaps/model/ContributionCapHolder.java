package com.bt.nextgen.service.integration.contributioncaps.model;

import java.util.List;

public interface ContributionCapHolder
{
	String getAccountId();

	void setAccountId(String accountId);

	List <MemberContributionsCap> getMemberContributionCapList();

	void setMemberContributionCapList(List <MemberContributionsCap> capList);
}
