package com.bt.nextgen.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;

public class IdentityTest
{

	@Test
	public void testBasic()
	{
		new com.bt.nextgen.core.util.Identity();
		assertThat(com.bt.nextgen.core.util.Identity.randomID(), not(containsString("-")));
	}

	@Test
	public void testReceiptNumbers()
	{
		assertThat(com.bt.nextgen.core.util.Identity.randomReceiptNumber().length(), is(10));
		assertThat(NumberUtils.isNumber(com.bt.nextgen.core.util.Identity.randomReceiptNumber().substring(0, 7)),
			is(true));
	}
}
