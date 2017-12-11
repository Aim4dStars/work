package com.bt.nextgen.core.api.operation;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;

public class FindAllTest
{
	@Test
	public void testFindAll_whenOperationInvoked_thenListIsReturned()
	{

		final TestDto dto = new TestDto(new TestKey("k1", "k2"), "a", "1");
		FindAllDtoService <TestDto> service = new FindAllDtoService <TestDto>()
		{
			@Override
			public List <TestDto> findAll(ServiceErrors serviceErrors)
			{
				return Collections.singletonList(dto);
			}
		};
		ApiResponse response = new FindAll <TestDto>("vTest", service).performOperation();
		List <TestDto> dtoList = ((ResultListDto <TestDto>)response.getData()).getResultList();
		Assert.assertEquals(dtoList.size(), 1);
		Assert.assertEquals(dtoList.get(0), dto);
	}

}
