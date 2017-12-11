package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.IncomePreferenceDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("IncomePreferenceDtoServiceV3")
public class IncomePreferenceDtoServiceImpl implements IncomePreferenceDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Override
    public IncomePreferenceDto update(IncomePreferenceDto prefDto, ServiceErrors serviceErrors) {
        accountIntegrationService.updateIncomePreferenceOption(
                SubAccountKey.valueOf(EncodedString.toPlainText(prefDto.getKey().getId())), prefDto.getIncomePreference(),
                serviceErrors);
        
        return prefDto;
    }

}
