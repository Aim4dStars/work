package com.bt.nextgen.api.draftaccount.schemas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Base class for tests that wish to utilise the Jackson JSON {@code ObjectMapper}
 * in order to load instances of POJOs mapped from schema files.
 * @param <J> the class under test, assumed to be serializable from a correctly-formatted JSON file.
 */
public abstract class AbstractObjectMapperTest<J> {

    /** Default Object Mapper. Initialized only once per class loader, to save on expensive Object creation. */
    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    private static final Pattern ARRAY_SELECTOR = Pattern.compile("\\[(\\d+)\\]$");

    private final Class<J> clazz;

    private final String pathPrefix;

    protected AbstractObjectMapperTest(Class<J> clazz, String pathPrefix) {
        this.clazz = clazz;
        this.pathPrefix = pathPrefix;
    }

    protected AbstractObjectMapperTest(Class<J> clazz) {
        this(clazz, pathToClass(AbstractObjectMapperTest.class));
    }

    protected static String pathToClass(Class<?> clazz) {
        String path = clazz.getName();
        path = path.substring(0, path.lastIndexOf('.') + 1);
        return path.replace('.', '/');
    }

    /**
     * Subclasses can override this method to swap in a different ObjectMapper instance, if desired.
     * @return the Jackson ObjectMapper instance to be used to load in JSON files.
     */
    protected ObjectMapper getObjectMapper() {
        return DEFAULT_MAPPER;
    }

    /**
     * Read the specified classpath resource (assumed to be a JSON file), and return it as a validated instance of
     * the specified class for this test.
     * @param fileName name of the classpath resource. Assumed to end in &quot;.json&quot;, and be in the same package
     *  as the test class.
     * @param jsonPath JSON path to the specific node within the main document that is to be loaded.
     * @return the deserialised instance.
     * @throws IOException I/O error occurred.
     */
    protected J readJsonResource(String fileName, String jsonPath) throws IOException {
        final String path = pathPrefix + fileName + ".json";
        final InputStream is = currentThread().getContextClassLoader().getResourceAsStream(path);
        assertNotNull("Classpath resource " + path + " not found", is);
        final ObjectMapper mapper = getObjectMapper();
        JsonNode node = mapper.readTree(is);
        if (isNotBlank(jsonPath)) {
            node = resolvePath(node, jsonPath);
        }
        return node != null && !node.isMissingNode() ? mapper.readValue(node.toString(), clazz) : null;
    }

    /**
     * Read the specified classpath resource (assumed to be a JSON file), and return it as a validated instance of
     * the specified class for this test.
     * @param fileName name of the classpath resource. Assumed to end in &quot;.json&quot;, and be in the same package
     *  as the test class.
     * @return the deserialised instance.
     * @throws IOException I/O error occurred.
     */
    protected J readJsonResource(String fileName) throws IOException {
        return readJsonResource(fileName, null);
    }

    /**
     * Recursively resolve the nested path within the provided object.
     * @param base base Node from which to begin.
     * @param jsonPath the path to traverse within the JSON object. Delimited with slash characters (to denote
     *  child nodes), and also with square brackets to denote array item selection.
     * @return the discovered node, or a {@link JsonNode#isMissingNode() missing node} reference if the path cannot
     *   be resolved.
     */
    protected static JsonNode resolvePath(JsonNode base, String jsonPath) {
        JsonNode node = base;
        String path = jsonPath;
        while (isNotBlank(path) && !node.isMissingNode()) {
            int slashIdx = path.indexOf('/');
            String nodeName;
            if (slashIdx > 0) {
                nodeName = path.substring(0, slashIdx);
                path = path.substring(slashIdx + 1);
            } else {
                nodeName = path;
                path = "";
            }
            int arrayIndex = -1;
            final Matcher m = ARRAY_SELECTOR.matcher(nodeName);
            if (m.find()) {
                arrayIndex = Integer.parseInt(m.group(1));
                nodeName = nodeName.substring(0, m.start());
            }
            node = node.findPath(nodeName);
            if (arrayIndex >= 0 && node.isArray()) {
                node = node.get(arrayIndex);
            }
        }
        return node;
    }
}