package com.bt.nextgen.core.api.operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.model.ResultMapDto;

public class GroupTest
{
	private static List <TestDto> testList;

	class TestOperation implements ControllerOperation
	{
		@Override
		public ApiResponse performOperation()
		{
			return new ApiResponse("Vtest", new ResultListDto <>(new ArrayList <TestDto>(testList)));
		}
	}

	@Before
	public void setup()
	{
		testList = new ArrayList <>();
		testList.add(new TestDto(new TestKey("k1", "k2"), "c", "1"));
		testList.add(new TestDto(new TestKey("k3", "k4"), "a", "2"));
		testList.add(new TestDto(new TestKey("k5", "k6"), "a", "3"));
		testList.add(new TestDto(new TestKey("k7", "k8"), "b", "3"));
		testList = Collections.unmodifiableList(testList);
	}

	@Test
	public void testPerformOperation_whenOperationCalled_thenTheGroupedAccordingToThatAttribute()
	{
		ApiResponse response = new Group <>("vTest", new TestOperation(), "attr1").performOperation();
		Map <? , List <TestDto>> result = ((ResultMapDto <TestDto>)response.getData()).getResultMap();

		Assert.assertEquals(result.keySet().size(), 3);
		Assert.assertEquals(result.get("a").size(), 2);
		Assert.assertEquals(result.get("b").size(), 1);
		Assert.assertEquals(result.get("c").size(), 1);
	}
}
