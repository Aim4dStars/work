package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class TestDto extends BaseDto implements KeyedDto <TestKey>
{
	private TestKey key;
	private String attr1;
	private String attr2;

	public TestDto(TestKey key, String attr1, String attr2)
	{
		super();
		this.key = key;
		this.attr1 = attr1;
		this.attr2 = attr2;
	}

	public void setKey(TestKey key)
	{
		this.key = key;
	}

	public String getAttr1()
	{
		return attr1;
	}

	public void setAttr1(String attr1)
	{
		this.attr1 = attr1;
	}

	public String getAttr2()
	{
		return attr2;
	}

	public void setAttr2(String attr2)
	{
		this.attr2 = attr2;
	}

	@Override
	public TestKey getKey()
	{
		return key;
	}
}