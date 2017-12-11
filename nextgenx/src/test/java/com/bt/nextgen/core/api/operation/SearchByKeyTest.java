package com.bt.nextgen.core.api.operation;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;

public class SearchByKeyTest
{
	@Test
	public void testSearchByKey_whenOperationInvokedWithNullKey_thenBadRequestException()
	{
		SearchByKeyDtoService <TestKey, TestDto> service = new SearchByKeyDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, ServiceErrors serviceErrors)
			{
				return null;
			}

			@Override
			public TestDto find(TestKey key, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new SearchByKey <TestKey, TestDto>("vTest", service, null).performOperation();
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}

	@Test
	public void testSearchByKey_whenOperationInvoked_thenListIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		SearchByKeyDtoService <TestKey, TestDto> service = new SearchByKeyDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, ServiceErrors serviceErrors)
			{
				Assert.assertEquals("k1", key.getAttr1());
				Assert.assertEquals("k2", key.getAttr2());
				return Collections.singletonList(dto);
			}

			@Override
			public TestDto find(TestKey key, ServiceErrors serviceErrors)
			{
				Assert.assertEquals("k1", key.getAttr1());
				Assert.assertEquals("k2", key.getAttr2());
				return dto;
			}
		};
		ApiResponse response = new SearchByKey <TestKey, TestDto>("vTest", service, dto.getKey()).performOperation();
		List <TestDto> dtoList = ((ResultListDto <TestDto>)response.getData()).getResultList();
		Assert.assertEquals(dtoList.size(), 1);
		Assert.assertEquals(dtoList.get(0), dto);
	}

	@Test
	public void testSearchByKey_whenServiceReturnsNoResults_thenNotFoundException()
	{
		SearchByKeyDtoService <TestKey, TestDto> service = new SearchByKeyDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, ServiceErrors serviceErrors)
			{
				return null;
			}

			@Override
			public TestDto find(TestKey key, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new SearchByKey <TestKey, TestDto>("vTest", service, new TestKey("k1", "k2")).performOperation();
			Assert.fail();
		}
		catch (NotFoundException e)
		{}
	}
}
