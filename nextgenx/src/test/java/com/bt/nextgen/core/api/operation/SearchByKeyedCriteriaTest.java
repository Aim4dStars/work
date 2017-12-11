package com.bt.nextgen.core.api.operation;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;

public class SearchByKeyedCriteriaTest
{
	@Test
	public void testFindByKey_whenOperationInvokedWithIncompleteKey_thenBadRequestException()
	{
		SearchByKeyedCriteriaDtoService <TestKey, TestDto> service = new SearchByKeyedCriteriaDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new SearchByKeyedCriteria <TestKey, TestDto>("vTest", service, new TestKey("k1", null), "search string").performOperation();
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}

	@Test
	public void testSearchByKeyedCriteria_whenOperationInvoked_thenListIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		SearchByKeyedCriteriaDtoService <TestKey, TestDto> service = new SearchByKeyedCriteriaDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors)
			{
				Assert.assertEquals("k1", key.getAttr1());
				Assert.assertEquals("k2", key.getAttr2());
				return Collections.singletonList(dto);
			}

		};
		ApiResponse response = new SearchByKeyedCriteria <TestKey, TestDto>("vTest",
			service,
			dto.getKey(),
			"[{'prop':'attr1','op':'=','val':'a','type':'string'}]").performOperation();
		List <TestDto> dtoList = ((ResultListDto <TestDto>)response.getData()).getResultList();
		Assert.assertEquals(dtoList.size(), 1);
		Assert.assertEquals(dtoList.get(0), dto);
	}

	@Test
	public void testSearchByKeyedCriteria_whenServiceReturnsNoResults_thenNotFoundException()
	{
		SearchByKeyedCriteriaDtoService <TestKey, TestDto> service = new SearchByKeyedCriteriaDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			ApiResponse response = new SearchByKeyedCriteria <TestKey, TestDto>("vTest",
				service,
				new TestKey("k1", "k2"),
				"[{'prop':'attr1','op':'=','val':'a','type':'string'}]").performOperation();
            List <TestDto> dtoList = ((ResultListDto <TestDto>)response.getData()).getResultList();
			Assert.assertNotNull(response);
            Assert.assertNull(dtoList);
		}
		catch (NotFoundException e)
		{}
	}

	@Test
	public void testSearchByKeyedCriteria_whenIllegalJSON_thenBadRequest()
	{
		SearchByKeyedCriteriaDtoService <TestKey, TestDto> service = new SearchByKeyedCriteriaDtoService <TestKey, TestDto>()
		{
			@Override
			public List <TestDto> search(TestKey key, List <ApiSearchCriteria> criteria, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new SearchByKeyedCriteria <TestKey, TestDto>("vTest", service, new TestKey("k1", "k2"), "not a valid search string").performOperation();
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}
}
