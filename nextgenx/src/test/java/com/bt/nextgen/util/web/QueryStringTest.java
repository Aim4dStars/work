package com.bt.nextgen.util.web;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueryStringTest
{
	@Test
	public void testBasicQueryString() throws Exception
	{
		com.bt.nextgen.core.web.util.QueryString queryString = new com.bt.nextgen.core.web.util.QueryString();
		queryString.add("account", "123456");
		assertEquals(queryString.toString(), "?account=123456");
	}

	@Test
	public void testMultipleString() throws Exception
	{
		com.bt.nextgen.core.web.util.QueryString queryString = new com.bt.nextgen.core.web.util.QueryString();
		queryString.add("account", "123456");
		queryString.add("date", "01/01/1980");
		assertEquals(queryString.toString(), "?account=123456&date=01%2F01%2F1980");
	}

	@Test
	public void testComplexString() throws Exception
	{
		com.bt.nextgen.core.web.util.QueryString queryString = new com.bt.nextgen.core.web.util.QueryString();
		queryString.add("account", "123456");
		queryString.add("date", "01/01/1980");
		queryString.add("data", new String("!@#$%^&*()"));
		assertEquals(queryString.toString(), "?account=123456&date=01%2F01%2F1980&data=%21%40%23%24%25%5E%26*%28%29");
	}
}
