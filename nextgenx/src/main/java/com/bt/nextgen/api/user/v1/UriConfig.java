package com.bt.nextgen.api.user.v1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("userV1")
@PropertySource(value = "classpath:/com/bt/nextgen/api/user/v1/UriConfig.properties")
@SuppressWarnings("squid:S2094")
// Configuration class for module uris
public class UriConfig {
}
