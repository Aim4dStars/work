package com.bt.panorama.direct.api.email.controller;


import com.bt.nextgen.core.web.model.AjaxResponse;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.api.email.service.SendPortfolioDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Created by L069552 on 17/06/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SendPortfolioDetailApiControllerTest {

    @InjectMocks
    SendPortfolioDetailApiController sendPortfolioDetailApiController;

    @Mock
    SendPortfolioDetailsService sendPortfolioDetailsService;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    @Mock
    private AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        when(sendPortfolioDetailsService.sendPortfolioDetails(any(PortfolioDetailDto.class), any(ServiceErrorsImpl.class))).thenReturn(true);
    }

    @Test
    public void test_emailPortfolioDetails() throws Exception {

        request.setRequestURI("/public/direct/sendEmail");
        request.setMethod("POST");
        request.setParameter("url", "URL");
        request.setParameter("email", "abc@test.com");
        request.setParameter("portfolioType", "Moderate");
        request.setParameter("name", "Abc");
//        AjaxResponse ajaxResponse = sendPortfolioDetailApiController.emailPortfolioDetails();
//        assertNotNull(ajaxResponse);
//        assertTrue(ajaxResponse.isSuccess());
        annotationMethodHandlerAdapter.handle(request, response, sendPortfolioDetailApiController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }
}
