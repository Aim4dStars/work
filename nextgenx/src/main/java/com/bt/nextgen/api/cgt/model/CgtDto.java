package com.bt.nextgen.api.cgt.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class CgtDto extends BaseDto implements KeyedDto <CgtKey>
{
	private CgtKey key;
	private List <CgtGroupDto> cgtGroupDtoList;

	public CgtDto(CgtKey key, List <CgtGroupDto> cgtGroupDtoList)
	{
		super();
		this.key = key;
		this.cgtGroupDtoList = cgtGroupDtoList;
	}

	@Override
	public CgtKey getKey()
	{
		return key;
	}

	public List <CgtGroupDto> getCgtGroupDtoList()
	{
		return cgtGroupDtoList;
	}

}
