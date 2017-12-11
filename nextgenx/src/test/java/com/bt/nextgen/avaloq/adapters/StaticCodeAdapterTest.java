package com.bt.nextgen.avaloq.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.avaloq.abs.screen_rep.hira.web_ui_lov_export.Code;
import com.bt.nextgen.test.JaxbUnmarshallTestUtil;

public class StaticCodeAdapterTest
{

	private String codeXml = "<code><code_head_list>\n"
		+ "      <code_head id=\"1486\" row_type=\"data\" hira=\"2\" level=\"code.head\">\n" + "        <annot>\n"
		+ "          <ctx_type>stord_type</ctx_type><ctx_id>65</ctx_id>\n" + "        </annot>\n"
		+ "            <code_id><val>65</val></code_id>\n" + "            <intl_id><val>weekly</val></intl_id>\n"
		+ "            <user_id><val>WEEKLY</val></user_id>\n" + "            <name><val>Weekly</val></name>\n"
		+ "      </code_head>\n" + "    </code_head_list></code>";
	private Code jaxbCode;

	@Before
	public void setUpJaxbObject() throws Exception
	{
		jaxbCode = JaxbUnmarshallTestUtil.unmarshallSimpleObject(codeXml, Code.class);
	}

	@Test
	public void testNullPointerException() throws Exception
	{
		StaticCodeAdapter underTest = new StaticCodeAdapter(null);
		assertNull(underTest.getId());
		assertNull(underTest.getName());
		assertNull(underTest.getValue());
	}

	@Test
	public void testJaxbBaseObjectMapping()
	{
		StaticCodeAdapter underTest = new StaticCodeAdapter(jaxbCode);
		assertEquals("65", underTest.getId());
		assertEquals("weekly", underTest.getValue());
		assertEquals("Weekly", underTest.getName());
	}

	@Test
	public void testOverridingName()
	{
		StaticCodeAdapter underTest = new StaticCodeAdapter(jaxbCode);
		assertEquals("Weekly", underTest.getName());
		underTest.setName("My userfriendly name");
		assertEquals("My userfriendly name", underTest.getName());
	}

}
