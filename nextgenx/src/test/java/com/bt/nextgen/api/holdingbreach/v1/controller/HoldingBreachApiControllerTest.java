package com.bt.nextgen.api.holdingbreach.v1.controller;

import com.bt.nextgen.api.holdingbreach.v1.service.HoldingBreachDtoService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

@RunWith(MockitoJUnitRunner.class)
public class HoldingBreachApiControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(HoldingBreachApiControllerTest.class);

    @InjectMocks
    private HoldingBreachApiController breachController;

    @Mock
    private HoldingBreachDtoService breachDtoService;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandler.setMessageConverters(messageConverters);

    }

    @Test
    public final void testsearchAssets() {

        Mockito.when(breachDtoService.findOne(Mockito.any(ServiceErrors.class))).thenReturn(null);

        mockHttpServletRequest.setRequestURI("/secure/api/holding-breach/v1_0/breaches");
        mockHttpServletRequest.setMethod("GET");
        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, breachController);
        } catch (Exception e) {
            logger.error("failed annotation method handler", e);
        }
    }
}
