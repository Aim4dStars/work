package com.bt.nextgen.api.policy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("policyV1")
@PropertySource(value = "classpath:/com/bt/nextgen/api/policy/v1/UriConfig.properties")
@SuppressWarnings("squid:S2094")
// Configuration class for module uris
public class UriConfig {
}