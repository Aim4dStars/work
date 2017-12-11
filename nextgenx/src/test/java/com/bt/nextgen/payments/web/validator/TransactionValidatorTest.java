package com.bt.nextgen.payments.web.validator;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.core.web.model.SearchParams;


@RunWith(MockitoJUnitRunner.class)
public class TransactionValidatorTest
{
	@InjectMocks
	private TransactionValidator transactionValidator;

	@Mock
	private Validator validator;
	private  MockHttpServletRequest request;
	
	@Before
	public void setup() throws Exception
	{
		request = new MockHttpServletRequest();
	}	


	@Test
	public void test_Validate() throws Exception
	{
		
		Errors errors = mock(Errors.class);
		transactionValidator.validate(SearchParams.ADVISER_NAME, errors);
		verify(validator, times(1)).validate(SearchParams.ADVISER_NAME, errors);
	}
	@Test(expected=Exception.class)
	public void test_Validate_Exception() throws Exception 
	{
		Map<String , String> params = new HashMap<>();
		params.put("key1", "AVALOQ_SEARCH_PARAMS");
		request.setParameters(params);
		transactionValidator.validateHttpRequestParameters(request);
	}
	
	@Test
	public void test_Validate_requestParams() throws Exception 
	{
		Map<String , String> params = new HashMap<>();
		params.put("TYPE", "AVALOQ_SEARCH_PARAMS");
		request.setParameters(params);
		transactionValidator.validateHttpRequestParameters(request);
		assertThat(request.getParameterNames(), notNullValue());
	}
}
