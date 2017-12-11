package com.bt.nextgen.api.basil;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by M035995 on 8/11/2016.
 */
@Configuration("basilV3")
@PropertySource(value = "classpath:/com/bt/nextgen/api/basil/v3/UriConfig.properties")
public class UriConfig {

    public static final String MIME_TYPE = "mimeType";

    public static final String DOCUMENT_TYPE = "docType";

    public static final String DOCUMENT_ID = "docId";

}