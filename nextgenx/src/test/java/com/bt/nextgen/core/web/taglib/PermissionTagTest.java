package com.bt.nextgen.core.web.taglib;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.bt.nextgen.cms.web.tags.AbstractTagTest;

public class PermissionTagTest extends AbstractTagTest
{
	private PermissionTag permissionTag = new PermissionTag();
	private PermissionEvaluator permissionEvaluator;
	private Authentication mockAuthentication;

	@Before
	public void initTag() throws IOException
	{
		mockHttpServletRequest.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

		Resource resource = mock(Resource.class);
		when(wac.getServletContext()).thenReturn(mockServletContext);
		mockPageContext.setAttribute("javax.servlet.jsp.jspRequest", mockHttpServletRequest);
		permissionTag.setPageContext(mockPageContext);
		mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

		SecurityContext mockSecurityContext = Mockito.mock(SecurityContext.class);
		mockAuthentication = Mockito.mock(Authentication.class);
		Mockito.when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
		SecurityContextHolder.setContext(mockSecurityContext);
		List <SimpleGrantedAuthority> authorityList = new ArrayList <SimpleGrantedAuthority>();
		authorityList.add(new SimpleGrantedAuthority("Adviser"));
		Mockito.<Collection <? extends GrantedAuthority>> when(mockAuthentication.getAuthorities()).thenReturn(authorityList);
		permissionEvaluator = mock(PermissionEvaluator.class);
		ReflectionTestUtils.setField(permissionTag, "permissionEvaluator", permissionEvaluator);
	}

	@Test
	public void testDoStartTag() throws Exception
	{
		ReflectionTestUtils.setField(permissionTag, "applicationContext", wac);
		permissionTag.setTargetId("portfolioId");
		permissionTag.setTargetType("MOVEMONEY");
		permissionTag.setAccess("CHANGE_LIMIT");
		when(permissionEvaluator.hasPermission(mockAuthentication, "portfolioId", "MOVEMONEY", "CHANGE_LIMIT")).thenReturn(true);
		int responseCode = permissionTag.doStartTag();
		Assert.assertThat(responseCode, is(equalTo(1)));
	}

	@Test
	public void testDoStartTag_withNullAccess() throws Exception
	{
		permissionTag.setTargetId("portfolioId");
		permissionTag.setTargetType("MOVEMONEY");
		int responseCode = permissionTag.doStartTag();
		Assert.assertThat(responseCode, is(equalTo(0)));
	}
	
	@After
	public void tearDown()
	{
		SecurityContextHolder.clearContext();
	}
	
}
