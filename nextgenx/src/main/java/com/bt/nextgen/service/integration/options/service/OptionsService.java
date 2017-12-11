package com.bt.nextgen.service.integration.options.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.product.ProductKey;

import javax.annotation.Nullable;

import java.util.Collection;

public interface OptionsService {
    Collection<OptionValue<String>> getOptions(ProductKey product, ServiceErrors serviceErrors);

    Collection<OptionValue<String>> getOptions(AccountKey account, ServiceErrors serviceErrors);

    Collection<OptionValue<String>> getOptions(@Nullable BrokerKey user, ServiceErrors serviceErrors);

    Collection<OptionValue<Boolean>> getFeatures(ProductKey product, ServiceErrors serviceErrors);

    Collection<OptionValue<Boolean>> getFeatures(AccountKey account, ServiceErrors serviceErrors);

    Boolean hasFeature(OptionKey optionKey, AccountKey accountKey, ServiceErrors serviceErrors);

    String getOption(OptionKey optionKey, AccountKey accountKey, ServiceErrors serviceErrors);
}
