package com.bt.nextgen.api.draftaccount;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by m040398 on 3/02/2016.
 * JSON formData validator impl.
 */
@Service
public class FormDataValidatorImpl implements FormDataValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormDataValidatorImpl.class);

    //US23991: to address the XSS vulnerability we match json fields values with this pattern
    private final static Pattern XSS_PATTERN = Pattern.compile(".*(<|>|&lt;|&gt;).*");

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper objectMapper;

    private final JsonSchemaFactory factory =
            JsonSchemaFactory.newBuilder()
                    .setLoadingConfiguration(LoadingConfiguration.newBuilder()
                            .setURITranslatorConfiguration(URITranslatorConfiguration.newBuilder().setNamespace(NAMESPACE_SCHEMAS).freeze())
                            .freeze())
                    .freeze();

    @Override
    public ProcessingReport validate(Map<String, Object> formData) throws ProcessingException {
        final String formDataJson;
        try {
            formDataJson = objectMapper.writeValueAsString(formData);
        } catch (JsonProcessingException e) {
            LOGGER.error("error writing json string", e);
            throw new ProcessingException("error writing json", e);
        }
        try {
            return this.validate(JsonLoader.fromString(formDataJson), APPLICATION_JSON_SCHEMA);
        }catch (IOException ex) {
            throw new ProcessingException("error loading json string into JsonNode", ex);
        }
    }


    public void replaceChars(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                replaceCharsInFieldValue(fields.next());
            }
        } else if (node.isArray()) {
            Iterator<JsonNode> items = node.iterator();
            while(items.hasNext()) {
                replaceChars(items.next());
            }
        }
    }

    /**
     * Replace ',' and '>' with space if present in the node value
     * @param field
     */
    private void replaceCharsInFieldValue(Map.Entry<String, JsonNode> field){
        if (field.getValue().isTextual()) {
            String value = field.getValue().asText();
            if (XSS_PATTERN.matcher(value).matches()) {
                field.setValue(new TextNode(replaceTags(value)));
            }
        } else if (field.getValue().isContainerNode()) {
            replaceChars(field.getValue());
        }
    }

    private String replaceTags(String s) {
        return s.replace("<", " ").replace(">", " ").replace("&lt;", " ").replace("&gt;", " ").trim();
    }

    @Override
    public ProcessingReport validate(final JsonNode formDataJsonNode, String jsonSchema) throws ProcessingException {
        final JsonSchema schema = factory.getJsonSchema(jsonSchema);
        //US23991 - remove '<' and '>' from any field value
        replaceChars(formDataJsonNode);
        return
                schema.validate(formDataJsonNode);
    }

    /**
     * For testing only
     * @param mapper
     */
    public void setObjectMapper(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }
}
