package com.bt.nextgen.core.web.controller;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.Errors;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bt.nextgen.addressbook.PayeeValidator;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.validator.FieldValidator;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.web.validator.ValidationErrorCode;


@RunWith(MockitoJUnitRunner.class)
public class ValidationControllerTest
{
	@InjectMocks
	private ValidationController validationController = new ValidationController();
	
	@Mock
	private FieldValidator fieldValidator;

	@Mock
	private PayeeValidator payeeValidator;
	
	@Mock
	private CmsService cmsService;

/*	String fieldId = "code";
	String fieldValue = "3320211";
	AjaxResponse result = validationController.validate("bsb", fieldId, fieldValue);
	Assert.assertThat(result.isSuccess(), Is.is(false));
*/	
	@Before
	public void setup()
	{
		Mockito.doAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return ValidationErrorCode.INVALID_BPAY_BILLER;
			}
			
		}).when(payeeValidator).validateBillerCode(anyString(), any(Errors.class));
		
		Mockito.doAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return ValidationErrorCode.INVALID_BSB;
			}
			
		}).when(payeeValidator).validateBsb(anyString(), any(Errors.class));
	}
	
	@Test
	public void testValidate_RequestMethod_URL_Valid() throws Exception
	{
		
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.GET.name(), "/secure/api/validateField");
        request.setParameter("conversationId", "bsb");
        request.setParameter("fieldId", "");
        request.setParameter("data","");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters); 
        annotationMethodHandlerAdapter.handle(request, response, validationController);
        
        assertThat(HttpServletResponse.SC_OK, Is.is(response.getStatus()));
	}
	
	@Test(expected=HttpRequestMethodNotSupportedException.class)
	public void testValidate_RequestMethod_Invalid() throws Exception
	{
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.POST.name(), "/secure/api/validateField");
        request.setParameter("conversationId", "bsb");
        request.setParameter("fieldId", "");
        request.setParameter("data","");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters); 
        annotationMethodHandlerAdapter.handle(request, response, validationController);
        
        assertThat(HttpServletResponse.SC_OK, Is.is(response.getStatus()));
	}

	@Test(expected=NoSuchRequestHandlingMethodException.class)
	public void testValidate_URL_Invalid() throws Exception
	{
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.GET.name(), "/secure/api/validateFields");
        request.setParameter("conversationId", "bsb");
        request.setParameter("fieldId", "");
        request.setParameter("data","");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters); 
        annotationMethodHandlerAdapter.handle(request, response, validationController);
        
        assertThat(HttpServletResponse.SC_OK, Is.is(response.getStatus()));
	}

	@Test(expected=MissingServletRequestParameterException.class)
	public void testValidate_RequestParamater_Missing() throws Exception
	{
		//Request method
        MockHttpServletRequest request = new MockHttpServletRequest(
                     RequestMethod.GET.name(), "/secure/api/validateField");
        request.setParameter("fieldId", "");
        request.setParameter("data","");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        /* Checking the URL **/
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters); 
        annotationMethodHandlerAdapter.handle(request, response, validationController);
        
        assertThat(HttpServletResponse.SC_OK, Is.is(response.getStatus()));
	}


	@Test
	public void testValidate_BSB_null() throws Exception
	{
		AjaxResponse ajaxResponse = validationController.validate("bsb", null, null);
		assertThat(ajaxResponse.isSuccess(),Is.is(false));
	
	}
	
	@Test
	public void testValidate_BSB_MaxLength() throws Exception
	{
		AjaxResponse ajaxResponse = validationController.validate("bsb", null, "123-1234");
		assertThat(ajaxResponse.isSuccess(),Is.is(false));
	
	}
	
	@Test
	public void testValidate_BSB_RepositoryLoad() throws Exception
	{
		Mockito.doAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "1230123";
			}
			
		}).when(cmsService).getContent(anyString());
		
		
		AjaxResponse ajaxResponse = validationController.validate("bsb", null, "123-0123");
		assertThat(ajaxResponse.isSuccess(),Is.is(false));
		assertThat((String)ajaxResponse.getData(), Is.is("1230123"));
	
	}

	@Test
	public void testValidate_Billercode_null() throws Exception
	{

		AjaxResponse ajaxResponse = validationController.validate("billerCode", null, null);
		assertThat(ajaxResponse.isSuccess(),Is.is(false));
	
	}
	
	@Test
	public void testValidate_Billercode_MaxLength() throws Exception
	{
		AjaxResponse ajaxResponse = validationController.validate("billerCode", null, "123123123123");
		assertThat(ajaxResponse.isSuccess(),Is.is(false));
	
	}
	
	@Test
	public void testValidate_Billercode_RepositoryLoad() throws Exception
	{
		Mockito.doAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "3210321";
			}
			
		}).when(cmsService).getContent(anyString());
		
		
		AjaxResponse ajaxResponse = validationController.validate("billerCode", null, "1230123");
		assertThat(ajaxResponse.isSuccess(),Is.is(false));
		assertThat((String)ajaxResponse.getData(), Is.is("3210321"));
	
	}

}
