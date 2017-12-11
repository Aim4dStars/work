package com.bt.nextgen.security;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.junit.Test;

public class UriEncoderTest
{
	@Test
	public void testNullEncode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.encode((String) null), is(nullValue()));
	}

	@Test
	public void testEmptyStringEncode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.encode(""), is(""));
	}

	@Test
	public void testBasicEncode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.encode("123"), is("MTIz"));
	}

	@Test
	public void testBasicEncode_bytearray()
	{
		byte[] data = null;
		assertThat(com.bt.nextgen.core.util.UriEncoder.encode(data), nullValue());
	}

	@Test
	public void testComplexEncode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.encode("G$@#HJGV*#^t78dbt8^$2378564b2378"),
			is("RyRAI0hKR1YqI150NzhkYnQ4XiQyMzc4NTY0YjIzNzg="));
	}

	@Test
	public void testNullDecode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.decode(null), is(nullValue()));
	}

	@Test
	public void testEmptyStringDecode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.decode(""), is(""));
	}

	@Test
	public void testBasicDecode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.decode("MTIz"), is("123"));
	}

	@Test
	public void testComplexDecode()
	{
		assertThat(com.bt.nextgen.core.util.UriEncoder.decode("RyRAI0hKR1YqI150NzhkYnQ4XiQyMzc4NTY0YjIzNzg="),
			is("G$@#HJGV*#^t78dbt8^$2378564b2378"));
	}
}
