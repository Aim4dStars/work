package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IFeeComponentForm;
import com.bt.nextgen.api.draftaccount.model.form.IFeesComponentsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.fees.FeesComponentType;

import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static java.util.Collections.emptyList;

/**
 * Wrapper for the JSON schema-generated {@code FeesComponentType}.
 */
class FeesComponentsForm implements IFeesComponentsForm {

    private final FeesComponentType components;

    public FeesComponentsForm(FeesComponentType components) {
        this.components = components;
    }

    @Override
    public List<IFeeComponentForm> getElements() {
        List<IFeeComponentForm> elements = emptyList();
        if (components != null) {
            elements = convert(components.getFeesComponent(), FeeComponentForm.CONVERTER);
        }
        return elements;
    }
}
