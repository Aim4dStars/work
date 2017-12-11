package com.bt.nextgen.core.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

public class PublicRequestMatcherTest
{
	@Test
	public void testLogout() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/public/doLogout");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertFalse(macther.matches(mockRequest));
	}

	@Test
	public void testLogoutInAnycase() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn(
				"/public/doLogout/logging?Error");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertFalse(macther.matches(mockRequest));
	}

	@Test
	public void testMixBadUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath())
				.thenReturn("/secure/public/doLogout");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertFalse(macther.matches(mockRequest));
	}

	@Test
	public void testPublicUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/public/page");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertTrue(macther.matches(mockRequest));
	}
	
	@Test
	public void testJustPublicUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/public/");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertTrue(macther.matches(mockRequest));
	}

	@Test
	public void testLoginUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn(
				"/public/page/logon?TAM_OP=auth_failure");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertTrue(macther.matches(mockRequest));
	}

	@Test
	public void testPublicLongUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/public/page/abc");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertTrue(macther.matches(mockRequest));
	}

	@Test
	public void testSecureUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/secure/doLogout");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertFalse(macther.matches(mockRequest));
	}
	
	@Test
	public void testJustSecureUrl() throws Exception
	{
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getServletPath()).thenReturn("/secure/");

		PublicRequestMatcher macther = new PublicRequestMatcher();
		assertFalse(macther.matches(mockRequest));
	}
}
