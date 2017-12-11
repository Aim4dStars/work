package com.bt.nextgen.core.api.model;

import java.util.List;

import static java.util.Arrays.asList;

public class ResultListDto <T extends Dto> extends BaseDto implements Dto
{
	private final List <T> resultList;

	public ResultListDto(List <T> resultList)
	{
		this.resultList = resultList;
	}

	@SafeVarargs
	public ResultListDto(T... results) {
		this(asList(results));
	}

	public List <T> getResultList()
	{
		return resultList;
	}
}
