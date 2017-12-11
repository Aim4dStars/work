package com.bt.nextgen.web;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.core.web.interceptor.DeviceBasedViewResolver;

public class DeviceBasedViewResolverTest
{
	private DeviceBasedViewResolver dbvr;
	private Device mockDevice;

	@Before
	public void setup()
	{
		DeviceResolver mockResolver = mock(DeviceResolver.class);
		mockDevice = mock(Device.class);
		when(mockResolver.resolveDevice(any(HttpServletRequest.class))).thenReturn(mockDevice);
		dbvr = new DeviceBasedViewResolver(mockResolver);
	}

	@Test
	public void testNormal_nameNotChanged() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mockDevice.isNormal()).thenReturn(true);
		when(mockDevice.isMobile()).thenReturn(false);
		when(mockDevice.isTablet()).thenReturn(false);
		when(mav.getViewName()).thenReturn("normal");

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, mav);

		verify(mav).setViewName(argThat(org.hamcrest.core.IsEqual.equalTo("normal")));
	}

	@Test
	public void testMobile_nameChanged() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mockDevice.isNormal()).thenReturn(false);
		when(mockDevice.isMobile()).thenReturn(true);
		when(mockDevice.isTablet()).thenReturn(false);
		when(mav.getViewName()).thenReturn("normal");

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, mav);

		verify(mav).setViewName(argThat(org.hamcrest.Matchers.endsWith("tablet")));
	}

	@Test
	public void testTable_nameChanged() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mockDevice.isNormal()).thenReturn(false);
		when(mockDevice.isMobile()).thenReturn(false);
		when(mockDevice.isTablet()).thenReturn(true);
		when(mav.getViewName()).thenReturn("normal");

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, mav);

		verify(mav).setViewName(argThat(org.hamcrest.Matchers.endsWith("tablet")));

	}

	@Test
	public void testForward_nameNotChanged() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mav.getViewName()).thenReturn("forward:normal");

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, mav);

		verify(mav, never()).setViewName(anyString());
	}

	@Test
	public void testRedirect_nameNotChanged() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mav.getViewName()).thenReturn("redirect:normal");

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, mav);

		verify(mav, never()).setViewName(anyString());
	}

	@Test
	public void testNullModelSupported() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mav.getViewName()).thenReturn(null);

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, null);

		verify(mav, never()).setViewName(anyString());
	}

	@Test
	public void testNullViewSupported() throws Exception
	{
		ModelAndView mav = mock(ModelAndView.class);
		when(mav.getViewName()).thenReturn(null);

		dbvr.postHandle(mock(HttpServletRequest.class), mock(HttpServletResponse.class), null, mav);

		verify(mav, never()).setViewName(anyString());
	}
}
