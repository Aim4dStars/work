package com.bt.nextgen.core.validation;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.service.MessageServiceImpl;
import com.bt.nextgen.core.util.DatabaseManager;
import com.bt.nextgen.core.validation.ValidationError;

public class ValidationFormatterTest {

	@InjectMocks
	private ValidationFormatter validationFormatter;
	
	@Mock
	private MessageService msgService = new MessageServiceImpl();
	
	ApplicationContext context;
	ApplicationContextProvider provider;
	BindingResult bindingResult;
	private String ERROR_MSG = "error is here";
	private String FIELD_NAME = "field_name";
	private String ERROR_CODE = "msg000";
	
	@Before
	public void setup()
	{
		context = mock(ApplicationContext.class);
		provider = new ApplicationContextProvider(new DatabaseManager());
		provider.setApplicationContext(context);
		bindingResult = mock(BindingResult.class);
		
		ApplicationContext applicationContext = mock(ApplicationContext.class);
		CmsService cmsService = mock(CmsService.class);
		Mockito.when(applicationContext.getBean(any(Class.class))).thenReturn(cmsService);
		ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
		applicationContextProvider.setApplicationContext(applicationContext);
		
		Mockito.when(msgService.lookup(ERROR_CODE)).thenReturn(ERROR_MSG);
		
	}
	
	@Test
	public void testValidationFormatterWithArgument()
	{
		String codes[] = {ERROR_CODE};
		DefaultMessageSourceResolvable resolver = new DefaultMessageSourceResolvable(codes, FIELD_NAME);
		Object arguments[] = {resolver};
		ObjectError objectError = new ObjectError(FIELD_NAME, codes, arguments, ERROR_MSG);
		List<ObjectError> errors = new ArrayList<>();
		errors.add(objectError);
		Mockito.when(bindingResult.getAllErrors()).thenReturn(errors);
		Collection<ValidationError> formatResult = (Collection<ValidationError>) validationFormatter.format(bindingResult);
		assertNotNull(formatResult);
		for (ValidationError formatted : formatResult) 
		{
			assertThat(formatted.getField(), is(FIELD_NAME));
			assertThat(formatted.getMessage(), is(ERROR_MSG));
	    }
	}
	
	@Test
	public void testValidationFormatterWithoutArgument() 
	{
		String codes[] = {ERROR_CODE};
		Object arguments[] = null;
		ObjectError objectError = new ObjectError(FIELD_NAME, codes, arguments, ERROR_MSG);
		List<ObjectError> errors = new ArrayList<>();
		errors.add(objectError);
		Mockito.when(bindingResult.getAllErrors()).thenReturn(errors);
		Object abc = validationFormatter.format(bindingResult);
		assertThat(abc.toString(), is(ERROR_MSG));
	}

}
