package com.bt.nextgen.service.integration.options.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
// Not using discriminators as we don't want to allow
// this table to reach the java tier in mixed mode.
@Table(name = "OPTION_VALUE")
public class StringOptionValueImpl implements OptionValue<String> {
    @EmbeddedId
    private OptionValueKey optionValueKey;

    @Column(name = "VALUE")
    private String value;

    private StringOptionValueImpl() {
        // for jpa
    }

    public StringOptionValueImpl(OptionValueKey optionValueKey, String value) {
        this();
        this.optionValueKey = optionValueKey;
        this.value = value;
    }

    @Override
    public OptionValueKey getOptionValueKey() {
        return optionValueKey;
    }

    @Override
    public String getValue() {
        return value;
    }

}
