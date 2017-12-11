package com.bt.nextgen.messages;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.NotificationImpl;
import com.bt.nextgen.service.integration.messages.*;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 
 * @author L070354
 * 
 * Test Class for the loading the Notification Messages
 *
 */
public class NotificationMessagesIntegrationTest extends BaseSecureIntegrationTest
{
	private static final Logger logger = LoggerFactory.getLogger(NotificationMessagesIntegrationTest.class);

	@Autowired
	NotificationIntegrationService notificationMessageService;


	/*
	 * Check whether the notifications loaded is not empty 
	 */
	@Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "serviceops", customerId = "avaloq", profileId ="" , jobRole="", jobId = "")
	public void testloadNotifications() throws Exception
	{
		logger.trace("Inside testMethod: testloadNotifications()");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List profileIdList = new ArrayList();
        profileIdList.add("7491");
        profileIdList.add("7492");

        DateTime startDate = new DateTime(2015, 1, 20, 0, 0,0);
        DateTime endDate = new DateTime(2015, 1, 22, 23, 59,59);

        List <com.btfin.panorama.core.security.integration.messages.Notification> notificationList= notificationMessageService.loadNotifications(profileIdList,startDate,endDate,serviceErrors);
		//assertNotNull(response.getNotification());
		assertThat(notificationList.size() > 0, Is.is(true));
	}

	
	@SecureTestContext(username = "explode", customerId = "201101101", jobRole="adviser",profileId="971",jobId="1234" )
    @Test
    public void testloadNotificationsForError() throws Exception
    {
        logger.trace("Inside testMethod: testloadNotificationsForError()");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List profileIdList = new ArrayList();
        profileIdList.add("7164");
        profileIdList.add("7262");
        DateTime startDate = new DateTime(2015, 1, 20, 0, 0,0);
        DateTime endDate = new DateTime(2015, 1, 22, 23, 59,59);

        List <com.btfin.panorama.core.security.integration.messages.Notification> notificationList= notificationMessageService.loadNotifications(profileIdList,startDate,endDate,serviceErrors);
        assertThat(serviceErrors.hasErrors(), Is.is(true));
    }

	/*
	 * Test Each Notifications individually
	 */
	@Test
	@SecureTestContext
	public void testSpecificNotifications() throws Exception
	{
		logger.trace("Inside testMethod: testSpecificNotifications()");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List profileIdList = new ArrayList();
        profileIdList.add("1234");
        DateTime startDate = new DateTime(2015, 1, 20, 0, 0,0);
        DateTime endDate = new DateTime(2015, 1, 22, 23, 59,59);
        List <com.btfin.panorama.core.security.integration.messages.Notification> notificationList= notificationMessageService.loadNotifications(profileIdList,startDate,endDate,serviceErrors);
		//assertNotNull(response);
		assertThat(notificationList.size() > 0, Is.is(true));
		String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ssZ";
		String datePattern = "yyyy-MM-dd";

		for (com.btfin.panorama.core.security.integration.messages.Notification notification : notificationList)
		{
			//logger.info("Notification messages are \n :{}", printNotifications(notification));
			switch (notification.getNotificationId())
			{

				case "6315":

					assertThat(notification.getNotificationTimeStamp(),
						is(DateTime.parse("2014-11-29T18:08:52+10:00", DateTimeFormat.forPattern(dateTimePattern))));
					assertThat(notification.getNotificationValidUntil(),
						is(DateTime.parse("2100-12-31", DateTimeFormat.forPattern(datePattern))));
					assertThat(notification.getResponsibleUserId(), is(5635));
					assertThat(notification.getRecipientId(), is(5635));
					assertThat(notification.getEventName(), is("Completion of a BPay"));
					assertThat(notification.getEventPriority(), is(2));
				    assertThat(notification.getNotificationStatus(), is(com.btfin.panorama.core.security.integration.messages.NotificationStatus.UNREAD));
					assertThat(notification.getTriggeringObject(), is(61792));
					assertThat(notification.getNotificationCategoryId(), is(com.btfin.panorama.core.security.integration.messages.NotificationCategory.TRANSFER_VETTING));
					assertThat(notification.isMyMessage(), is(false));
					assertThat(notification.getAccount().getAccountKey().getId(), is("36846"));
					assertThat(notification.getOwnerAccountType(), is(com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType.INDIVIDUAL));
					assertThat(notification.getMoneyAccountNumber(), is(61795));
					assertThat(notification.getPerson().getClientKey().getId(), is("61790"));
					assertThat(notification.getNotificationSubCategoryId(), is(com.btfin.panorama.core.security.integration.messages.NotificationSubCategory.PAYMENT_LIMIT_CHANGE));
					assertThat(notification.isAdviserDashBoadFlag(), is(true));
					break;

				case "6314":
					assertThat(notification.getNotificationTimeStamp(),
						is(DateTime.parse("2014-11-29T18:03:09+10:00", DateTimeFormat.forPattern(dateTimePattern))));
					assertThat(notification.getNotificationValidUntil(),
						is(DateTime.parse("2100-12-31", DateTimeFormat.forPattern(datePattern))));
					assertThat(notification.getResponsibleUserId(), is(5635));
					assertThat(notification.getRecipientId(), is(5635));
					assertThat(notification.getEventName(), is(nullValue()));
					assertThat(notification.getEventPriority(), is(1));
					assertThat(notification.getNotificationStatus(), is(com.btfin.panorama.core.security.integration.messages.NotificationStatus.UNREAD));
					assertThat(notification.getTriggeringObject(), is(61814));
					assertThat(notification.getNotificationMessage(),
						is("The Establishment fee requested of $900.00 has not been charged and will expiry on 15-Sep-2014."));
					assertThat(notification.getNotificationCategoryId(),
						is(com.btfin.panorama.core.security.integration.messages.NotificationCategory.FAILED_TRANSACTIONS_AND_WARNINGS));
					assertThat(notification.isMyMessage(), is(true));
					assertThat(notification.getPerson().getClientKey().getId(), is("61814"));
					assertThat(notification.getNotificationSubCategoryId(), is(com.btfin.panorama.core.security.integration.messages.NotificationSubCategory.DEPOSIT_CLEARED));
					assertThat(notification.isAdviserDashBoadFlag(), is(true));
					break;
			}

		}

	}

	/*
	 * Creating mock notifications
	 */

	public Map <String, com.btfin.panorama.core.security.integration.messages.Notification> createMockNotification() throws Exception
	{
		NotificationImpl n = new NotificationImpl();
		Map <String, com.btfin.panorama.core.security.integration.messages.Notification> ntfcnmap = new HashMap <String, com.btfin.panorama.core.security.integration.messages.Notification>();
		String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ssZ";
		String datePattern = "yyyy-MM-dd";
		DateTime date = new DateTime();

		n.setNotificationId("6315");
		n.setNotificationTimeStamp(date.parse("2014-11-29T18:08:52+10:00", DateTimeFormat.forPattern(dateTimePattern)));
		n.setNotificationValidUntil(date.parse("2100-12-31", DateTimeFormat.forPattern(datePattern)));
		n.setResponsibleUserId(5635);
		n.setRecipientId(5635);
		n.setEventName("Completion of a BPay");
		n.setEventPriority(2);
		n.setStatus(com.btfin.panorama.core.security.integration.messages.NotificationStatus.UNREAD);
		n.setTriggeringObject(61792);
		n.setNotificationMessage("Elizabeth Myer has received a BPAY deposit of $4,500.00.");
		n.setNotificationCategoryId(com.btfin.panorama.core.security.integration.messages.NotificationCategory.TRANSFER_VETTING);
		n.setMyMessage(false);
		n.setBpId("36846");
		n.setOwnerAccountType(com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType.INDIVIDUAL);
		n.setMoneyAccountNumber(61795);
		ntfcnmap.put(n.getNotificationId(), n);

		return ntfcnmap;
	}

	/*
	 * Test bunch of Notifications for integration
	 */

	@SecureTestContext
	@Ignore
	public void testEachNotification() throws Exception
	{
		logger.trace("Inside testMethod: testEachNotification()");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List profileIdList = new ArrayList();
        profileIdList.add("1234");
        DateTime startDate = new DateTime(2015, 1, 20, 0, 0,0);
        DateTime endDate = new DateTime(2015, 1, 22, 23, 59,59);
        List <com.btfin.panorama.core.security.integration.messages.Notification> notificationList= notificationMessageService.loadNotifications(profileIdList,startDate,endDate,serviceErrors);
		//assertNotNull(response);
		assertThat(notificationList.size() > 0, Is.is(true));
		String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ssZ";
		String datePattern = "yyyy-MM-dd";
		Map <String, com.btfin.panorama.core.security.integration.messages.Notification> ntfcnmap = createMockNotification();

		for (com.btfin.panorama.core.security.integration.messages.Notification notification : notificationList)
		{

			//logger.info("Notification messages are \n :{}", printNotifications(notification));
			String key = notification.getNotificationId();

			if (ntfcnmap.containsKey(key))
			{
				com.btfin.panorama.core.security.integration.messages.Notification ntfcn = (com.btfin.panorama.core.security.integration.messages.Notification)ntfcnmap.get(key);
				assertThat(notification.getNotificationTimeStamp(), is(ntfcn.getNotificationTimeStamp()));
				assertThat(notification.getNotificationValidUntil(), is(ntfcn.getNotificationValidUntil()));
				assertThat(notification.getResponsibleUserId(), is(ntfcn.getResponsibleUserId()));
				assertThat(notification.getRecipientId(), is(ntfcn.getRecipientId()));
				assertThat(notification.getEventName(), is(ntfcn.getEventName()));
				assertThat(notification.getEventPriority(), is(ntfcn.getEventPriority()));
				assertThat(notification.getNotificationStatus(), is(ntfcn.getNotificationStatus()));
				assertThat(notification.getTriggeringObject(), is(ntfcn.getTriggeringObject()));
				assertThat(notification.getOrder().getOrderId(), is(ntfcn.getOrder().getOrderId()));
				assertThat(notification.getNotificationMessage(), is(ntfcn.getNotificationMessage()));
				assertThat(notification.getNotificationCategoryId(), is(ntfcn.getNotificationCategoryId()));
				assertThat(notification.isMyMessage(), is(ntfcn.isMyMessage()));
				assertThat(notification.getAccount().getAccountKey().getId(), is(ntfcn.getAccount().getAccountKey().getId()));
				assertThat(notification.getOwnerAccountType(), is(ntfcn.getOwnerAccountType()));
				assertThat(notification.getMoneyAccountNumber(), is(ntfcn.getMoneyAccountNumber()));
				assertThat(notification.getAccount().getAccountKey().getId(), is(ntfcn.getAccount().getAccountKey().getId()));

			}
		}
	}

	/*
	 * Method to print the notifications
	 */
	public StringBuilder printNotifications(com.btfin.panorama.core.security.integration.messages.Notification n)
	{
		StringBuilder s = new StringBuilder();
		s.append("NTFCN_ID:" + n.getNotificationId() + "\n");
		s.append("NTFCN_TIMESTAMP:" + n.getNotificationTimeStamp().toString() + "\n");
		s.append("NTFCN_VALIDTO:" + n.getNotificationValidUntil() + "\n");
		s.append("NTFCN_RESP_USERID:" + n.getResponsibleUserId() + "\n");
		s.append("NTFCN_RECPID:" + n.getRecipientId() + "\n");
		s.append("NTFCN_EVENTTYPE" + ":" + n.getEventName() + "\n");
		s.append("NTFCN_PRIORITY:" + n.getEventPriority() + "\n");
		s.append("NTFCN_STATUS:" + n.getNotificationStatus() + "\n");
		s.append("NTFCN_TRIGOBJ:" + n.getTriggeringObject() + "\n");
		s.append("NTFCN_ORDERID:" + n.getOrder().getOrderId() + "\n");
		s.append("NTFCN_MSG:" + n.getNotificationMessage() + "\n");
		s.append("NTFCN_CAT" + ":" + n.getNotificationCategoryId() + "\n");
		s.append("NTFCN_MSGTYPE:" + n.isMyMessage() + "\n");
		s.append("NTFCN_BP_ID" + ":" + n.getAccount().getAccountKey().getId() + "\n");
		s.append("NTFCN_OWNER_TYPE_ID:" + n.getOwnerAccountType() + "\n");
		s.append("NTFCN_MACC_NR:" + n.getMoneyAccountNumber() + "\n");
		s.append("NTFCN_PERSON_ID:" + n.getPerson().getClientKey().getId() + "\n\n");
		s.append("PATH_NTFCN_SUB_CAT:" + n.getNotificationSubCategoryId() + "\n\n");
		s.append("PATH_NTFCN_IS_ADVR_DSHBD:" + n.isAdviserDashBoadFlag() + "\n");

		return s;
	}
}
