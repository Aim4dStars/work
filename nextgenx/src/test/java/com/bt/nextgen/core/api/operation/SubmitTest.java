package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.ws.soap.saaj.SaajSoapEnvelopeException;

public class SubmitTest
{
	@Test
	public void testSubmit_whenOperationInvoked_thenKeyedDtoIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		SubmitDtoService <TestKey, TestDto> service = new SubmitDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto submit(TestDto keyedObject, ServiceErrors serviceErrors)
			{
				return dto;
			}
		};

		KeyedApiResponse <TestKey> response = new Submit <TestKey, TestDto>("vTest", service, null, dto).performOperation();
		Dto d = response.getData();
		TestKey key = response.getId();
		Assert.assertEquals("k1", key.getAttr1());
		Assert.assertEquals("k2", key.getAttr2());
		Assert.assertEquals(d, dto);
	}

	@Test
	public void testSubmit_whenServiceReturnsNoResults_thenNotFoundException()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		SubmitDtoService <TestKey, TestDto> service = new SubmitDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto submit(TestDto keyedObject, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new Submit <TestKey, TestDto>("vTest", service, null, dto).performOperation();
			Assert.fail();
		}
		catch (NotFoundException e)
		{}
	}

	@Test
	public void testSubmit_whenSaajSoapEnvelopeExceptionThrown_thenSetAppropriateMessage()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		SubmitDtoService <TestKey, TestDto> service = new SubmitDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto submit(TestDto keyedObject, ServiceErrors serviceErrors)
			{
				throw new SaajSoapEnvelopeException("Test Message");
			}
		};

		try
		{
			new Submit <TestKey, TestDto>("vTest", service, null, dto).performOperation();
			Assert.fail();
		}
		catch (ApiException e)
		{
			Assert.assertEquals("Please check if adviser/investor details provided in the application are valid as expected by ICC", e.getMessage());
		}
	}
}
