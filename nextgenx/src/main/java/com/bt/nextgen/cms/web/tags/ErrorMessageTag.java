package com.bt.nextgen.cms.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * Custom body tag for specifying a custom error message. Currently only used
 * by the <code>ContentFragmentTag</code>, but could potentially be re-factored for
 * more general usage if/when required.  
 * @author Andrew Bird
 */
@SuppressWarnings("serial")
public final class ErrorMessageTag extends BodyTagSupport
{

	/**
	 * Called after the body content has been set. At this point, we can
	 * call on our parent tag to set the error message.
	 * @return SKIP_BODY 
	 */
	@Override
	public int doAfterBody() throws JspException
	{
		Tag parent = getParent();
		if (parent instanceof ContentFragmentTag)
		{
			String errorMessage = getBodyContent().getString();
			((ContentFragmentTag)parent).setErrorMessage(errorMessage);
			return super.doAfterBody();
		}
		else if (parent == null)
		{
			throw new JspException("Null parent tag");
		}
		else
		{
			throw new JspException("Invalid class for parent tag: " + parent.getClass().getName());
		}
	}
}
