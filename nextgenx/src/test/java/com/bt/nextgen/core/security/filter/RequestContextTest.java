package com.bt.nextgen.core.security.filter;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_UNAUTHED_USERNAME;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_USERNAME_PARAM;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class RequestContextTest
{

	@Test
	public void testParsingUsername()
	{
		final String username = "username_role_avaloqId";
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameter(SECURITY_USERNAME_PARAM.value())).thenReturn(username);
		RequestContext context = new RequestContext(request);

		assertThat(context.username, Is.is(username));
		assertThat(context.usernameParts[0], Is.is("username"));
		assertThat(context.usernameParts[1], Is.is("role"));
		assertThat(context.usernameParts[2], Is.is("avaloqId"));
		assertThat(context.getRole(), Is.is("role"));
	}

	@Test
	public void testParsingUsernameUnauthenticated()
	{
		SecurityContextHolder.clearContext();
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		RequestContext context = new RequestContext(request);

		assertThat(context.username, Is.is(SECURITY_UNAUTHED_USERNAME.value()));
	}

	@Test
	public void testParsingUsername_reuseSecurityContext()
	{
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		SecurityContext secContext = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(secContext);

		when(secContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(new User("username", "password",
			Collections.EMPTY_LIST), ""));

		RequestContext context = new RequestContext(request);

		assertThat(context.username, Is.is("username"));
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testParsingPartialUsername()
	{
		final String username = "username_role";
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameter(SECURITY_USERNAME_PARAM.value())).thenReturn(username);
		RequestContext context = new RequestContext(request);

		assertThat(context.username, Is.is(username));
		assertThat(context.usernameParts[0], Is.is("username"));
		assertThat(context.usernameParts[1], Is.is("role"));
		assertThat(context.usernameParts[2], Is.is("username"));
		assertThat(context.getRole(), Is.is("role"));
	}

	@Test
	public void testParsingSmallUsername()
	{
		final String username = "username";
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getParameter(SECURITY_USERNAME_PARAM.value())).thenReturn(username);
		RequestContext context = new RequestContext(request);

		assertThat(context.username, Is.is(username));
		assertThat(context.usernameParts[0], Is.is("username"));
		assertThat(context.usernameParts[1], Is.is("username"));
		assertThat(context.usernameParts[2], Is.is("username"));
		assertThat(context.getRole(), Is.is("username"));
	}
}
