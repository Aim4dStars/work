package com.bt.nextgen.core.web.taglib;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.bt.nextgen.cms.web.tags.AbstractTagTest;

public class HashUrlTagTest extends AbstractTagTest
{

	private HashUrlTag hashUrlTag;
	private static final String SOME_PATH = "/public/static/js/client/desktop/shared/addBiller.js";

	@Before
	public void initTag() throws IOException
	{
		mockHttpServletRequest.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		hashUrlTag = new HashUrlTag();
		mockHttpServletRequest.setLocalPort(8080);
		mockHttpServletRequest.setContextPath("/nextgen");
		Resource resource = mock(Resource.class);
		hashUrlTag.setSrc(SOME_PATH);
		ByteArrayInputStream inputStream = new ByteArrayInputStream("some text".getBytes());
		when(resource.getInputStream()).thenReturn(inputStream);
		when(wac.getResource(hashUrlTag.getSrc())).thenReturn(resource);
        when(wac.getServletContext()).thenReturn(mockServletContext);
        mockPageContext.setAttribute("javax.servlet.jsp.jspRequest", mockHttpServletRequest);

        hashUrlTag.setJspContext(mockPageContext);
        mockServletContext.setAttribute( WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
       
	}
	
	

	@Test
	public void testDoTag() throws JspException, IOException
	{
		hashUrlTag.doTag();
		String content = mockHttpServletResponse.getContentAsString();
		assertThat(content.contains("/js/client/desktop/shared/addBiller.js"), Is.is(true));
		assertThat(content.contains("/nextgen/public/static"), Is.is(true));
		assertThat(content.contains("N6pjx3OY2VRHMmLhoAV8HmMu2nc="), Is.is(true));
		
		hashUrlTag.doTag();
		content = mockHttpServletResponse.getContentAsString();
		assertThat(content.contains("/js/client/desktop/shared/addBiller.js"), Is.is(true));
		assertThat(content.contains("/nextgen/public/static"), Is.is(true));
		assertThat(content.contains("N6pjx3OY2VRHMmLhoAV8HmMu2nc="), Is.is(true));
		
	}
	
	@Test
	public void testDoTag_pdf()throws JspException, IOException{
		hashUrlTag.setPdf("some pdf");
		hashUrlTag.doTag();
		String content = mockHttpServletResponse.getContentAsString();
		assertThat(content.startsWith("http://localhost:8080/nextgen"), Is.is(true));
		assertThat(content.contains(SOME_PATH), Is.is(true));
	}

}
