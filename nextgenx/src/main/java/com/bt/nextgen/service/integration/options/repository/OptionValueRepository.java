package com.bt.nextgen.service.integration.options.repository;

import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.OptionType;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.model.OptionValueKey;

import java.util.Collection;

public interface OptionValueRepository
{
    OptionValue<Boolean> findToggleOptionValue(OptionValueKey key);

    OptionValue<String> findStringOptionValue(OptionValueKey key);

    Collection<OptionValue<Boolean>> searchToggleOptions(OptionType optionType, CategoryKey category);

    Collection<OptionValue<String>> searchStringOptions(OptionType optionType, CategoryKey category);
}