package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * ClientApplicationDto implementation for DIRECT
 *
 * Created by m040398 on 18/07/2016.
 */
public class ClientApplicationDtoDirectImpl extends BaseClientApplicationDto {

    @JsonIgnore
    private DirectClientApplicationFormData formData; // auto-generated from JSON schema: directAccountApplicationSchema.json

    public ClientApplicationDtoDirectImpl() {
    }

    public ClientApplicationDtoDirectImpl(ClientApplicationKey key, DirectClientApplicationFormData formData) {
        super(key);
        this.formData = formData;
    }

    public ClientApplicationDtoDirectImpl(DirectClientApplicationFormData formData) {
        this.formData = formData;
    }

    public ClientApplicationDtoDirectImpl(ClientApplicationKey key) {
        super(key);
    }

    @Override
    public Object getFormData() {
        return formData;
    }

    @Override
    public void setFormData(Object formData) {
        this.formData = (DirectClientApplicationFormData) formData;
    }

    @Override
    public boolean isJsonSchemaSupported() {
        return true;
    }

    @Override
    public boolean isDirectApplication() {
        return true;
    }

    @JsonIgnore
    public String getAccountType() {
        return toString(formData.getAccountType());
    }

}
