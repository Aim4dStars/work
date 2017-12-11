package com.bt.nextgen.core.util;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public final class Identity
{
	public static String randomID()
	{
		return StringUtils.remove(randomUUID(), "-");
	}

	public static String randomUUID()
	{
		return UUID.randomUUID().toString();
	}

	public static String randomReceiptNumber()
	{
		return RandomStringUtils.randomNumeric(7) + RandomStringUtils.randomAlphabetic(3).toUpperCase();
	}
}
