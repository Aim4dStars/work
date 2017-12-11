package com.bt.nextgen.api.modelpreference.v1.service;

import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreferenceIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubaccountPreferencesDtoServiceImpl extends BasePreferencesDtoServiceImpl
        implements SubaccountPreferencesDtoService {
    @Autowired
    private ModelPreferenceIntegrationService integrationService;

    public SubaccountPreferencesDto find(com.bt.nextgen.api.account.v3.model.AccountKey key, ServiceErrors serviceErrors) {
        AccountKey integrationKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        List<ModelPreference> data = integrationService.getPreferencesForSubaccount(integrationKey, serviceErrors);
        Map<AccountKey, IssuerAccount> assetMap = getIssuerMap(data, serviceErrors);

        return new SubaccountPreferencesDto(key, toPreferencesDto(data, assetMap));
    }

}
