package com.bt.nextgen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(
	{
		"classpath:/spring/spring-security.xml"
	})
public class SecurityConfig
{
}