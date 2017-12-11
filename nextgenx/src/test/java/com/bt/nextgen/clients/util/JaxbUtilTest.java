package com.bt.nextgen.clients.util;

import org.junit.Test;

public class JaxbUtilTest
{
	@Test(expected = RuntimeException.class)
	public void testUnmarshall_invalidResourceThrows() throws Exception
	{
		JaxbUtil.unmarshall("Doesn't exist", String.class);
	}

}
