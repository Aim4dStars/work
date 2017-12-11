package com.bt.nextgen.service.avaloq.logout;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by M044020 on 30/03/2017.
 */
public class LogoutUserIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    LogoutUserIntegrationService logoutUserIntegrationService;

    @SecureTestContext(username = "logout", customerId = "201635682", jobRole = "adviser", profileId = "5393", jobId = "102540")
    @Test
    public void testNotifyUserLogout() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserLogoutDetail userLogoutDetail = logoutUserIntegrationService.notifyUserLogout(serviceErrors);
        assertThat(userLogoutDetail, is(notNullValue()));
        assertTrue(userLogoutDetail.getSecUser().equals("5393"));

    }

    @Test
    public void testNotifyUserLogoutWithoutUser() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserLogoutDetail userLogoutDetail = logoutUserIntegrationService.notifyUserLogout(serviceErrors);
        assertThat(userLogoutDetail, is(nullValue()));
    }

}