package com.bt.nextgen.api.option.v1.service;

import com.bt.nextgen.api.option.v1.model.OptionsDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.options.model.OptionValue;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * This service retrieves and updates a user's saved preferences.
 */
@Service("UserOptionDtoServiceV1")
public class UserOptionDtoServiceImpl implements UserOptionDtoService {
    @Autowired
    private OptionsService optionService;

    @Autowired
    private UserProfileService userProfileService;


    @Override
    public OptionsDto findOne(ServiceErrors serviceErrors) {

        OptionsDto dto = new OptionsDto();

        BrokerKey broker = BrokerKey.valueOf(userProfileService.getPositionId());
        // investor, paraplanner linked to more than one etc will get default options only
        Collection<OptionValue<String>> options = optionService.getOptions(broker, serviceErrors);
        for (OptionValue<String> option : options) {
            dto.addOption(option.getOptionValueKey().getOptionKey().getOptionName(), option.getValue());
        }
        return dto;
    }

}
