package com.bt.nextgen.api.registration.controller;

import com.bt.nextgen.api.registration.model.UserRoleDto;
import com.bt.nextgen.api.registration.service.UserRoleDtoService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Enumeration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link UserRoleApiController}
 *
 * Created by M044020 on 27/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRoleApiControllerTest {

    @InjectMocks
    private UserRoleApiController roleApiController;

    @Mock
    private UserRoleDtoService userRoleDtoService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public void setup() {
        UserRoleDto userDto = new UserRoleDto();
        userDto.setGcmId("12038909");
        userDto.setTransactionStatus(true);
        when(userRoleDtoService.submit(any(UserRoleDto.class), any(ServiceErrors.class))).thenReturn(userDto);
        when(request.getHeaderNames()).thenReturn(new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                return null;
            }
        });
    }

    @Test
    public void getStatusWithBadRequest() throws Exception {
        Cookie cookie = new Cookie("process_timer_LOGON", URLEncoder.encode("invalid\n", "UTF-8"));
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        roleApiController.getAccountApplicationStatus("C98E1473EB7B9FD6C7F8884AC0607EE46EE65D159F9268FC", request, response);
    }

    @Test
    public void getAccountApplicationStatus() throws Exception {
        roleApiController.getAccountApplicationStatus("C98E1473EB7B9FD6C7F8884AC0607EE46EE65D159F9268FC", request, response);
    }
}