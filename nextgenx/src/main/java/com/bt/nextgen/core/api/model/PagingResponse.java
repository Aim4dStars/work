package com.bt.nextgen.core.api.model;

public class PagingResponse
{
	private final Integer startIndex;
	private final Integer size;

	public PagingResponse(Integer startIndex, Integer size)
	{
		super();
		this.startIndex = startIndex;
		this.size = size;
	}

	public Integer getStartIndex()
	{
		return startIndex;
	}

	public Integer getSize()
	{
		return size;
	}

}
