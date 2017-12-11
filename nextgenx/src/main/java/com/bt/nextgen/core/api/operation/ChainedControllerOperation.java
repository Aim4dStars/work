package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.ApiResponse;

/**
 * Controller operation that decorates the results of another operation,
 * to provide additional functionality
 */
public abstract class ChainedControllerOperation implements ControllerOperation
{

	private ControllerOperation chainedOperation = null;

	public ChainedControllerOperation(ControllerOperation chainedOperation)
	{
		this.chainedOperation = chainedOperation;
	}

	@Override
	public final ApiResponse performOperation()
	{
		if (chainedOperation == null)
		{
			throw new IllegalArgumentException("Chained operation must not be null");
		}
		else
		{
			return performChainedOperation(chainedOperation.performOperation());
		}
	}

	protected abstract ApiResponse performChainedOperation(ApiResponse response);

}
