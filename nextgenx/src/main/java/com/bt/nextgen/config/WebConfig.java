package com.bt.nextgen.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;

@Configuration
@EnableCaching
@EnableWebMvc
@ComponentScan(basePackages =
{
	"com.bt.nextgen","com.bt.panorama.direct","com.btfin.panorama"
})
public class WebConfig extends AbstractWebConfig
{
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

	@Bean(name = "integrationValidator")
	public LocalValidatorFactoryBean integrationValidatorFactoryBean()
	{
		return new LocalValidatorFactoryBean();
	}

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public org.apache.commons.configuration.Configuration configurationBuilderBean()
    {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        builder.setFile(new File("config.xml"));
        org.apache.commons.configuration.Configuration config = null;
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            logger.info("Error in configurationBuilderBean",e);
        }
        return config;
    }

    //TODO - Check this for duplication (this is imported from cash and may be incorrect)
    @Bean
    public RuntimeConfiguration runtimeSettings()
    {
        return new RuntimeConfiguration();
    }
}