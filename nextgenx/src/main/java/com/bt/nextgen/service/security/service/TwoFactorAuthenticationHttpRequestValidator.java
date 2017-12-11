package com.bt.nextgen.service.security.service;

import com.bt.nextgen.service.safi.model.SafiAnalyzeRequest;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@SuppressWarnings({"squid:MethodCyclomaticComplexity"})
public class TwoFactorAuthenticationHttpRequestValidator implements Validator
{
	private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationHttpRequestValidator.class);

	private static final String ERROR_MESSAGE = "contains invalid characters";


	@Override
	public boolean supports(Class<?> aClass)
	{
		return aClass.isAssignableFrom(SafiAnalyzeRequest.class);
	}

	@Override
	public void validate(Object o, Errors errors)
	{
		HttpRequestParams httpRequestParams = ((SafiAnalyzeRequest) o).getRequestParams();

		if (StringUtils.isNotEmpty(httpRequestParams.getHttpAccept()) && !isValidString(httpRequestParams.getHttpAccept()))
		{
			errors.rejectValue("http-accept", ERROR_MESSAGE);
			logger.warn("invalid httpAccept " + ERROR_MESSAGE);
		}
		else if (StringUtils.isNotEmpty(httpRequestParams.getHttpAcceptChars()) && !isValidString(httpRequestParams.getHttpAcceptChars()))
		{
			errors.rejectValue("httpRequestParams", ERROR_MESSAGE);
			logger.warn("invalid httpRequestParams " + ERROR_MESSAGE);
		}
		else if (StringUtils.isNotEmpty(httpRequestParams.getHttpAcceptEncoding()) && !isValidString(httpRequestParams.getHttpAcceptEncoding()))
		{
			errors.rejectValue("httpAcceptEncoding", ERROR_MESSAGE);
			logger.warn("invalid httpAcceptEncoding " + ERROR_MESSAGE);
		}
		else if (StringUtils.isNotEmpty(httpRequestParams.getHttpAcceptLanguage()) && !isValidString(httpRequestParams.getHttpAcceptLanguage()))
		{
			errors.rejectValue("httpAcceptLanguage", ERROR_MESSAGE);
			logger.warn("invalid httpAcceptLanguage " + ERROR_MESSAGE);
		}
		else if (StringUtils.isNotEmpty(httpRequestParams.getHttpOriginatingIpAddress()) && !isValidString(httpRequestParams.getHttpOriginatingIpAddress()))
		{
			errors.rejectValue("httpOriginatingIpAddress", ERROR_MESSAGE);
			logger.warn("invalid httpOriginatingIpAddress " + ERROR_MESSAGE);
		}
		else if (StringUtils.isNotEmpty(httpRequestParams.getHttpReferrer()) &&
                // Done as some versions of IE send full URL for HTTP_REFERER including # and ?
                !isValidString(StringUtils.substringBefore(httpRequestParams.getHttpReferrer(),"#")))
		{
			errors.rejectValue("httpReferer", ERROR_MESSAGE);
			logger.warn("invalid httpReferer " + ERROR_MESSAGE);
		}
		else if (StringUtils.isNotEmpty(httpRequestParams.getHttpXForwardedHost()) && !isValidString(httpRequestParams.getHttpXForwardedHost()))
		{
			errors.rejectValue("httpXForwardedHost", ERROR_MESSAGE);
			logger.warn("invalid httpXForwardedHost " + ERROR_MESSAGE);
		}
	}

	public boolean isValidString(String value)
	{
		return value.matches("[a-zA-Z0-9-_/()+=;:\\* ,.&]*");
	}
}