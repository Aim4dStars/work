package com.bt.nextgen.core.web.model;

/**
 * 	This immutable model contains the global alert and notification for adviser/investor unread and high priority message
 */
public class GlobalAlertNotificationModel
{
	private String unread;
	private String high;

	public GlobalAlertNotificationModel(String unread, String high)
	{
		this.unread = unread;
		this.high = high;
	}

	public String getUnread()
	{
		return unread;
	}

	public String getHigh()
	{
		return high;
	}

	@Override
	public String toString()
	{
		return "[ unread: "+unread+", high: "+high+"]";
	}

	public GlobalAlertNotificationModel add(GlobalAlertNotificationModel fromMe)
	{
		return new GlobalAlertNotificationModel(unread + fromMe.unread, high + fromMe.high);
	}
}
