package com.bt.nextgen.config;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.DeviceWebArgumentResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.SimpleSpringPreparerFactory;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.interceptor.ValidationMessagesInterceptorAdapter;
import com.bt.nextgen.util.Environment;

import static com.bt.nextgen.core.util.SETTINGS.CMS_BASE_DIR;
import static com.bt.nextgen.core.util.SETTINGS.CONTENT_CMS_MAX_AGE;
import static com.bt.nextgen.core.util.SETTINGS.CONTENT_STATIC_MAX_AGE;
import static com.bt.nextgen.core.util.SETTINGS.MICROSITE_INVESTOR_CACHE_PERIOD;
import static com.bt.nextgen.core.util.SETTINGS.MICROSITE_INVESTOR_RESOURCE_LOCATION;
import static com.bt.nextgen.core.util.SETTINGS.WEBCLIENT_CACHE_PERIOD;
import static com.bt.nextgen.core.util.SETTINGS.WEBCLIENT_RESOURCE_LOCATION;

@EnableWebMvc
@PropertySource({
	"classpath:/version-app.properties", "classpath:/common.properties", "classpath:/env.properties"
})
@SuppressWarnings("squid:S1200")
public abstract class AbstractWebConfig extends WebMvcConfigurerAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractWebConfig.class);

	@Autowired
	@Qualifier("jsonObjectMapper")
	private ObjectMapper objectMapper;

	@Override
	public void configureMessageConverters(List <HttpMessageConverter <? >> converters)
	{
		converters.add(new StringHttpMessageConverter());
		converters.add(new ByteArrayHttpMessageConverter());
		converters.add(new ResourceHttpMessageConverter());
		converters.add(jsonMessageConverter());
		super.configureMessageConverters(converters);
	}

	@Bean
	public HttpMessageConverter <? > jsonMessageConverter()
	{
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		DeviceResolver resolver = new LiteDeviceResolver();
		registry.addInterceptor(new DeviceResolverHandlerInterceptor(resolver));

		WebContentInterceptor interceptor = new WebContentInterceptor();
		interceptor.setCacheSeconds(0);
		interceptor.setUseExpiresHeader(true);
		interceptor.setUseCacheControlHeader(true);
		interceptor.setUseCacheControlNoStore(true);
		registry.addInterceptor(interceptor);
		registry.addInterceptor(new ValidationMessagesInterceptorAdapter());
		// This will need to be reinstated when we need device specific views
		//		registry.addInterceptor(new DeviceBasedViewResolver(resolver));

	}

	@Override
	public void addArgumentResolvers(List <HandlerMethodArgumentResolver> argumentResolvers)
	{
		argumentResolvers.add(new ServletWebArgumentResolverAdapter(new DeviceWebArgumentResolver()));
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		final String cmsBase = CMS_BASE_DIR.value();
		registry.addResourceHandler("/public/static/**")
			.addResourceLocations("/public/static/")
			.setCachePeriod(CONTENT_STATIC_MAX_AGE.intValue());
		registry.addResourceHandler("/public/brand/**")
			.addResourceLocations(cmsBase + "/content/brand/")
			.setCachePeriod(CONTENT_CMS_MAX_AGE.intValue());

		if (Environment.notProduction())
		{
			logger.info("Not running in production environment, enabling api test pages...");
			registry.addResourceHandler("/public/test/**")
				.addResourceLocations("/public/test/")
				.setCachePeriod(CONTENT_STATIC_MAX_AGE.intValue());

			logger.info("Not running in production environment, enabling configuration publishing...");
			registry.addResourceHandler("/public/settings/**/*.xml",
				"/public/settings/**/*.sql",
				"/public/settings/**/*.properties")
				.addResourceLocations("/", "classpath:/")
				.setCachePeriod(CONTENT_STATIC_MAX_AGE.intValue());

            logger.info("Not running in production environment, enabling swagger api documentation resources...");
            registry.addResourceHandler("/public/static/documentation/**").addResourceLocations(
                    "classpath:/META-INF/resources/");
            registry.addResourceHandler("/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
		}

		registry.addResourceHandler("/public/content/**")
			.addResourceLocations(cmsBase + "/public/")
			.setCachePeriod(CONTENT_CMS_MAX_AGE.intValue());

		registry.addResourceHandler("/secure/app/**")
				.addResourceLocations(WEBCLIENT_RESOURCE_LOCATION.value())
				.setCachePeriod(WEBCLIENT_CACHE_PERIOD.intValue());

		registry.addResourceHandler("/onboard/app/**")
			.addResourceLocations(WEBCLIENT_RESOURCE_LOCATION.value())
			.setCachePeriod(WEBCLIENT_CACHE_PERIOD.intValue());

		registry.addResourceHandler("/public/site/investorpre/**")
				.addResourceLocations(MICROSITE_INVESTOR_RESOURCE_LOCATION.value())
				.setCachePeriod(MICROSITE_INVESTOR_CACHE_PERIOD.intValue());
	}

	@Bean
	public ViewResolver viewResolver()
	{
		UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
		viewResolver.setViewClass(TilesView.class);
		return viewResolver;
	}

	@Bean
	@Order(value = 1)
	public ExceptionResolver exceptionResolver()
	{
		return new ExceptionResolver();
	}

	@Bean
	@Order(value = 0)
	public AnnotationMethodHandlerExceptionResolver annotationMethodHandlerExceptionResolver()
	{
		return new AnnotationMethodHandlerExceptionResolver();
	}

	@Bean
	public TilesConfigurer tilesConfigurer()
	{
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions(new String[]
		{
			"/WEB-INF/tiles-defs.xml", "/WEB-INF/tiles-defs/admin.xml", "/WEB-INF/tiles-defs/global-elements.xml",
			"/WEB-INF/tiles-defs/logon.xml", "/WEB-INF/tiles-defs/registration.xml", "/WEB-INF/tiles-defs/service-operator.xml",
			"/WEB-INF/tiles-defs/error.xml"
		});

		tilesConfigurer.setPreparerFactoryClass(SimpleSpringPreparerFactory.class);

		return tilesConfigurer;
	}

	@Bean
	public MultipartResolver multipartResolver()
	{
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(Long.parseLong(Properties.get("fileupload.max.file.size")));
		return multipartResolver;
	}

}