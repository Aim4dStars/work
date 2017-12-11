package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * ClientApplicationDto implementation usiong a Map<String, Object> as a JSON payload holder
 *
 * Created by m040398 on 18/07/2016.
 */
public class ClientApplicationDtoMapImpl extends BaseClientApplicationDto {

    private Map<String, Object> formData;

    public ClientApplicationDtoMapImpl(){
    }

    public ClientApplicationDtoMapImpl(ClientApplicationKey key, Map<String, Object> formData) {
        super(key);
        this.formData = formData;
    }

    public ClientApplicationDtoMapImpl(Map<String, Object> formData) {
        this.formData = formData;
    }

    public ClientApplicationDtoMapImpl(ClientApplicationKey key) {
        super(key);
    }

    @Override
    public Object getFormData() {
        return formData;
    }

    @Override
    public void setFormData(Object formData) {
        this.formData = (Map<String, Object>) formData;
    }

    @Override
    public boolean isJsonSchemaSupported() {
        return formData != null && formData.get(FormDataConstants.FIELD_VERSION) != null;
    }

    @Override
    public boolean isDirectApplication() {
        return isJsonSchemaSupported() && FormDataConstants.VALUE_APPLICATION_ORIGIN_DIRECT.equals(formData.get(FormDataConstants.FIELD_APPLICATION_ORIGIN));
    }

    @JsonIgnore
    public String getAccountType() {
        return toString(formData.get(FormDataConstants.FIELD_ACCOUNT_TYPE));
    }

}
