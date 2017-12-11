package com.bt.nextgen.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.bt.nextgen.core.util.PasswordUtil;

public class PasswordUtilTest
{
	@Test
	public void testBasic() throws Exception
	{
		new PasswordUtil();
		assertThat(PasswordUtil.createPassword("today123"), is("91704f9225fbb26356b6f8c26672fc856c2ed188"));
	}
}
