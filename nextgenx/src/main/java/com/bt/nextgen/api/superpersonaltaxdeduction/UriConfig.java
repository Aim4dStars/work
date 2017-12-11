package com.bt.nextgen.api.superpersonaltaxdeduction;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("personaltaxdeductionV1")
@PropertySource(value = "classpath:/com/bt/nextgen/api/superpersonaltaxdeduction/v1/UriConfig.properties")
@SuppressWarnings("squid:S2094")
// Configuration class for module uris
public class UriConfig {
}