package com.bt.nextgen.api.notification.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class NotificationUpdateDto extends BaseDto implements KeyedDto <String>
{
	private String key;
	private String status;

	public NotificationUpdateDto(String key, String status)
	{
		this.key = key;
		this.status = status;
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
	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
}
