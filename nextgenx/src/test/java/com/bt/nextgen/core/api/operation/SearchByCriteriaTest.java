package com.bt.nextgen.core.api.operation;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;

public class SearchByCriteriaTest
{
	@Test
	public void testSearchByCriteria_whenOperationInvoked_thenListIsReturned()
	{
		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		SearchByCriteriaDtoService <TestDto> service = new SearchByCriteriaDtoService <TestDto>()
		{
			@Override
			public List <TestDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
			{
				return Collections.singletonList(dto);
			}
		};
		ApiResponse response = new SearchByCriteria <TestDto>("vTest",
			service,
			"[{'prop':'attr1','op':'=','val':'a','type':'string'}]").performOperation();
		List <TestDto> dtoList = ((ResultListDto <TestDto>)response.getData()).getResultList();
		Assert.assertEquals(dtoList.size(), 1);
		Assert.assertEquals(dtoList.get(0), dto);
	}

	// TODO: commented out for now pending discussion
	//	@Test
	//	public void testSearchByCriteria_whenServiceReturnsNoResults_thenNotFoundException()
	//	{
	//		SearchByCriteriaDtoService <TestDto> service = new SearchByCriteriaDtoService <TestDto>()
	//		{
	//			@Override
	//			public List <TestDto> search(List <ApiSearchCriteria> criteriaList)
	//			{
	//				return null;
	//			}
	//		};
	//
	//		try
	//		{
	//			new SearchByCriteria <TestDto>("vTest", service, "[{'prop':'attr1','op':'=','val':'a','type':'string'}]").performOperation();
	//			Assert.fail();
	//		}
	//		catch (NotFoundException e)
	//		{}
	//	}

	@Test
	public void testSearchByCriteria_whenIllegalJSON_thenBadRequest()
	{
		SearchByCriteriaDtoService <TestDto> service = new SearchByCriteriaDtoService <TestDto>()
		{
			@Override
			public List <TestDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new SearchByCriteria <TestDto>("vTest", service, "not valid json").performOperation();
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}
}
