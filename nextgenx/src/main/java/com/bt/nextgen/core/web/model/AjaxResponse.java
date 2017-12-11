package com.bt.nextgen.core.web.model;

public class AjaxResponse
{
	private final boolean success;
	private final Object data;

	public AjaxResponse(boolean success, Object data)
	{
		this.success = success;
		this.data = data;
	}

	/**
	 * This will create a response with a success code and the passed message and null data
	 *
	 * @param message
	 */
	public AjaxResponse(Object data)
	{
		this(true, data);
	}

	public AjaxResponse()
	{
		this(true, null);
	}

	public boolean isSuccess()
	{
		return success;
	}

	public Object getData()
	{
		return data;
	}
}
