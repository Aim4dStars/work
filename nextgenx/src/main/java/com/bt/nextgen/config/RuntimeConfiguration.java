package com.bt.nextgen.config;

import com.bt.nextgen.core.util.SETTINGS;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class will 'tweak' the spring context at runtime. Currently only needed to tweak some of the security settings
 */
public class RuntimeConfiguration
	implements BeanPostProcessor
{
	@Override public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
	{
		return bean;
	}

	@Override public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
	{
		setupUsernamePasswordFilter(bean);
		return bean;
	}

	private void setupUsernamePasswordFilter(Object bean)
	{
		if(bean.getClass().isAssignableFrom(UsernamePasswordAuthenticationFilter.class))
		{
			UsernamePasswordAuthenticationFilter filter = (UsernamePasswordAuthenticationFilter) bean;
			filter.setUsernameParameter(SETTINGS.SECURITY_USERNAME_PARAM.value());
			filter.setPasswordParameter(SETTINGS.SECURITY_PASSWORD_PARAM.value());
		}
	}
}
