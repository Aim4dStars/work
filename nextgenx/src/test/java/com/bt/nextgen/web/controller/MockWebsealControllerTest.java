package com.bt.nextgen.web.controller;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MockWebsealControllerTest
{
    @InjectMocks
	private MockWebsealController websealController;

	@Test
	public void testLogonFailed()
	{
		String view = websealController.logonFailed();
		assertThat(view, Is.is( LogonController.REDIRECT_LOGON + "?TAM_OP=auth_failure"));
	}
	
	@Test
	public void testLogonFailed_Url() throws Exception{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/public/page/logonFailure");
		request.setMethod(RequestMethod.GET.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView  modelAndView = annotationMethodHandlerAdapter.handle(request, response, websealController);
		assertThat(modelAndView.getViewName(),Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=auth_failure"));
	}

	@Test
	public void testLogoutSuccess()
	{
		String view = websealController.logoutSuccess();
		assertThat(view, Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=logout"));
	}
	
	@Test
	public void testLogoutSuccess_Url() throws Exception
	{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/public/page/logoutSuccess");
		request.setMethod(RequestMethod.GET.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView  modelAndView = annotationMethodHandlerAdapter.handle(request, response, websealController);
		assertThat(modelAndView.getViewName(),Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=logout"));
	}

	@Test
	public void testAccessDenied()
	{
		String view = websealController.accessDenied();
		assertThat(view, Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=auth_failure"));
	}
	
	@Test
	public void testAccessDenied_Url() throws Exception
	{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/public/page/accessDenied");
		request.setMethod(RequestMethod.GET.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView  modelAndView = annotationMethodHandlerAdapter.handle(request, response, websealController);
		assertThat(modelAndView.getViewName(),Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=auth_failure"));
	}

	@Test
	public void testRequestLogon()
	{
		String view = websealController.requestLogon();
		assertThat(view, Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=login"));
	}
	
	@Test
	public void testRequestLogon_Url() throws Exception{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI(LogonController.LOGON);
		request.setMethod(RequestMethod.GET.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView  modelAndView = annotationMethodHandlerAdapter.handle(request, response, websealController);
		assertThat(modelAndView.getViewName(),Is.is("redirect:"+LogonController.LOGON + "?TAM_OP=login"));
	}
	
	@Test(expected= HttpRequestMethodNotSupportedException.class)
	public void testRequestLogon_IncorrectRequestMethod() throws Exception{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI(LogonController.LOGON);
		request.setMethod(RequestMethod.POST.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
	    annotationMethodHandlerAdapter.handle(request, response, websealController);
	}

	@Test
	public void testDoLogon()
	{
		String view = websealController.doLogon();
		assertThat(view, Is.is("redirect:"+HomePageController.HOMEPAGE));
	}
	
	@Test
	public void testDoLogon_Url() throws Exception{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/security/doLogon");
		request.setMethod(RequestMethod.POST.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView  modelAndView = annotationMethodHandlerAdapter.handle(request, response, websealController);
		assertThat(modelAndView.getViewName(),Is.is( HomePageController.REDIRECT_HOMEPAGE));
	}
	
	@Test(expected= HttpRequestMethodNotSupportedException.class)
	public void testDoLogon_IncorrectRequestMethod() throws Exception{
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/security/doLogon");
		request.setMethod(RequestMethod.GET.name());
		AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
		ModelAndView  modelAndView = annotationMethodHandlerAdapter.handle(request, response, websealController);
		assertThat(modelAndView.getViewName(),Is.is( HomePageController.REDIRECT_HOMEPAGE));
	}

}
