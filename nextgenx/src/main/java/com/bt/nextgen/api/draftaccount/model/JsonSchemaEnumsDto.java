package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by M040398 on 23/08/2016.
 */
public class JsonSchemaEnumsDto extends BaseDto {

    private Map<String, Object> root = new HashMap<>();

    public void addEnumValues(String name, Object enumValues) {
        root.put(name, enumValues);
    }

    public Map<String, Object> getRoot() {
        return Collections.unmodifiableMap(root);
    }
}
