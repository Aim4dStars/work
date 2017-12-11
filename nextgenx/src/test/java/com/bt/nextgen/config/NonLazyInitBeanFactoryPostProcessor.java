package com.bt.nextgen.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class NonLazyInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor
{
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
	{
		for (String beanName : beanFactory.getBeanDefinitionNames())
		{
			beanFactory.getBeanDefinition(beanName).setLazyInit(true);
		}
	}
}