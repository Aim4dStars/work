package com.bt.nextgen.api.modelpreference.v1.service;

import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesActionDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreferenceIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubaccountPreferencesDtoServiceSubmitImpl extends BasePreferencesDtoServiceImpl
        implements SubaccountPreferencesSubmitDtoService {
    @Autowired
    private ModelPreferenceIntegrationService integrationService;

    @Override
    public SubaccountPreferencesActionDto submit(SubaccountPreferencesActionDto subAccountPrefs, ServiceErrors serviceErrors) {
        AccountKey integrationKey = AccountKey.valueOf(EncodedString.toPlainText(subAccountPrefs.getKey().getAccountId()));

        integrationService.updatePreferencesForSubaccount(integrationKey,
                toPreferences(subAccountPrefs.getPreferences()), serviceErrors);
        return subAccountPrefs;
    }

}
