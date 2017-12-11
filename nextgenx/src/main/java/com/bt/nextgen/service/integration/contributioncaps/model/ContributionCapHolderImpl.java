package com.bt.nextgen.service.integration.contributioncaps.model;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

@ServiceBean(xpath = "/")
public class ContributionCapHolderImpl implements ContributionCapHolder
{

	private static final String XML_HEADER = "//data/top/top_head_list/top_head";
	private String accountId;

	@ServiceElementList(xpath = XML_HEADER, type = MemberContributionsCapImpl.class)
	private List <MemberContributionsCap> capList = new ArrayList <>();

	@Override
	public String getAccountId()
	{
		// TODO Auto-generated method stub
		return accountId;
	}

	@Override
	public void setAccountId(String accountId)
	{
		this.accountId = accountId;

	}

	@Override
	public List <MemberContributionsCap> getMemberContributionCapList()
	{
		// TODO Auto-generated method stub
		return capList;
	}

	@Override
	public void setMemberContributionCapList(List <MemberContributionsCap> capList)
	{
		this.capList = capList;

	}

}
