package com.bt.nextgen.core.web.taglib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.taglibs.TagLibConfig;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.ExpressionEvaluationUtils;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import java.io.Serializable;
import java.util.*;

public class PermissionTag extends TagSupport {
    //~ Static fields/initializers =====================================================================================

    protected static final Log logger = LogFactory.getLog(PermissionTag.class);

    //~ Instance fields ================================================================================================

    private ApplicationContext applicationContext;
    private String targetId;
	private String targetType;
    private PermissionEvaluator permissionEvaluator;
    private String access = "";
    private String var;
    
    //~ Methods ========================================================================================================

    public int doStartTag() throws JspException {
        if ((null == access) || "".equals(access)) {
            return skipBody();
        }

        initializeIfRequired();

        final String evaledPermissionsString = ExpressionEvaluationUtils.evaluateString("access", access, pageContext);

//        Object resolvedDomainObject;
//
//        if (targetType instanceof String) {
//            resolvedDomainObject = ExpressionEvaluationUtils.evaluate("domainObject", (String) targetType,
//                    Object.class, pageContext);
//        } else {
//            resolvedDomainObject = targetType;
//        }
//
//        if (resolvedDomainObject == null) {
//            if (logger.isDebugEnabled()) {
//                logger.debug("domainObject resolved to null, so including tag body");
//            }
//
//            // Of course they have access to a null object!
//            return evalBody();
//        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "SecurityContextHolder did not return a non-null Authentication object, so skipping tag body");
            }

            return skipBody();
        }

        if (permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
        		targetId, targetType, evaledPermissionsString)) {
            return evalBody();
        }

        return skipBody();
    }

    private int skipBody() {
        if (var != null) {
            pageContext.setAttribute(var, Boolean.FALSE, PageContext.PAGE_SCOPE);
        }
        return TagLibConfig.evalOrSkip(false);
    }

    private int evalBody() {
        if (var != null) {
            pageContext.setAttribute(var, Boolean.TRUE, PageContext.PAGE_SCOPE);
        }
        return TagLibConfig.evalOrSkip(true);
    }


    /**
     * Allows test cases to override where application context obtained from.
     *
     * @param pageContext so the <code>ServletContext</code> can be accessed as required by Spring's
     *        <code>WebApplicationContextUtils</code>
     *
     * @return the Spring application context (never <code>null</code>)
     */
    protected ApplicationContext getContext(PageContext pageContext) {
        ServletContext servletContext = pageContext.getServletContext();

        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    public String getTargetType() {
        return targetType;
    }

    public String getAccess() {
        return access;
    }

    private void initializeIfRequired() throws JspException {
        if (applicationContext != null) {
            return;
        }

        this.applicationContext = getContext(pageContext);

        permissionEvaluator = getBeanOfType(PermissionEvaluator.class);
    }

    private <T> T getBeanOfType(Class<T> type) throws JspException {
        Map<String, T> map = applicationContext.getBeansOfType(type);

        for (ApplicationContext context = applicationContext.getParent();
            context != null; context = context.getParent()) {
            map.putAll(context.getBeansOfType(type));
        }

        if (map.size() == 0) {
            return null;
        } else if (map.size() == 1) {
            return map.values().iterator().next();
        }

        throw new JspException("Found incorrect number of " + type.getSimpleName() +" instances in "
                    + "application context - you must have only have one!");
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getTargetId()
	{
		return targetId;
	}

	public void setTargetId(String targetId)
	{
		this.targetId = targetId;
	}

    public void setAccess(String access) {
        this.access = access;
    }

    public void setVar(String var) {
        this.var = var;
    }
}
