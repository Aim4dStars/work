package com.bt.nextgen.core.web.taglib;

import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

// @Todo Turn this into an iframe solution
public class DisplayContentTag extends SimpleTagSupport
{
	private String contentId = null;
	private String styleClass = null;
	private String style = null;

	public void doTag() throws JspException, IOException
	{
		JspWriter out = getJspContext().getOut();

		out.write("<div " + (StringUtils.isNotEmpty(
			styleClass) ? "class=\"" + styleClass + "\" " : "") + (StringUtils.isNotEmpty(
			style) ? "style=\"" + style + "\"" : "") + ">Content goes here<br/><span style=\"font-size:10px\">[id: " + contentId + "]</span></div>");
	}

	public void setContentId(String contentId)
	{
		this.contentId = contentId;
	}

	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}
}