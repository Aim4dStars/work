package com.bt.nextgen.service.integration.options.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
// Not using discriminators as we don't want to allow
// this table to reach the java tier in mixed mode.
@Table(name = "OPTION_VALUE")
public class ToggleOptionValueImpl implements OptionValue<Boolean> {
    @EmbeddedId
    private OptionValueKey optionValueKey;

    @Column(name = "VALUE")
    // true/false rather than 0/1 for readability because we're storing it in a mixed mode column.
    private String hasFeature;

    private ToggleOptionValueImpl() {
        // for jpa
    }

    public ToggleOptionValueImpl(OptionValueKey optionValueKey, Boolean hasFeature) {
        this();
        this.optionValueKey = optionValueKey;
        this.hasFeature = hasFeature.toString().toLowerCase();
    }

    @Override
    public OptionValueKey getOptionValueKey() {
        return optionValueKey;
    }

    @Override
    public Boolean getValue() {
        if (hasFeature == null)
            return null;
        return Boolean.valueOf(hasFeature);
    }

}
