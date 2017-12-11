package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.FileDto;
import com.bt.nextgen.core.api.model.KeyedDto;

@Deprecated
public class BglDataDto extends FileDto implements KeyedDto <DateRangeAccountKey>
{
	private DateRangeAccountKey key;

	public BglDataDto(DateRangeAccountKey key, byte[] data)
	{
		super(data);
		this.key = key;
	}

	@Override
	public DateRangeAccountKey getKey()
	{
		return key;
	}

}
