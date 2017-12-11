package com.bt.nextgen.core.web.taglib;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import static org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext;

/**
 * JSP tag that will show or hide its enclosed content based on whether or not a named feature has been enabled
 * for the current version of the application.
 */
public class FeatureToggleTag extends BodyTagSupport {

	/** Default behaviour - show the content if the feature is enabled. */
	static final String SHOW = "show";

	/** Hide behaviour - hide the content if the feature is enabled. */
	static final String HIDE = "hide";

	/** The name of the feature toggle that determines whether or not this tag's body gets rendered. */
	private String feature;

	/** Whether to show or hide the content if the feature is active (default is false, meaning content displays on an active toggle). */
	private boolean hide = false;

	/** The current set of features for this application. */
	private FeatureToggles featureToggles = null;

	/**
	 * Set the name of the feature that will determine whether or not the body contents are to be displayed.
	 * @param feature
	 */
	public void setFeature(String feature) {
		this.feature = feature;
	}

	public void setBehaviour(String behaviour) {
		this.hide = HIDE.equalsIgnoreCase(behaviour);
	}

	private FeatureToggles getFeatureToggleDto() {
		if (featureToggles == null) {
			final BeanFactory beans = getWebApplicationContext(pageContext.getServletContext());
			final FeatureTogglesService service = beans.getBean(FeatureTogglesService.class);
			featureToggles = service.findOne(new FailFastErrorsImpl());
		}
		return featureToggles;
	}

	/**
	 * Start tag.
	 * @return whether to include the body, or skip it.
	 */
	@Override
	public int doStartTag() {
		boolean toggle = getFeatureToggleDto().getFeatureToggle(feature);
		return hide ^ toggle ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		// Reset default attribute values
		this.feature = null;
		this.hide = false;
		return super.doEndTag();
	}
}
