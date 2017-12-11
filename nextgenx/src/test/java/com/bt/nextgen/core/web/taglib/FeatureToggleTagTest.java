package com.bt.nextgen.core.web.taglib;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import static com.bt.nextgen.core.web.taglib.FeatureToggleTag.HIDE;
import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

@RunWith(MockitoJUnitRunner.class)
public class FeatureToggleTagTest {

	@Mock
	private PageContext pageContext;

	@Mock
	private ServletContext servletContext;

	@Mock
	private WebApplicationContext webApplicationContext;

	@Mock
	private FeatureTogglesService featureTogglesService;

	private FeatureToggleTag tag;

	@Before
	public void initTag() {
		tag = new FeatureToggleTag();
		tag.setPageContext(pageContext);
		tag.setFeature("testFeature");
	}

	@Test
	public void doStartTagWithFeatureOn() {
		expectGetFeatureToggleDto(featureToggleDto("testFeature", true));
		assertEquals(Tag.EVAL_BODY_INCLUDE, tag.doStartTag());
		tag.setBehaviour(HIDE);
		assertEquals(Tag.SKIP_BODY, tag.doStartTag());
	}

	@Test
	public void doStartTagWithFeatureAbsent() {
		expectGetFeatureToggleDto(new FeatureToggles());
		assertEquals(Tag.SKIP_BODY, tag.doStartTag());
		tag.setBehaviour(HIDE);
		assertEquals(Tag.EVAL_BODY_INCLUDE, tag.doStartTag());
	}

	@Test
	public void doStartTagWithFeatureOff() {
		expectGetFeatureToggleDto(featureToggleDto("testFeature", false));
		assertEquals(Tag.SKIP_BODY, tag.doStartTag());
		tag.setBehaviour(HIDE);
		assertEquals(Tag.EVAL_BODY_INCLUDE, tag.doStartTag());
	}

	private void expectGetFeatureToggleDto(FeatureToggles toggles) {
		when(pageContext.getServletContext()).thenReturn(servletContext);
		when(servletContext.getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(webApplicationContext);
		when(webApplicationContext.getBean(FeatureTogglesService.class)).thenReturn(featureTogglesService);
		when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(toggles);
	}

	private static FeatureToggles featureToggleDto(String feature, boolean toggle) {
		final FeatureToggles toggles = new FeatureToggles();
		toggles.setFeatureToggle(feature, toggle);
		return toggles;
	}
}
