package com.bt.nextgen.service.integration.options.model;

public interface OptionValue<T> {
    OptionValueKey getOptionValueKey();

    T getValue();
}
