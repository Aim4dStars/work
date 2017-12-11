package com.bt.nextgen.api.client.v3;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("client")
@PropertySource(value = "classpath:/com/bt/nextgen/api/client/v3/UriConfig.properties")
public class UriConfig {

}