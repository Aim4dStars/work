package com.bt.nextgen.api.notification.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;

public class NotificationListDto extends BaseDto
{
	private int unreadCount;
	private int priorityCount;
	private List <NotificationDto> notificationList;

	public int getUnreadCount()
	{
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount)
	{
		this.unreadCount = unreadCount;
	}

	public int getPriorityCount()
	{
		return priorityCount;
	}

	public void setPriorityCount(int priorityCount)
	{
		this.priorityCount = priorityCount;
	}

	public List <NotificationDto> getNotificationList()
	{
		return notificationList;
	}

	public void setNotificationList(List <NotificationDto> notificationList)
	{
		this.notificationList = notificationList;
	}

}
