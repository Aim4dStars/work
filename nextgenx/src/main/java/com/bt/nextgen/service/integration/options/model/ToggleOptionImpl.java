package com.bt.nextgen.service.integration.options.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
// Not using discriminators as we don't want to allow
// this table to reach the java tier in mixed mode.
@Table(name = "OPTION_REF")
public class ToggleOptionImpl implements Option<Boolean>, Serializable {

    @EmbeddedId
    private OptionKey optionKey;

    @Column(name = "OPTION_TYPE")
    @Enumerated(EnumType.STRING)
    private OptionType optionType;

    @Column(name = "DEFAULT_VALUE")
    // true/false rather than 0/1 for readability because we're storing it in a mixed mode column.
    private String defaultValue;

    private ToggleOptionImpl() {
        // for jpa
    }

    @Override
    public OptionKey getOptionKey() {
        return optionKey;
    }

    @Override
    public OptionType getOptionType() {
        return optionType;
    }

    @Override
    public Boolean getDefaultValue() {
        if (defaultValue == null)
            return null;
        return Boolean.valueOf(defaultValue);
    }

}
