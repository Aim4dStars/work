package com.bt.nextgen.api.account.v2;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Deprecated
@Configuration("accountV2")
@PropertySource(value = "classpath:/com/bt/nextgen/api/account/v2/UriConfig.properties")
@SuppressWarnings("squid:S2094")
// Configuration class for module uris
public class UriConfig {
}
