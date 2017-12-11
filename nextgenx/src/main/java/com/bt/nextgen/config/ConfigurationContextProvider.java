package com.bt.nextgen.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationContextProvider implements ApplicationContextAware
{

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    //SONAR Issues: Dodgy - Write to static field from instance method,Throws declarations should not be redundant
    @SuppressWarnings({"findbugs:ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD","squid:RedundantThrowsDeclarationCheck"})
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurationContextProvider.applicationContext = applicationContext;
    }
}