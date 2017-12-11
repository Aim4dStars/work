package com.bt.nextgen.api.draftaccount;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.fail;

/**
 * Base class for reading JSON resources from the class path. By default, expects to find these resources within
 * the exact same path as the child test class.
 */
public abstract class AbstractJsonReaderTest {

    private ClassLoader classLoader;

    private ObjectMapper mapper;

    private String pathPrefix;

    @Before
    public void initClassLoaderAndObjectMapper() {
        classLoader = currentThread().getContextClassLoader();
        mapper = new ObjectMapper();
        final String className = getClass().getName();
        pathPrefix = className.substring(0, className.lastIndexOf('.') + 1).replace('.', '/');
    }

    protected Map<String, Object> readJsonFromFile(String fileName) throws IOException {
        InputStream in = null;
        String fullName = fileName;
        if (!fileName.contains("/")) {
            fullName = pathPrefix + fileName;
        }
        in = classLoader.getResourceAsStream(fullName);
        if (in == null) {
            fail("JSON classpath resource not found: " + fullName);
        }
        return mapper.readValue(IOUtils.toString(in), new TypeReference<Map<String, Object>>() {});
    }

    protected String readJsonStringFromFile(String fileName) throws IOException {
        InputStream in = null;
        String fullName = fileName;
        if (!fileName.contains("/")) {
            fullName = pathPrefix + fileName;
        }
        in = classLoader.getResourceAsStream(fullName);
        if (in == null) {
            fail("JSON classpath resource not found: " + fullName);
        }
        return IOUtils.toString(in);
    }

}
