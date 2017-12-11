package com.bt.nextgen.logout.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.logout.LogoutUserIntegrationService;
import com.bt.nextgen.service.avaloq.logout.UserLogoutDetail;
import com.bt.nextgen.service.avaloq.logout.UserLogoutDetailImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 30/03/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogoutServiceImplTest {
    @InjectMocks
    private LogoutService logoutService = new LogoutServiceImpl();

    @Mock
    private LogoutUserIntegrationService logoutUserIntegrationService;

    @Test
    public void notifyLogoutUserWithUserDetailsNull() throws Exception {
        when(logoutUserIntegrationService.notifyUserLogout(any(ServiceErrors.class))).thenReturn(null);
        logoutService.notifyLogoutUser(new ServiceErrorsImpl());
    }

    @Test
    public void notifyLogoutUserWithUserDetails() throws Exception {
        UserLogoutDetail userLogoutDetail =  new UserLogoutDetailImpl();
        userLogoutDetail.setSecUser("ABC Test");
        userLogoutDetail.setLoginTime(new DateTime(2017, 01, 01, 12, 22, 10));
        userLogoutDetail.setLastActionTime(new DateTime(2017, 01, 01, 12, 24, 36));
        when(logoutUserIntegrationService.notifyUserLogout(any(ServiceErrors.class))).thenReturn(userLogoutDetail);
        logoutService.notifyLogoutUser(new ServiceErrorsImpl());
    }

    @Test
    public void notifyLogoutUserWithException() throws Exception {
        when(logoutUserIntegrationService.notifyUserLogout(any(ServiceErrors.class))).thenThrow(new NullPointerException("Test NullPointerException."));
        logoutService.notifyLogoutUser(new ServiceErrorsImpl());
    }

}