package com.bt.nextgen.util.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmlUtilTest
{
	private static final String xml = "<TestResponse xmlns=\"ns://btfin.com/nextgen/services/schemas\"><Context><Status>SUCCESS</Status></Context></TestResponse>";

	@Test(expected = IllegalArgumentException.class)
	public void testException()
	{
		String string = null;
		com.bt.nextgen.core.xml.XmlUtil.parseDocument(string);
	}

	@Test(expected = RuntimeException.class)
	public void testRuntimeException()
	{
		com.bt.nextgen.core.xml.XmlUtil.parseDocument("<blah>rubbish");
	}

	@Test
	public void testCompactPrint()
	{
		assertEquals(com.bt.nextgen.core.xml.XmlUtil.compactPrint(com.bt.nextgen.core.xml.XmlUtil.parseDocument(xml)),
			"<TestResponse xmlns=\"ns://btfin.com/nextgen/services/schemas\"><Context><Status>SUCCESS</Status></Context></TestResponse>");
	}

	@Test
	public void testPrettyPrint()
	{
		assertEquals(com.bt.nextgen.core.xml.XmlUtil.prettyPrint(com.bt.nextgen.core.xml.XmlUtil.parseDocument(xml)),
			"<TestResponse xmlns=\"ns://btfin.com/nextgen/services/schemas\">\r\n" + "  <Context>\r\n" + "    <Status>SUCCESS</Status>\r\n" + "  </Context>\r\n" + "</TestResponse>");

		assertEquals(com.bt.nextgen.core.xml.XmlUtil.prettyPrint(
			com.bt.nextgen.core.xml.XmlUtil.parseDocument(xml).getDocumentElement()),
			"<TestResponse xmlns=\"ns://btfin.com/nextgen/services/schemas\">\r\n" + "  <Context>\r\n" + "    <Status>SUCCESS</Status>\r\n" + "  </Context>\r\n" + "</TestResponse>");
	}
}
