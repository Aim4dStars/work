package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class FindOneTest
{
	@Test
	public void testFindOne_whenOperationInvoked_thenListIsReturned()
	{

		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		FindOneDtoService<TestDto> service = new FindOneDtoService <TestDto>()
		{
			@Override
			public TestDto findOne(ServiceErrors serviceErrors)
			{
				return dto;
			}
		};
		ApiResponse response = new FindOne <TestDto>("vTest", service).performOperation();
		TestDto dtoResult = ((TestDto)response.getData());
		Assert.assertEquals(dtoResult.getAttr1(), "a");
	}

	@Test
	public void testFindOne_whenServiceReturnsNoResults_thenNotFoundException()
	{
		FindOneDtoService <TestDto> service = new FindOneDtoService <TestDto>()
		{
			@Override
			public TestDto findOne(ServiceErrors serviceErrors)
			{
				return null;
			}
		};

		try
		{
			new FindOne <TestDto>("vTest", service).performOperation();
			Assert.fail();
		}
		catch (NotFoundException e)
		{}

	}

}
