package com.bt.nextgen.config;

import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("SecureJsonObjectMapper")
public class SecureJsonObjectMapper extends JsonObjectMapper {

    public SecureJsonObjectMapper() {
        super();

        // turn off mapping of fields by default. Fields to be included should have an annotation of @JsonView(JsonViews.Write.class)
        this.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
    }
}
