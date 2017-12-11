package com.bt.nextgen.api.option.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.option.v1.model.AccountOptionsDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * This service retrieves and updates a user's saved preferences.
 */
@Service("AccountOptionDtoServiceV1")
public class AccountOptionDtoServiceImpl implements AccountOptionDtoService {
    @Autowired
    private OptionsService optionService;

    @Override
    public AccountOptionsDto find(AccountKey key, ServiceErrors serviceErrors) {
        AccountOptionsDto dto = new AccountOptionsDto(key);
        Collection<OptionValue<String>> options = optionService.getOptions(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())),
                serviceErrors);
        for (OptionValue<String> option : options) {
            dto.addOption(option.getOptionValueKey().getOptionKey().getOptionName(), option.getValue());
        }
        return dto;
    }

}
