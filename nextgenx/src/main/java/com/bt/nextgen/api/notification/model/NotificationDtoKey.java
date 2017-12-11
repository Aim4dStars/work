package com.bt.nextgen.api.notification.model;

public class NotificationDtoKey
{
	private boolean clientNotification;

	public NotificationDtoKey(boolean clientNotification)
	{
		this.clientNotification = clientNotification;
	}

	public boolean isClientNotification()
	{
		return clientNotification;
	}
}
