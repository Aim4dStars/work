package com.bt.nextgen.payments.web.validator;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.core.type.DateFormatType;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.model.SearchParams;

@Component("transactionValidator")
public class TransactionValidator implements Validator
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	@Override
	public boolean supports(Class<?> clazz)
	{
		return clazz.isAssignableFrom(SearchParams.class);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		validator.validate(target, errors);
		if (!(target instanceof SearchParams))
		{
			try {
				throw new Exception("The key to search is not valid.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void validateHttpRequestParameters(HttpServletRequest request) throws Exception
	{
		Enumeration<?> keyParams = request.getParameterNames();
		while(keyParams.hasMoreElements())
		{
			String key = (String) keyParams.nextElement();
			try 
			{
				SearchParams.valueOf(key);
			} 
			catch (IllegalArgumentException ex) 
			{  
				throw new Exception("The key ["+key+"] to search is not valid.");
			}
		}
	}
}
