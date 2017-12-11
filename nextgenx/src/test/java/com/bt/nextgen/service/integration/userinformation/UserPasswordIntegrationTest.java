package com.bt.nextgen.service.integration.userinformation;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserPasswordDetail;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserPasswordIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	UserInformationIntegrationService userService;
	@Autowired
	UserProfileService userProfileService;

	UserPasswordDetail userPasswordDetail;

	@Test
	@SecureTestContext(username="adviser", customerId = "201616433")
	public void testNotifyPasswordChangeInfo() throws Exception
	{
		testNotifyPasswordChangeInfo("201616433", "2014-09-30T18:24:02");
	}

	public void testNotifyPasswordChangeInfo(String userId, String expectedDate)
	{

		userPasswordDetail = userService.notifyPasswordChange(userProfileService.getActiveProfile(), new ServiceErrorsImpl());
		DateTimeFormatter dtfOut = DateTimeFormat.forPattern(Constants.AVALOQ_RESPONSE_DATE_FORMAT);
		assertNotNull(userPasswordDetail);
		assertEquals(expectedDate, dtfOut.print(userPasswordDetail.getLastPasswordChanged()));
	}

}
