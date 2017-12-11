package com.bt.nextgen.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnvironmentTest
{
	@Test
	public void testBasic()
	{
		assertEquals(Environment.isDevelopment(), Environment.ENVIRONMENT.DEV.equals(Environment.environment()));
		assertEquals(Environment.isIntegration(),
			Environment.ENVIRONMENT.INTEGRATION.equals(Environment.environment()));
		assertEquals(Environment.isSit(), false);
		assertEquals(Environment.isUat(), false);
		assertEquals(Environment.isProduction(), false);
		assertEquals(Environment.notProduction(), !Environment.isProduction());
	}
}
