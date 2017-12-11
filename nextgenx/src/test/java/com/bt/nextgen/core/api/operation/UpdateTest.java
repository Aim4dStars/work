package com.bt.nextgen.core.api.operation;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;

public class UpdateTest
{
	@Test
	public void testUpdate_whenOperationInvokedWithIncompleteKey_thenBadRequestException()
	{
		final TestDto dto = new TestDto(new TestKey("k1", null), "a", "1");
		UpdateDtoService <TestKey, TestDto> service = new UpdateDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto update(TestDto keyedObject, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new Update <TestKey, TestDto>("vTest", service, null, dto).performOperation();
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}

	@Test
	public void testUpdate_whenOperationInvoked_thenKeyedDtoIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		UpdateDtoService <TestKey, TestDto> service = new UpdateDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto update(TestDto keyedObject, ServiceErrors serviceErrors)
			{
				return dto;
			}
		};

		KeyedApiResponse <TestKey> response = new Update <TestKey, TestDto>("vTest", service, null, dto).performOperation();
		Dto d = response.getData();
		TestKey key = response.getId();
		Assert.assertEquals("k1", key.getAttr1());
		Assert.assertEquals("k2", key.getAttr2());
		Assert.assertEquals(d, dto);
	}

	@Test
	public void testUpdate_whenServiceReturnsNoResults_thenNotFoundException()
	{
		// FIXME: commented out.  SS will fix it
		/*		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
				UpdateDtoService <TestKey, TestDto> service = new UpdateDtoService <TestKey, TestDto>()
				{
					@Override
					public TestDto update(TestDto keyedObject)
					{
						return null;
					}
				};

				try
				{
					new Update <TestKey, TestDto>("vTest", service, null, dto).performOperation();
					Assert.fail();
				}
				catch (NotFoundException e)
				{}
				*/
	}
}
