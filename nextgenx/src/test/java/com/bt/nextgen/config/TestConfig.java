package com.bt.nextgen.config;

import com.bt.nextgen.core.web.controller.AdminController;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import java.io.File;

@Configuration
@ComponentScan(basePackages =
{
	"com.bt.nextgen"
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
{
	WebConfig.class, SecurityConfig.class, AdminController.class
}))
@PropertySource(value =
{
	"classpath:/version-app.properties", "classpath:/common.properties", "classpath:/env.properties",
    "classpath:/avaloq.test.properties"
})
public class TestConfig
{
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);

	@Bean(name = "mvcValidator")
	LocalValidatorFactoryBean localValidatorFactoryBean()
	{
		return new LocalValidatorFactoryBean();
	}

	@Bean(name = "setupAdvice")
	LocalValidatorFactoryBean localValidatorFactoryBeanDefinition()
	{
		RootBeanDefinition validatorDef = new RootBeanDefinition("org.springframework.validation.beanvalidation.LocalValidatorFactoryBean");
		validatorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return new LocalValidatorFactoryBean();
	}

	@Bean(name = "integrationValidator")
	LocalValidatorFactoryBean integrationValidatorFactoryBean()
	{
		return new LocalValidatorFactoryBean();
	}

    @Bean
    public org.apache.commons.configuration.Configuration configurationBuilderBean() throws Exception
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

    @Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

    @Bean
    public ServletContext mockServletContext() {
        return new MockServletContext();
    }

    @Bean
    public HttpSession MockHttpSession() {
        return new MockHttpSession();
    }

}
