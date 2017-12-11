package com.bt.nextgen.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PropertiesTest
{
	@Test
	public void testBasic()
	{
		assertThat(com.bt.nextgen.core.util.Properties.get("environment"), is(Environment.environment().toString()));
	}

	@Test
	public void testNull()
	{
		assertNull(com.bt.nextgen.core.util.Properties.get("$@#%*$%*@#%$*@$"));
	}
}
