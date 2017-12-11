package com.bt.nextgen.messages;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.NotificationImpl;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountResponseImpl;
import com.bt.nextgen.service.avaloq.NotificationUpdateRequestImpl;
import com.bt.nextgen.service.avaloq.code.CacheManagedStaticCodeIntegrationServiceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.btfin.panorama.core.security.integration.messages.NotificationAddRequest;
import com.bt.nextgen.service.integration.messages.NotificationAddRequestImpl;
import com.btfin.panorama.core.security.integration.messages.NotificationEventType;
import com.btfin.panorama.core.security.integration.messages.NotificationIdentifier;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author L070589 
 * 
 * Integration Test class for Notification Service
 */
public class NotificationServiceIntegrationImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	NotificationIntegrationService notificationService;
	@Autowired
	ParsingContext context;
    @Autowired

    private CacheManagedStaticCodeIntegrationServiceImpl staticCodes;
    private NotificationUnreadCountResponse responseUnreadCountResponse;
    private NotificationUpdateRequest requestUpdate;
    private ServiceErrors serviceErrors = new ServiceErrorsImpl();
    private NotificationAddRequestImpl notificationAddRequest;
    private NotificationAddRequestImpl notificationAddRequestTwo;

	@Before
	public void setUp() throws Exception
	{
        notificationAddRequest = new NotificationAddRequestImpl();
        notificationAddRequest.setMessageContext("TestMessasgeContext");
        notificationAddRequest.setNotificationEventType(NotificationEventType.MESSAGE_CENTER);
        NotificationResolutionBaseKey notificationResolutionBaseKey = new NotificationResolutionBaseKey(AccountKey.valueOf("1914"));
        notificationAddRequest.setNotificationResolutionBaseKey(notificationResolutionBaseKey);
        notificationAddRequest.setTriggeringObjectKey(PersonKey.valueOf("34760"));

        notificationAddRequestTwo = new NotificationAddRequestImpl();
        notificationAddRequestTwo.setMessageContext("TestMessasgeContextTwo");
        notificationAddRequestTwo.setNotificationEventType(NotificationEventType.MESSAGE_CENTER);
        NotificationResolutionBaseKey notificationResolutionBaseKeyTwo = new NotificationResolutionBaseKey(AccountKey.valueOf("55530"));
        notificationAddRequestTwo.setNotificationResolutionBaseKey(notificationResolutionBaseKeyTwo);
        notificationAddRequestTwo.setTriggeringObjectKey(PersonKey.valueOf("55530"));
	}

	/**
	 * Test Method for Count Priority and Non Priority Notification Functionality
	 */
	@Test
	@SecureTestContext(username="adviser", jobRole = "adviser" , customerId = "201601934", profileId="971", jobId = "")
	public void testNotificationServiceGetDetailedUnreadCount()
	{
        responseUnreadCountResponse = notificationService.getDetailedUnReadNotification(serviceErrors);
		testTotalUnreadClientNotifications(responseUnreadCountResponse, 11);
		testTotalPriorityClientNotifications(responseUnreadCountResponse, 5);
		testTotalUnreadMyNotifications(responseUnreadCountResponse, 7);
		testTotalPriorityMyNotifications(responseUnreadCountResponse, 0);
		testTotalNotifications(responseUnreadCountResponse, 18);
		testTotalPriorityNotifications(responseUnreadCountResponse, 5);
	}

    /**
     * Test Method for Total Unread Messages
     */
    @Test
    @SecureTestContext(username="adviser", jobRole = "adviser" , customerId = "201601934", profileId="971", jobId = "")
    public void testNotificationServiceGetUnreadCount()
    {
        NotificationUnreadCount responseUnreadCountResponse = notificationService.getUnReadNotification(serviceErrors);
        assertThat(responseUnreadCountResponse, is(notNullValue()));
        assertThat(responseUnreadCountResponse.getTotalUnreadClientNotifications(), is(11));
        assertThat(responseUnreadCountResponse.getTotalUnreadMyNotifications(), is(7));
        assertThat(responseUnreadCountResponse.getTotalNotifications(), is(18));

    }

    /**
     * Test Method for Count Error Handling
     */
    @Test
    @SecureTestContext(username="explode", jobRole = "adviser" , customerId = "201601934", profileId="971", jobId = "")
    public void testNotificationServiceGetUnreadCount_ErrorHandling()
    {
        NotificationUnreadCountResponseImpl responseUnreadCountResponse_errorResponse;
        responseUnreadCountResponse_errorResponse = (NotificationUnreadCountResponseImpl) notificationService.getDetailedUnReadNotification(serviceErrors);
        assertThat(responseUnreadCountResponse_errorResponse, is(notNullValue()));
        assertThat(responseUnreadCountResponse_errorResponse.getErrorList(), is(notNullValue()));
        assertThat(responseUnreadCountResponse_errorResponse.getErrorList().get(0).getType().toString(), is("fa"));
        assertThat(responseUnreadCountResponse_errorResponse.getErrorList().get(0).getReason().toString(), is(""));
        assertThat(responseUnreadCountResponse_errorResponse.getErrorList().get(1).getType().toString(), is("fa"));
        assertThat(responseUnreadCountResponse_errorResponse.getErrorList().get(1).getReason().toString(),is("Fatal error occurred: "));
    }

	/**
	 * Test Method for Update Notification Functionality
	 */
	@Test
	public void testUpdateNotification() {
        NotificationUpdateRequestImpl request = new NotificationUpdateRequestImpl();
        NotificationIdentifier obj = new NotificationImpl();
        obj.setNotificationId("123123");
        request.setNotificationIdentifier(obj);
        request.setStatus(NotificationStatus.READ);
        String response = notificationService.updateNotification(request, serviceErrors);
        assertThat(response.toUpperCase(), is("SUCCESS"));
    }

    @Test
	public void testLoadAndUpdateNotification()
	{
        List profileIdList = new ArrayList();
        profileIdList.add("7164");
        profileIdList.add("7262");
        DateTime startDate = new DateTime(2015, 1, 20, 0, 0,0);
        DateTime endDate = new DateTime(2015, 1, 22, 23, 59,59);
		List list = notificationService.loadNotifications(profileIdList,startDate,endDate,serviceErrors);
		testUpdateForReadNotifications(list, "Success");
	}

	protected NotificationUnreadCountResponse testTotalUnreadClientNotifications(
		NotificationUnreadCountResponse responseUnreadCountResponse, int totalUnreadClientNotifications)
	{
		assertThat(responseUnreadCountResponse, is(notNullValue()));
		assertThat(responseUnreadCountResponse.getTotalUnreadClientNotifications(), is(totalUnreadClientNotifications));
		return responseUnreadCountResponse;
	}

	protected NotificationUnreadCountResponse testTotalPriorityClientNotifications(
		NotificationUnreadCountResponse responseUnreadCountResponse, int totalPriorityClientNotifications)
	{
		assertThat(responseUnreadCountResponse, is(notNullValue()));
		assertThat(responseUnreadCountResponse.getTotalPriorityClientNotifications(), is(totalPriorityClientNotifications));
		return responseUnreadCountResponse;
	}

	protected NotificationUnreadCountResponse testTotalUnreadMyNotifications(
		NotificationUnreadCountResponse responseUnreadCountResponse, int totalUnreadMyNotifications)
	{
		assertThat(responseUnreadCountResponse, is(notNullValue()));
		assertThat(responseUnreadCountResponse.getTotalUnreadMyNotifications(), is(totalUnreadMyNotifications));
		return responseUnreadCountResponse;
	}

	protected NotificationUnreadCountResponse testTotalPriorityMyNotifications(
		NotificationUnreadCountResponse responseUnreadCountResponse, int totalPriorityMyNotifications)
	{
		assertThat(responseUnreadCountResponse, is(notNullValue()));
		assertThat(responseUnreadCountResponse.getTotalPriorityMyNotifications(), is(totalPriorityMyNotifications));
		return responseUnreadCountResponse;
	}

	protected NotificationUnreadCountResponse testTotalNotifications(NotificationUnreadCountResponse responseUnreadCountResponse,
		int totalNotifications)
	{
		assertThat(responseUnreadCountResponse, is(notNullValue()));
		assertThat(responseUnreadCountResponse.getTotalNotifications(), is(totalNotifications));
		return responseUnreadCountResponse;
	}

	protected NotificationUnreadCountResponse testTotalPriorityNotifications(
		NotificationUnreadCountResponse responseUnreadCountResponse, int totalPriorityNotifications)
	{
		assertThat(responseUnreadCountResponse, is(notNullValue()));
		assertThat(responseUnreadCountResponse.getTotalPriorityNotifications(), is(totalPriorityNotifications));
		return responseUnreadCountResponse;
	}

	protected String testUpdateForReadNotifications(List <NotificationIdentifier> list, String updateStatus) {

        assertThat(list, is(notNullValue()));
        List<NotificationUpdateRequest> notificationUpdateRequestList = new ArrayList<>();
        for (NotificationIdentifier notificationIdentifier : list) {
            NotificationUpdateRequestImpl request = new NotificationUpdateRequestImpl();
            request.setNotificationIdentifier(notificationIdentifier);
            request.setStatus(NotificationStatus.READ);
            notificationUpdateRequestList.add(request);
        }
        String status = notificationService.updateNotifications(notificationUpdateRequestList, serviceErrors);
        assertThat(status, is(updateStatus));
        return status;
    }

    @Test
    @SecureTestContext(customerId = "avaloq")
    public void testAddNotifications() {
        String response = notificationService.addNotification(notificationAddRequest, serviceErrors);
        assertThat(response, notNullValue());
        assertThat(response, is("Success"));
    }

    @Test
    @SecureTestContext(customerId = "avaloq")
    public void testAddListOfNotifications() {
        String response = notificationService.addNotifications(Arrays.<NotificationAddRequest>asList(notificationAddRequest,
                notificationAddRequestTwo), serviceErrors);
        assertThat(response, notNullValue());
        assertThat(response, is("Success"));
    }
}