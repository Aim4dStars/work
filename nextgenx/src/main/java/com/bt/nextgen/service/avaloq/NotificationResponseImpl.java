package com.bt.nextgen.service.avaloq;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.btfin.panorama.core.security.integration.messages.Notification;
import com.btfin.panorama.core.security.integration.messages.NotificationResponse;

/**
 * 
 * @author L070354
 * 
 * xpath implementation of the notification response
 *
 */

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class NotificationResponseImpl extends AvaloqBaseResponseImpl implements NotificationResponse
{

	@ServiceElementList(xpath = "//data/ntfcn_list/ntfcn/ntfcn_head_list/ntfcn_head", type = NotificationImpl.class)
	private List <Notification> notificationlist;

	public List <Notification> getNotification()
	{
		// TODO Auto-generated method stub
		return notificationlist;
	}

	@Override
	public void setNotification(List <Notification> notificationlist)
	{
		this.notificationlist = notificationlist;
	}

}
