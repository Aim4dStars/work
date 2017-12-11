package com.bt.nextgen.core.web.interceptor;

import com.bt.nextgen.config.WebConfig;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Arrays;
import java.util.Set;

import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToDev;
import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToProd;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { WebConfig.class })
public class Spring3CorsFilterHandlerInterceptorTest {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Test
    public void interceptor_is_on_request() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/public/version");

        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);

        Optional<Spring3CorsFilterHandlerInterceptor> containsHandler = FluentIterable
                .from(Arrays.asList(handlerExecutionChain.getInterceptors()))
                .filter(Spring3CorsFilterHandlerInterceptor.class).first();

        // Note that this will be present for all requests due to the mapping in spring-security.xml
        assertTrue(containsHandler.isPresent());
    }

    @Test
    public void interceptor_is_not_run_on_non_annotated_request() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/public/home");

        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);

        Optional<Spring3CorsFilterHandlerInterceptor> containsHandler = FluentIterable
                .from(Arrays.asList(handlerExecutionChain.getInterceptors()))
                .filter(Spring3CorsFilterHandlerInterceptor.class).first();

        MockHttpServletResponse response = new MockHttpServletResponse();

        Spring3CorsFilterHandlerInterceptor handlerInterceptor = containsHandler.get();
        handlerInterceptor.preHandle(request, response, handlerExecutionChain.getHandler());

        Set<String> headerNames = response.getHeaderNames();
        assertFalse(headerNames.contains("Access-Control-Allow-Origin"));
        assertFalse(headerNames.contains("Access-Control-Allow-Methods"));
        assertFalse(headerNames.contains("Access-Control-Max-Age"));
        assertFalse(headerNames.contains("Access-Control-Allow-Headers"));
    }

    @Test
    public void interceptor_runs_on_annotated_request() throws Exception {
        setEnvironmentToDev();

        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/public/version");
        MockHttpServletResponse response = new MockHttpServletResponse();

        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);

        Optional<Spring3CorsFilterHandlerInterceptor> containsHandler = FluentIterable
                .from(Arrays.asList(handlerExecutionChain.getInterceptors()))
                .filter(Spring3CorsFilterHandlerInterceptor.class).first();

        Spring3CorsFilterHandlerInterceptor handlerInterceptor = containsHandler.get();
        handlerInterceptor.preHandle(request, response, handlerExecutionChain.getHandler());

        Set<String> headerNames = response.getHeaderNames();
        assertTrue(headerNames.contains("Access-Control-Allow-Origin"));
        assertTrue(headerNames.contains("Access-Control-Allow-Methods"));
        assertTrue(headerNames.contains("Access-Control-Max-Age"));
        assertTrue(headerNames.contains("Access-Control-Allow-Headers"));
    }

    @Test
    public void interceptor_is_not_run_in_prod() throws Exception {
        setEnvironmentToProd();

        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/public/version");
        MockHttpServletResponse response = new MockHttpServletResponse();

        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);

        Optional<Spring3CorsFilterHandlerInterceptor> containsHandler = FluentIterable
                .from(Arrays.asList(handlerExecutionChain.getInterceptors()))
                .filter(Spring3CorsFilterHandlerInterceptor.class).first();

        Spring3CorsFilterHandlerInterceptor handlerInterceptor = containsHandler.get();
        handlerInterceptor.preHandle(request, response, handlerExecutionChain.getHandler());

        Set<String> headerNames = response.getHeaderNames();
        assertFalse(headerNames.contains("Access-Control-Allow-Origin"));
        assertFalse(headerNames.contains("Access-Control-Allow-Methods"));
        assertFalse(headerNames.contains("Access-Control-Max-Age"));
        assertFalse(headerNames.contains("Access-Control-Allow-Headers"));

        setEnvironmentToDev();
    }
}
