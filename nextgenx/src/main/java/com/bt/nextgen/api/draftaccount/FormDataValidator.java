package com.bt.nextgen.api.draftaccount;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import java.util.Map;

/**
 * Created by m040398 on 3/02/2016.
 *
 * This interface must be used to validate the 'formData' POST-ed to /draft_accounts API
 */
public interface FormDataValidator {

    String NAMESPACE_SCHEMAS = "resource:/com/bt/nextgen/api/draftaccount/schemas/v1/";

    String APPLICATION_JSON_SCHEMA = "accountApplicationSchema.json";

    String JAVA_PACKAGE_JSON_SCHEMAS = "com.bt.nextgen.api.draftaccount.schemas.v1";

    String DIRECT_APPLICATION_JSON_SCHEMA = "directAccountApplicationSchema.json";

    ProcessingReport validate(Map<String, Object> formData) throws ProcessingException;

    ProcessingReport validate(JsonNode formDataJsonNode, String jsonSchema) throws ProcessingException;
}
