package com.bt.nextgen.web.controller;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.util.View;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerTest
{
	@InjectMocks
	private ErrorHandler errorHandler;

	@Mock
	private UserProfileService mockProfileService;

	private Profile mockProfile;

	@Before
	public void setup()
	{
		mockProfile = mock(Profile.class);
		when(mockProfileService.getEffectiveProfile()).thenReturn(mockProfile);
	}

	@Test
	public void testHandle403_Returns_error404() throws Exception
	{
		when(mockProfileService.isLoggedIn()).thenReturn(false);
		assertThat(errorHandler.handle("403"), Is.is("redirect:/public/page/logon"));

		when(mockProfileService.isLoggedIn()).thenReturn(true);
		assertThat(errorHandler.handle("403"), Is.is(View.ERROR_404));

	}

	@Test
	public void testHandle404_Returns_error404() throws Exception
	{
		when(mockProfileService.isLoggedIn()).thenReturn(false);
		assertThat(errorHandler.handle("404"), Is.is("redirect:/public/page/logon"));

		when(mockProfileService.isLoggedIn()).thenReturn(true);
		assertThat(errorHandler.handle("404"), Is.is(View.ERROR_404));

	}

	@Test
	public void testHandle500_Returns_error500() throws Exception
	{
		when(mockProfileService.isLoggedIn()).thenReturn(false);
		assertThat(errorHandler.handle("500"), Is.is("redirect:/public/static/page/maintenance.html"));

		when(mockProfileService.isLoggedIn()).thenReturn(true);
		assertThat(errorHandler.handle("500"), Is.is(View.ERROR_500));
	}
}
