package com.bt.nextgen.core.web.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Import tag similar to the JSTL one but delegates to the spring resource loader so that it can read 
 * resources from the classpath as well as standard urls.   
 */
public class ImportTag extends SimpleTagSupport
{
	private String resourceLocation;

	public void setResource(String resourceLocation) throws JspTagException
	{
		this.resourceLocation = resourceLocation;
	}

	public void doTag() throws JspException
	{
		try
		{
			HttpServletRequest httpServletRequest = ((HttpServletRequest)((PageContext)getJspContext()).getRequest());
			JspWriter out = getJspContext().getOut();

			httpServletRequest.getContextPath();
			out.print(acquireContentAsString());
		}
		catch (IOException ex)
		{
			throw new JspTagException(ex.toString(), ex);
		}
	}

	private String acquireContentAsString() throws IOException
	{
		ApplicationContext context = getAppContext();

		Resource resource = context.getResource(resourceLocation);

		StringWriter writer = new StringWriter();
		IOUtils.copy(resource.getInputStream(), writer, Charsets.UTF_8);
		return writer.toString();
	}

	private ApplicationContext getAppContext()
	{
		return WebApplicationContextUtils.getWebApplicationContext(((PageContext)getJspContext()).getServletContext());
	}

}