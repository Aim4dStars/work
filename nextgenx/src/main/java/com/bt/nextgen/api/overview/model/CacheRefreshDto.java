package com.bt.nextgen.api.overview.model;


import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

public class CacheRefreshDto extends BaseDto implements KeyedDto<AccountKey>
{
	private DateTime cacheLastRefreshedDatetime;
	private String status;
	private AccountKey key;


	public DateTime getCacheLastRefreshedDatetime()
	{
		return cacheLastRefreshedDatetime;
	}

	public void setCacheLastRefreshedDatetime(DateTime cacheLastRefreshedDatetime)
	{
		this.cacheLastRefreshedDatetime = cacheLastRefreshedDatetime;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	@Override
	public AccountKey getKey()
	{
		return key;
	}
}