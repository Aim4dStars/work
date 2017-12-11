package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.ApiResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ChainedControllerOperationTest
{
	@Test
	public void testPerformOperation_whenAOperationIsInvoked_thenThenSoIsTheChainedOperation()
	{
		final List <ControllerOperation> ops = new ArrayList <>();

		ControllerOperation op = new ControllerOperation()
		{
			@Override
			public ApiResponse performOperation()
			{
				ops.add(this);
				return null;
			}
		};

		ControllerOperation op2 = new ChainedControllerOperation(op)
		{
			@Override
			public ApiResponse performChainedOperation(ApiResponse r)
			{
				ops.add(this);
				return r;
			}
		};

		op2.performOperation();
		Assert.assertEquals(op, ops.get(0));
		Assert.assertEquals(op2, ops.get(1));
	}

}
