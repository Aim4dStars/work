package com.bt.nextgen.api.notification.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class NotificationCountDto extends BaseDto implements KeyedDto <NotificationDtoKey>
{
	private NotificationDtoKey key;
	private int unreadCount;
	private int priorityCount;

	public NotificationCountDto(NotificationDtoKey key, int unreadCount, int priorityCount)
	{
		this.key = key;
		this.unreadCount = unreadCount;
		this.priorityCount = priorityCount;
	}

	public int getUnreadCount()
	{
		return unreadCount;
	}

	public int getPriorityCount()
	{
		return priorityCount;
	}

	@Override
	public NotificationDtoKey getKey()
	{
		return key;
	}

}
