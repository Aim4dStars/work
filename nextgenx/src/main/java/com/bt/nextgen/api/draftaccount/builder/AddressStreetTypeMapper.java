package com.bt.nextgen.api.draftaccount.builder;

import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utility class for mapping street types. Uses a classpath resource to load in the mappings.
 */
@Component
public class AddressStreetTypeMapper {

    private static final String RESOURCE = "addressStreetTypes.json";

    private Map<String, String> streetTypes = null;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper objectMapper;

    @PostConstruct
    public synchronized void initStreetTypes() throws IOException {
        if (streetTypes == null) {
            final InputStream in = getClass().getClassLoader().getResourceAsStream(RESOURCE);
            this.streetTypes = getObjectMapper().readValue(IOUtils.toString(in), new TypeReference<Map<String, String>>(){});
        }
    }

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            // This won't happen in a Spring container environment, but makes POJO unit testing more convenient...
            objectMapper = new JsonObjectMapper();
        }
        return objectMapper;
    }

    private synchronized Map<String, String> getStreetTypes() {
        if (streetTypes == null) {
            try {
                initStreetTypes();
            } catch (IOException ioe) {
                getLogger(AddressStreetTypeMapper.class).error("I/O error loading resource {}", RESOURCE, ioe);
            }
        }
        return streetTypes;
    }

    public String getStandardStreetType(String streetType){
        if (isNotBlank(streetType)) {
            return getStreetTypes().get(streetType.trim().toUpperCase());
        }
        return "";
    }
}
