package com.bt.nextgen.service.integration.options.repository;

import com.bt.nextgen.service.integration.options.model.Option;
import com.bt.nextgen.service.integration.options.model.OptionType;

import java.util.Collection;

public interface OptionRepository {
    Collection<Option<String>> searchStringOptions(OptionType optionType);

    Collection<Option<Boolean>> searchToggleOptions(OptionType optionType);
}