package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.ApiResponse;

/**
 * A controller operation is a peice of functionality that an api operation can support (eg find/search/sort etc)
 */
public interface ControllerOperation
{
	public ApiResponse performOperation();
}
