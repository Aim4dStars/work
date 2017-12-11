package com.bt.nextgen.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bt.nextgen.core.util.DatabaseManager;
import com.bt.nextgen.core.util.Properties;

import static com.bt.nextgen.core.util.SETTINGS.SETTINGS_VERSION;

@Component
public class ApplicationContextProvider implements ApplicationContextAware
{
	private static ApplicationContext applicationContext = null;
	private final DatabaseManager databaseManager;

	private static final Logger logger = LoggerFactory.getLogger(ApplicationContextProvider.class);

	@Autowired
	public ApplicationContextProvider(DatabaseManager databaseManager)
	{
		this.databaseManager = databaseManager;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		ApplicationContextProvider.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	@PostConstruct
	public void init() throws IOException
	{
		logger.info("Panorama version information - <server:{}>, <settings:{}>",
			Properties.getString("nextgen.version"),
			SETTINGS_VERSION.value());

		databaseManager.run();

		try
		{
			DefaultBootstrap.bootstrap();
		}
		catch (ConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
