package com.bt.nextgen.service.integration.options.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
// Not using discriminators as we don't want to allow
// this table to reach the java tier in mixed mode.
@Table(name = "OPTION_REF")
public class StringOptionImpl implements Option<String> {
    @EmbeddedId
    private OptionKey optionKey;

    @Column(name = "OPTION_TYPE")
    @Enumerated(EnumType.STRING)
    private OptionType optionType;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    public OptionKey getOptionKey() {
        return optionKey;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
