package com.bt.nextgen.api.chesssponsor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by L078480 on 22/06/2017.
 */
@Configuration("chessV1")
@PropertySource(value = "classpath:/com/bt/nextgen/api/chess/v1/UriConfig.properties")
public class UriConfig {
}
