package com.bt.nextgen.core.web.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeviceBasedViewResolver extends HandlerInterceptorAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(DeviceBasedViewResolver.class);
	private final DeviceResolver deviceResolver;
	private static final String REDIRECT = "redirect:";
	private static final String FORWARD = "forward:";

	public DeviceBasedViewResolver(DeviceResolver deviceResolver)
	{
		this.deviceResolver = deviceResolver;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception
	{
		if (modelAndView == null)
		{
			return;
		}

		String oldName = modelAndView.getViewName();

		if (oldName == null)
		{
			return;
		}

		if (startsWithKeyWord(oldName))
		{
			return;
		}

		Device device = deviceResolver.resolveDevice(request);
		String newViewName = resolveView(oldName, device);
		logger.debug("View name changed to: {} from: {} based on {}", new Object[] {
			newViewName, oldName, device.toString()
		});
		modelAndView.setViewName(newViewName);
	}

	private boolean startsWithKeyWord(String viewName)
	{
		return viewName.startsWith(FORWARD) || viewName.startsWith(REDIRECT);
	}

	private String resolveView(String baseName, Device deviceContext)
	{
		return baseName + (deviceContext.isNormal() ? "" : ".tablet");
	}
}
