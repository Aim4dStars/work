package com.bt.nextgen.service.integration.options.model;

public interface Option<T> {

    OptionKey getOptionKey();

    OptionType getOptionType();

    T getDefaultValue();

}