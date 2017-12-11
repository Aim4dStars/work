package com.bt.nextgen.core.api.operation;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;

public class CreateTest
{
	@Test
	public void testCreate_whenOperationInvoked_thenKeyedDtoIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		CreateDtoService <TestKey, TestDto> service = new CreateDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto create(TestDto dto, ServiceErrors serviceErrors)
			{
				return dto;
			}
		};
		KeyedApiResponse <TestKey> response = new Create <TestKey, TestDto>("vTest", service, dto).performOperation();
		Dto d = response.getData();
		TestKey key = response.getId();
		Assert.assertEquals("k1", key.getAttr1());
		Assert.assertEquals("k2", key.getAttr2());
		Assert.assertEquals(d, dto);
	}

	@Test
	public void testCreate_whenServiceReturnsNoResults_thenNotFoundException()
	{
		CreateDtoService <TestKey, TestDto> service = new CreateDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto create(TestDto dto, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new Create <TestKey, TestDto>("vTest", service, null).performOperation();
			Assert.fail();
		}
		catch (NotFoundException e)
		{}
	}

	@Test
	public void testCreate_whenServiceReturnsIncompleteKey_thenNotFoundException()
	{
		final TestDto dto = new TestDto(new TestKey("k1", null), "a", "1");
		CreateDtoService <TestKey, TestDto> service = new CreateDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto create(TestDto dto, ServiceErrors serviceErrors)
			{
				return dto;
			}
		};

		try
		{
			new Create <TestKey, TestDto>("vTest", service, dto).performOperation();
			Assert.fail();
		}
		catch (NotFoundException e)
		{}
	}
}
