package com.bt.nextgen.service.integration.cashcategorisation.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class CategorisableCashTransactionDto extends BaseDto implements KeyedDto <DepositKey>
{
	private String accountId;
	private List <MemberContributionDto> memberContributionDtoList;
    private String fromDate;
    private String toDate;

	@Override
	public DepositKey getKey()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public List <MemberContributionDto> getMemberContributionDtoList()
	{
		return memberContributionDtoList;
	}

	public void setMemberContributionDtoList(List <MemberContributionDto> memberContributionDtoList)
	{
		this.memberContributionDtoList = memberContributionDtoList;
	}

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
