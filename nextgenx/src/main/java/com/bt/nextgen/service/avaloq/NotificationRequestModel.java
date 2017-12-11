package com.bt.nextgen.service.avaloq;

public class NotificationRequestModel implements NotificationRequest
{
	
	String notificationId;
	String notificationStatus;
	
	public String getNotificationId()
	{
		return notificationId;
	}
	public void setNotificationId(String notificationId)
	{
		this.notificationId = notificationId;
	}
	public String getNotificationStatus()
	{
		return notificationStatus;
	}
	public void setNotificationStatus(String notificationStatus)
	{
		this.notificationStatus = notificationStatus;
	}

}
