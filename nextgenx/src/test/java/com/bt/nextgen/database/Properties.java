package com.bt.nextgen.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;

public class Properties
{
    private static java.util.Properties properties;
    private static final Logger logger = LoggerFactory.getLogger(Properties.class);

    static
    {
        boot();
    }

    private static void boot()
    {
        try
        {
            Resource resource = new ClassPathResource("/database.test.properties");
            properties = PropertiesLoaderUtils.loadProperties(resource);
            properties.putAll(System.getProperties());
            properties.putAll(com.bt.nextgen.core.util.Properties.all());
        }
        catch (IOException e)
        {
            logger.error("Exception caught while loading properties", e);
        }
    }

    public static final String getString(String path)
    {
        return get(path);
    }

    public static final String get(String path)
    {
        return properties.getProperty(path);
    }

    public static final String getOracleUserName() {
        return get("oracle.db.username");
    }

    public static final String getOracleUrl() {
        return get("oracle.db.url");
    }

    public static final String getOraclePassword() {
        return get("oracle.db.password");
    }

    public static java.util.Properties all() {
        return properties;
    }
}
