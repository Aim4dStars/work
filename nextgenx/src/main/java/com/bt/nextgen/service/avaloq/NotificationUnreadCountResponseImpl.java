/**
 *
 */
package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;

/**
 * @author L070589
 *
 * Implementation class for Notification Unread Count Response
 */
@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class NotificationUnreadCountResponseImpl extends AvaloqBaseResponseImpl implements NotificationUnreadCountResponse
{

	public static final String XPATH_CLIENT_PRIORITY_UNREAD_NOTIFICATION = "sum(//tot_unread[ancestor::msgctr_head/msg_avsr_ctr_id/val='1' and ancestor::prio/*//prio/val[text()='High priority']]/val)";
	public static final String XPATH_CLIENT_NONPRIORITY_UNREAD_NOTIFICATION = "sum(//tot_unread[ancestor::msgctr_head/msg_avsr_ctr_id/val='1' and ancestor::prio/*//prio/val[text()='Non priority']]/val)";
	public static final String XPATH_MY_PRIORITY_UNREAD_NOTIFICATION = "sum(//tot_unread[ancestor::msgctr_head/msg_avsr_ctr_id/val='2' and ancestor::prio/*//prio/val[text()='High priority']]/val)";
	public static final String XPATH_MY_NONPRIORITY_UNREAD_NOTIFICATION = "sum(//tot_unread[ancestor::msgctr_head/msg_avsr_ctr_id/val='2' and ancestor::prio/*//prio/val[text()='Non priority']]/val)";

	@ServiceElement(xpath = XPATH_CLIENT_PRIORITY_UNREAD_NOTIFICATION)
	private int clientHightPriorityUnreadMessages;

	@ServiceElement(xpath = XPATH_MY_PRIORITY_UNREAD_NOTIFICATION)
	private int myHightPriorityUnReadMessages;

	@ServiceElement(xpath = XPATH_CLIENT_NONPRIORITY_UNREAD_NOTIFICATION)
	private int clientNonPriorityUnreadMessages;

	@ServiceElement(xpath = XPATH_MY_NONPRIORITY_UNREAD_NOTIFICATION)
	private int myNonPriorityUnreadMessages;

	@Override
	public int getTotalUnreadClientNotifications()
	{
		int totalClientUnReadMessages = clientHightPriorityUnreadMessages + clientNonPriorityUnreadMessages;
		return totalClientUnReadMessages;
	}

	@Override
	public int getTotalUnreadMyNotifications()
	{
		int totalMyUnReadMessages = myHightPriorityUnReadMessages + myNonPriorityUnreadMessages;
		return totalMyUnReadMessages;
	}

	@Override
	public int getTotalPriorityClientNotifications()
	{
		return clientHightPriorityUnreadMessages;
	}

	@Override
	public int getTotalPriorityMyNotifications()
	{
		return myHightPriorityUnReadMessages;
	}

	@Override
	public int getTotalPriorityNotifications()
	{
		return (getTotalPriorityClientNotifications() + getTotalPriorityMyNotifications());
	}

	@Override
	public int getTotalNotifications()
	{
		return (getTotalUnreadClientNotifications() + getTotalUnreadMyNotifications());
	}

}
