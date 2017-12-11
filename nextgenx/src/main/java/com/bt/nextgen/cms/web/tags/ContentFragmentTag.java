package com.bt.nextgen.cms.web.tags;

import com.bt.nextgen.cms.service.CmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import javax.servlet.jsp.PageContext;
import java.io.IOException;

import static org.springframework.util.StringUtils.hasLength;

/**
 * A simple JSP tag that fetches and renders a specified item of content.
 * Content is fetched from the configured <code>ContentSource</code>for this web
 * application.
 *
 * @author Andrew Bird
 */
@SuppressWarnings("serial")
public final class ContentFragmentTag extends RequestContextAwareTag
{

	/**
	 * Default error message mark-up.
	 */
	private static final String DEF_ERR_MSG = "<!-- Unable to write content -->";

	private static final Logger logger = LoggerFactory.getLogger(ContentFragmentTag.class);

	/**
	 * Default bean ID under which we expect to find the
	 * <code>ContentSource</code> service bean.
	 */
	public static final String CNT_SRC = "contentSource";

	/**
	 * Name of the content fragment to include.
	 */
	private String name = null;

	/**
	 * Name of the variable under which to store the rendered content.
	 */
	private String var = null;

	/**
	 * Scope of the variable to set (if used).
	 */
	private int scope = PageContext.PAGE_SCOPE;

	/**
	 * Error message to be displayed if something goes wrong.
	 */
	private String errorMessage = DEF_ERR_MSG;

	/**
	 * Set the name of the content fragment to be rendered.
	 *
	 * @param name the name of the fragment.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Get the name of the content fragment to be rendered.
	 *
	 * @return name the name of the fragment.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Specify the JSP variable in which to store the rendered content.
	 *
	 * @param var the name of the scoped variable in which to store the rendered
	 *            content.
	 */
	public void setVar(String var)
	{
		this.var = var;
	}

	/**
	 * Get the value of the var attribute.
	 *
	 * @return the value of the var attribute.
	 */
	public String getVar()
	{
		return var;
	}

	/**
	 * Set the scope of the named variable.
	 *
	 * @param scope a string describing the scope. Should be one of:
	 *              <ul>
	 *              <li>page [default]</li>
	 *              <li>request</li>
	 *              <li>session</li>
	 *              <li>application</li>
	 *              </ul>
	 */
	public void setScope(String scope)
	{
		if ("application".equals(scope))
		{
			this.scope = PageContext.APPLICATION_SCOPE;
		}
		else if ("session".equals(scope))
		{
			this.scope = PageContext.SESSION_SCOPE;
		}
		else if ("request".equals(scope))
		{
			this.scope = PageContext.REQUEST_SCOPE;
		}
		else
		{ // default to page scope
			this.scope = PageContext.PAGE_SCOPE;
		}
	}

	/**
	 * Set the custom error message.
	 *
	 * @param errorMessage the custom error message to be displayed.
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	/**
	 * Get the custom error message.
	 *
	 * @return the custom error message to be displayed.
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * Set the description. This does absolutely nothing to the tag, it is
	 * merely present as an attribute to help developers optionally describe the
	 * content fragment they are including.
	 *
	 * @param ignored not actually used.
	 */
	public void setDescription(String ignored)
	{
	}

	/**
	 * Writes the specified content out to the current page, or stores it in the
	 * page context variables as appropriate.
	 *
	 * @param content the content to be written or stored.
	 * @throws IOException low-level I/O error.
	 */
	private void setVariableOrWriteContent(String content) throws IOException
	{
		if (!hasLength(var))
		{
			pageContext.getOut().write(content);
		}
		else
		{
			pageContext.setAttribute(var, content, scope);
		}
	}

	/**
	 * Default implementation of internal doStartTag.
	 *
	 * @return <code>EVAL_BODY_INCLUDE</code>, because we can now optionally
	 *         support a child <code>ErrorMessageTag</code>.
	 */
	@Override
	protected int doStartTagInternal()
	{
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * Render the content, to either the current page or a named variable.
	 *
	 * @return <code>EVAL_PAGE</code>
	 */
	@Override
	public int doEndTag()
	{
		String actualName = "";
		try
		{
			RequestContext rc = getRequestContext();
			BeanFactory bf = rc.getWebApplicationContext();

			// couldn't find any feature fragment so use the generic fragment,
			// please.
			if (!StringUtils.hasText(actualName))
			{
				actualName = name;
			}
			CmsService cmsService = bf.getBean(CmsService.class);

			String content = cmsService.getContent(name);
			setVariableOrWriteContent(content);
		}
		catch (Exception e)
		{
			logger.warn("Error rendering content: " + name, e);
			try
			{
				setVariableOrWriteContent(errorMessage);
			}
			catch (IOException ioe)
			{
				logger.error("Error rendering error message!", ioe);
			}
		}
		return EVAL_PAGE;
	}

	/**
	 * Override <code>doFinally</code> to set tag attributes back to default
	 * values.
	 */
	@Override
	public void doFinally()
	{
		super.doFinally();
		name = var = null;
		scope = PageContext.PAGE_SCOPE;
		errorMessage = DEF_ERR_MSG;

	}

}
