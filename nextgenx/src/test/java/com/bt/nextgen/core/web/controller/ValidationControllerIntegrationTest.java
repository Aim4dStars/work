package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.validator.FieldValidator;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

public class ValidationControllerIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private ValidationController validationController;
	@Autowired
	private FieldValidator fieldValidator;

		@Before
		public void setup()
		{
			fieldValidator = Mockito.mock(FieldValidator.class, Answers.RETURNS_SMART_NULLS.get());
			ReflectionTestUtils.setField(validationController, "fieldValidator", fieldValidator);
			ReflectionTestUtils.setField(validationController, "messageService", Mockito.mock(MessageService.class));
		}

	//@Ignore
	@Test
	public void testInvalidArgsError() throws Exception
	{
		String fieldId = "code";
		String fieldValue = "3320211";
		AjaxResponse result = validationController.validate("bsb", fieldId, fieldValue);
		Assert.assertThat(result.isSuccess(), Is.is(false));
	}
	
	//@Ignore
	@Test
	public void testCleanCallOk() throws Exception
	{
		String fieldId = "code";
		String fieldValue = "332027";
		AjaxResponse result = validationController.validate("bsb", fieldId, fieldValue);
		Assert.assertThat(StringUtils.isBlank((String)result.getData()), Is.is(true));
	}



}
