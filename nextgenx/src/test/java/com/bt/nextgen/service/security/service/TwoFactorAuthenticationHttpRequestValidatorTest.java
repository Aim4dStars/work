package com.bt.nextgen.service.security.service;

import com.bt.nextgen.service.safi.model.SafiAnalyzeRequest;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class TwoFactorAuthenticationHttpRequestValidatorTest
{
	@Test
	public void testValidHttpAcceptParameterStringValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("application/json, text/javascript, */*; q=0.01");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testInValidHttpAcceptParameterStringValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("ABCDE@@*");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(1)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testValidHttpRefererValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("http://dwgps0026:7990/projects/BT/repos/nextgen/commits/8cd2d2f1833141fe74fa37487b38daefafdcc603");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testValidHttpAcceptCharsValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testValidHttpAcceptEncodingValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("gzip,deflate");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testValidHttpAcceptLanguageValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("en-ua,en;q=0.5");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testValidIpAddressValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("52.76.40.19");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testValidUserAgentValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.04506.30)");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(0)).rejectValue(anyString(), anyString());
	}

	@Test
	public void testInvalidUserAgentValue()
	{
		SafiAnalyzeRequest request = new SafiAnalyzeRequest();
		HttpRequestParams params = new HttpRequestParams();
		params.setHttpAccept("Internet Explorer 6.<script> (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.04506.30)");
		request.setHttpRequestParams(params);

		TwoFactorAuthenticationHttpRequestValidator validator = new TwoFactorAuthenticationHttpRequestValidator();
		Errors errors = Mockito.mock(Errors.class);

		validator.validate(request, errors);
		Mockito.verify(errors, times(1)).rejectValue(anyString(), anyString());
	}
}