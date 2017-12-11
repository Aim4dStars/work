package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.options.model.CategoryKey;
import com.bt.nextgen.service.integration.options.model.Option;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.model.OptionType;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.model.OptionValueKey;
import com.bt.nextgen.service.integration.options.model.StringOptionValueImpl;
import com.bt.nextgen.service.integration.options.model.ToggleOptionValueImpl;
import com.bt.nextgen.service.integration.options.repository.OptionRepository;
import com.bt.nextgen.service.integration.options.repository.OptionValueRepository;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OptionsServiceImpl implements OptionsService {
    protected static final String FEATURES_CACHE = "com.bt.nextgen.service.integration.options.service.OptionsService.features";
    protected static final String OPTIONS_CACHE = "com.bt.nextgen.service.integration.options.service.OptionsService.options";

    @Autowired
    private BrokerHierarchyFactory brokerHierarchyFactory;
    @Autowired
    private ProductHierarchyFactory productHierarchyFactory;
    @Autowired
    private AccountHierarchyFactory accountHierarchyFactory;


    @Override
    @Cacheable(key = "#product", value = OPTIONS_CACHE)
    public Collection<OptionValue<String>> getOptions(ProductKey product, ServiceErrors serviceErrors) {
        List<CategoryKey> categories = productHierarchyFactory.buildHierarchy(product, serviceErrors);
        return resolveStringHierarchy(OptionType.PRODUCT_OPTION, categories).values();
    }

    @Override
    @Cacheable(key = "#account", value = OPTIONS_CACHE)
    public Collection<OptionValue<String>> getOptions(AccountKey account, ServiceErrors serviceErrors) {
        List<CategoryKey> categories = accountHierarchyFactory.buildHierarchy(account, serviceErrors);
        return resolveStringHierarchy(OptionType.PRODUCT_OPTION, categories).values();
    }

    @Override
    @Cacheable(key = "#broker", value = OPTIONS_CACHE)
    public Collection<OptionValue<String>> getOptions(@Nullable BrokerKey broker, ServiceErrors serviceErrors) {
        List<CategoryKey> categories;
        if (broker != null) {
            categories = brokerHierarchyFactory.buildHierarchy(broker, serviceErrors);
        } else {
            categories = Collections.emptyList();
        }
        return resolveStringHierarchy(OptionType.BROKER_OPTION, categories).values();
    }

    @Override
    @Cacheable(key = "#product", value = FEATURES_CACHE)
    public Collection<OptionValue<Boolean>> getFeatures(ProductKey product, ServiceErrors serviceErrors) {
        List<CategoryKey> categories = productHierarchyFactory.buildHierarchy(product, serviceErrors);
        return resolveToggleHierarchy(OptionType.PRODUCT_TOGGLE, categories).values();
    }

    @Override
    @Cacheable(key = "#account", value = FEATURES_CACHE)
    public Collection<OptionValue<Boolean>> getFeatures(AccountKey account, ServiceErrors serviceErrors) {
        List<CategoryKey> categories = accountHierarchyFactory.buildHierarchy(account, serviceErrors);
        return resolveToggleHierarchy(OptionType.PRODUCT_TOGGLE, categories).values();
    }

    @Override
    @Cacheable(key = "{#optionKey, #accountKey}", value = FEATURES_CACHE)
    public Boolean hasFeature(OptionKey optionKey, AccountKey accountKey, ServiceErrors serviceErrors) {
        Collection<OptionValue<Boolean>> optionValues = getFeatures(accountKey, serviceErrors);
        for (OptionValue<Boolean> optionValue : optionValues) {
            String optionName = optionValue.getOptionValueKey().getOptionKey().getOptionName();
            if (optionName.equals(optionKey.getOptionName())) {
                return optionValue.getValue();
            }
        }
        return false;
    }

    @Override
    @Cacheable(key = "{#optionKey, #accountKey}", value = OPTIONS_CACHE)
    public String getOption(OptionKey optionKey, AccountKey accountKey, ServiceErrors serviceErrors) {
        Collection<OptionValue<String>> optionValues = getOptions(accountKey, serviceErrors);
        for (OptionValue<String> optionValue : optionValues) {
            String optionName = optionValue.getOptionValueKey().getOptionKey().getOptionName();
            if (optionName.equals(optionKey.getOptionName())) {
                return optionValue.getValue();
            }
        }
        return "";
    }


    @Autowired
    private OptionRepository optionRepo;

    @Autowired
    private OptionValueRepository optionValueRepo;

    private Map<OptionKey, OptionValue<Boolean>> resolveToggleHierarchy(OptionType optionType, List<CategoryKey> categories) {
        Map<OptionKey, OptionValue<Boolean>> result = new HashMap<>();
        for (CategoryKey category : categories) {
            Collection<OptionValue<Boolean>> categoryOptions = optionValueRepo.searchToggleOptions(optionType, category);
            for (OptionValue<Boolean> optionValue : categoryOptions) {
                if (result.get(optionValue.getOptionValueKey().getOptionKey()) == null) {
                    result.put(optionValue.getOptionValueKey().getOptionKey(), optionValue);
                }
            }
        }

        Collection<Option<Boolean>> allOptions = optionRepo.searchToggleOptions(optionType);
        for (Option<Boolean> option : allOptions) {
            if (result.get(option.getOptionKey()) == null) {
                result.put(
                        option.getOptionKey(),
                        new ToggleOptionValueImpl(OptionValueKey.valueOf(null, null, option.getOptionKey()), option
                                .getDefaultValue()));
            }
        }
        return result;
    }

    // TODO - there has got to be a better way than duplicating this logic
    private Map<OptionKey, OptionValue<String>> resolveStringHierarchy(OptionType optionType, List<CategoryKey> categories) {
        Map<OptionKey, OptionValue<String>> result = new HashMap<>();
        for (CategoryKey category : categories) {
            Collection<OptionValue<String>> categoryOptions = optionValueRepo.searchStringOptions(optionType, category);
            for (OptionValue<String> optionValue : categoryOptions) {
                if (result.get(optionValue.getOptionValueKey().getOptionKey()) == null) {
                    result.put(optionValue.getOptionValueKey().getOptionKey(), optionValue);
                }
            }
        }

        Collection<Option<String>> allOptions = optionRepo.searchStringOptions(optionType);
        for (Option<String> option : allOptions) {
            if (result.get(option.getOptionKey()) == null) {
                result.put(
                        option.getOptionKey(),
                        new StringOptionValueImpl(OptionValueKey.valueOf(null, null, option.getOptionKey()), option
                                .getDefaultValue()));
            }
        }
        return result;
    }

}
