package com.bt.nextgen.api.prm;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("prmV3")
@PropertySource(value = "classpath:/com/bt/nextgen/api/prm/v1/UriConfig.properties")
public class UriConfig {
}
