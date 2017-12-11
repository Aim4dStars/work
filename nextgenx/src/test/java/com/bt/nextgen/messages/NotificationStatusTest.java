package com.bt.nextgen.messages;

import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author L070589
 * Test class to validate Methods defined in NotificationStatus Enum.
 */
public class NotificationStatusTest
{

	/**
	 * Test method to validate getStatusValue() returning appropriate Enum value.
	 */
	@Test
	public void testNotificationStatusValues()
	{

		NotificationStatus statusValue = NotificationStatus.DELETED;
		assertThat(statusValue.getStatusValue(), is("deleted"));
		statusValue = NotificationStatus.READ;
		assertThat(statusValue.getStatusValue(), is("read"));
		statusValue = NotificationStatus.UNREAD;
		assertThat(statusValue.getStatusValue(), is("open"));
	}

	/**
	 * Test method to validate getStatus() returning appropriate Enum constant.
	 */
	@Test
	public void testNotificationStatus()
	{
		NotificationStatus status = NotificationStatus.getStatus("open");

		assertThat(status, is(NotificationStatus.UNREAD));
		status = NotificationStatus.getStatus("read");
		assertThat(status, is(NotificationStatus.READ));
		status = NotificationStatus.getStatus("deleted");
		assertThat(status, is(NotificationStatus.DELETED));
		status = NotificationStatus.getStatus("anythingelse");
		assertThat(status, is(NotificationStatus.UNKNOWN));
	}

}
