package com.bt.nextgen.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableCaching
@ComponentScan(basePackages =
{ "com.bt.nextgen" })
public class CacheConfig// implements CachingConfigurer 
{
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean()
	{
		EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
		factory.setConfigLocation(new ClassPathResource("/cache/ehcache.xml"));
		factory.setCacheManagerName(EhCacheManagerFactoryBean.class.getName());
		return factory;
	}

	@Bean
	public CacheManager cacheManager()
	{
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
		return cacheManager;
	}

}