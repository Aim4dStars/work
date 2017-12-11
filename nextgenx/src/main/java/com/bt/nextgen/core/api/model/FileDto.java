package com.bt.nextgen.core.api.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDto extends BaseDto
{
	private byte[] data;

	public FileDto(byte[] data)
	{
		super();
		this.data = data;
	}

	public InputStream getStream() throws IOException
	{
		return new ByteArrayInputStream(data);
	}

}
