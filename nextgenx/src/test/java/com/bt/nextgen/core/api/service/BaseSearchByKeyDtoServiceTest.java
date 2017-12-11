package com.bt.nextgen.core.api.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.core.api.dto.BaseSearchByKeyDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.TestDto;
import com.bt.nextgen.core.api.operation.TestKey;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

public class BaseSearchByKeyDtoServiceTest
{
	private class SearchByKeyService extends BaseSearchByKeyDtoService <TestKey, TestDto>
	{
		@Override
		public List <TestDto> search(TestKey key, ServiceErrors serviceErrors)
		{
			ArrayList <TestDto> tests = new ArrayList <>();
			tests.add(new TestDto(key, "test1att1", "test1att2"));
			tests.add(new TestDto(key, "test2att1", "test2att2"));
			return tests;
		}
	};

	@Test
	public void testFind_whenServiceReturnsMoreThanOneResult_thenBadRequestException()
	{
		TestKey key = new TestKey("a1", "a2");

		try
		{
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			new SearchByKeyService().find(key, serviceErrors);
			Assert.fail();
		}
		catch (BadRequestException e)
		{}
	}
}
