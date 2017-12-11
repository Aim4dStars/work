package com.bt.nextgen.core.api.operation;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;

public class FindByKeyTest
{
	@Test
	public void testFindByKey_whenOperationInvokedWithIncompleteKey_thenBadRequestException()
	{
		FindByKeyDtoService <TestKey, TestDto> service = new FindByKeyDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto find(TestKey key, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new FindByKey <TestKey, TestDto>("vTest", service, new TestKey("k1", null)).performOperation();
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}

	@Test
	public void testFindByKey_whenOperationInvoked_thenListIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		FindByKeyDtoService <TestKey, TestDto> service = new FindByKeyDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto find(TestKey key, ServiceErrors serviceErrors)
			{
				Assert.assertEquals("k1", key.getAttr1());
				Assert.assertEquals("k2", key.getAttr2());
				return dto;
			}
		};
		KeyedApiResponse <TestKey> response = new FindByKey <TestKey, TestDto>("vTest", service, dto.getKey()).performOperation();

		Dto d = response.getData();
		TestKey key = response.getId();
		Assert.assertEquals("k1", key.getAttr1());
		Assert.assertEquals("k2", key.getAttr2());
		Assert.assertEquals(d, dto);
	}

	@Test
	public void testFindByKeyl_whenServiceReturnsNoResults_thenNotFoundException()
	{
		FindByKeyDtoService <TestKey, TestDto> service = new FindByKeyDtoService <TestKey, TestDto>()
		{
			@Override
			public TestDto find(TestKey key, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new FindByKey <TestKey, TestDto>("vTest", service, new TestKey("k1", "k2")).performOperation();
			Assert.fail();
		}
		catch (NotFoundException e)
		{}

	}
}
