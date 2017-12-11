package com.bt.nextgen.core.api.model;

import java.util.List;
import java.util.Map;

public class ResultMapDto <T extends Dto> extends BaseDto implements Dto
{
	private Map <? , List <T>> resultMap;

	public ResultMapDto(Map <? , List <T>> resultMap)
	{
		super();
		this.resultMap = resultMap;
	}

	public Map <? , List <T>> getResultMap()
	{
		return resultMap;
	}
}
