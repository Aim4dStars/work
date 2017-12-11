package com.bt.nextgen.core.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public final class UriEncoder
{
	public static String encode(String value)
	{
		if (value == null)
		{
			return null;
		}
		return encode(value.getBytes());
	}

	public static String encode(byte[] value)
	{
		if (value == null)
		{
			return null;
		}
		return Base64.encodeBase64String(value);
	}

	public static String decode(String value)
	{
		if (value == null)
		{
			return null;
		}

		try
		{
			return new String(Base64.decodeBase64(value), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			return null;
		}
	}
}
