package com.bt.nextgen.cms.web.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.DispatcherServlet;

import com.bt.nextgen.cms.service.CmsService;

public class ContentFragmentTagTest extends AbstractTagTest
{

	private ContentFragmentTag tag;
	private CmsService service = mock(CmsService.class);

	@Before
	public void initTag()
	{
		mockHttpServletRequest.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		when(service.getContent("test")).thenReturn("<h1>Test content</h1>");
		expectExecuteEndTag(service);
		tag = new ContentFragmentTag();
		tag.setPageContext(mockPageContext);
		tag.setDescription("Purely for testing purposes");
	}

	private void assertStartAndEndTag() throws JspException
	{
		assertEquals(Tag.EVAL_BODY_INCLUDE, tag.doStartTag());
		assertEquals(Tag.EVAL_PAGE, tag.doEndTag());
	}

	@Test
	public void doFinallyClearsState() throws JspException
	{
		tag.setName("test");
		tag.setVar("var");
		assertStartAndEndTag();
		tag.doFinally();
		assertNull(tag.getName());
		assertNull(tag.getVar());
	}

	@Test
	public void doEndTagWithNoVar() throws Exception
	{
		tag.setName("test");
		assertStartAndEndTag();
		assertEquals("<h1>Test content</h1>", mockHttpServletResponse.getContentAsString());
	}


	@Test
	public void doEndTagWillWriteErrorMessageOnException() throws Exception
	{
		when(service.getContent("test")).thenThrow(new RuntimeException());
		tag.setName("test");
		tag.setErrorMessage("<p>error<p>");
		assertStartAndEndTag();
		assertEquals("<p>error<p>", mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void doEndTagWithDefaultScope() throws Exception
	{
		tag.setName("test");
		tag.setVar("test");
		assertStartAndEndTag();
		assertTrue(mockHttpServletResponse.getContentAsString().length() == 0);
		assertEquals("<h1>Test content</h1>", mockPageContext.getAttribute("test", PageContext.PAGE_SCOPE));
	}

	@Test
	public void doEndTagWithPageScope() throws Exception
	{
		tag.setName("test");
		tag.setVar("test");
		tag.setScope("page");
		assertStartAndEndTag();
		assertTrue(mockHttpServletResponse.getContentAsString().length() == 0);
		assertEquals("<h1>Test content</h1>", mockPageContext.getAttribute("test", PageContext.PAGE_SCOPE));
	}

	@Test
	public void doEndTagWithRequestScope() throws Exception
	{
		tag.setName("test");
		tag.setVar("test");
		tag.setScope("request");
		assertStartAndEndTag();
		assertTrue(mockHttpServletResponse.getContentAsString().length() == 0);
		assertEquals("<h1>Test content</h1>", mockPageContext.getAttribute("test", PageContext.REQUEST_SCOPE));
	}

	@Test
	public void doEndTagWithSessionScope() throws Exception
	{
		tag.setName("test");
		tag.setVar("test");
		tag.setScope("session");
		assertStartAndEndTag();
		assertTrue(mockHttpServletResponse.getContentAsString().length() == 0);
		assertEquals("<h1>Test content</h1>", mockPageContext.getAttribute("test", PageContext.SESSION_SCOPE));
	}

	@Test
	public void doEndTagWithApplicationScope() throws Exception
	{
		tag.setName("test");
		tag.setVar("test");
		tag.setScope("application");
		assertStartAndEndTag();
		assertTrue(mockHttpServletResponse.getContentAsString().length() == 0);
		assertEquals("<h1>Test content</h1>", mockPageContext.getAttribute("test", PageContext.APPLICATION_SCOPE));
	}

}
