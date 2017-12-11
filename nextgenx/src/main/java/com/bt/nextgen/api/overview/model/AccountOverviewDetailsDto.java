package com.bt.nextgen.api.overview.model;


import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

public class AccountOverviewDetailsDto extends BaseDto implements KeyedDto <AccountKey>
{
	private DateTime cacheLastRefreshedDatetime;

	private AccountKey accountKey;


	public DateTime getCacheLastRefreshedDatetime()
	{
		return cacheLastRefreshedDatetime;
	}

	public void setCacheLastRefreshedDatetime(DateTime cacheLastRefreshedDatetime)
	{
		this.cacheLastRefreshedDatetime = cacheLastRefreshedDatetime;
	}

	@Override
	public AccountKey getKey()
	{
		return accountKey;
	}

	@Override
	public String getType()
	{
		return "AccountOverviewDetailsDto";
	}
}