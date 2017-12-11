package com.bt.nextgen.service.integration.messages;

import com.btfin.panorama.core.security.integration.messages.NotificationIdentifier;
import com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.integration.messages.NotificationSubCategory;
import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.account.AccountIdentifier;
import com.btfin.panorama.core.security.integration.order.OrderIdentifier;
import com.bt.nextgen.service.integration.userinformation.ClientIdentifier;

/**
 *
 * @author L070354
 * 
 * Interface defined for the Notification messages
 * 
 * */
//TODO create extension object with extra url/title/personalised message fields - update implementation (don't populate the Notification Message for ASX/Market share)
public interface Notification extends NotificationIdentifier
{
	public DateTime getNotificationTimeStamp();

	public DateTime getNotificationValidUntil();

	public int getResponsibleUserId();

	public Integer getRecipientId();

	public String getEventName();

	public int getEventPriority();

	public NotificationStatus getNotificationStatus();

	public int getTriggeringObject();

	public OrderIdentifier getOrder();

	public String getNotificationMessage();

	public com.btfin.panorama.core.security.integration.messages.NotificationCategory getNotificationCategoryId();

	public NotificationSubCategory getNotificationSubCategoryId();

	public boolean isAdviserDashBoadFlag();

	public NotificationOwnerAccountType getOwnerAccountType();

	public int getMoneyAccountNumber();

	public void setBpId(String bpId);

	public boolean isMyMessage();

	public ClientIdentifier getPerson();

	public AccountIdentifier getAccount();

	public String getType();

	public String getUrl();

	public String getUrlText();

	public String getPersonalizedMessage();
}
