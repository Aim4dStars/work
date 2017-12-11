package com.bt.nextgen.core.web.taglib;

import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.service.MessageServiceImpl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class ErrorTag extends SimpleTagSupport
{
	private String errorId = null;
	private MessageService msgService = new MessageServiceImpl();

	public void doTag() throws JspException, IOException
	{
		JspWriter out = getJspContext().getOut();
		out.write(msgService.lookup(errorId));
	}

	public String getErrorId()
	{
		return errorId;
	}

	public void setErrorId(String errorId)
	{
		this.errorId = errorId;
	}
}