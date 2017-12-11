package com.bt.nextgen.api.modelpreference.v1.service;

import com.bt.nextgen.api.modelpreference.v1.model.AccountPreferencesDto;
import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreferenceIntegrationService;
import com.bt.nextgen.service.integration.modelpreferences.SubaccountModelPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccountPreferencesDtoServiceImpl extends BasePreferencesDtoServiceImpl implements AccountPreferencesDtoService {
    @Autowired
    private ModelPreferenceIntegrationService integrationService;

    @Override
    public AccountPreferencesDto find(com.bt.nextgen.api.account.v3.model.AccountKey key, ServiceErrors serviceErrors) {
        AccountKey integrationKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        AccountModelPreferences preferences = integrationService.getPreferencesForAccount(integrationKey, serviceErrors);
        Map<AccountKey, IssuerAccount> issuerMap = getIssuerMap(preferences, serviceErrors);

        List<SubaccountPreferencesDto> subaccounts = toSubAccountDto(preferences.getSubaccountPreferences(), issuerMap);
        return new AccountPreferencesDto(key, subaccounts, toPreferencesDto(preferences.getPreferences(), issuerMap));
    }

    private List<SubaccountPreferencesDto> toSubAccountDto(List<SubaccountModelPreferences> subaccounts,
            Map<AccountKey, IssuerAccount> issuerMap) {
        List<SubaccountPreferencesDto> subAccountPrefs = new ArrayList<>();
        for (SubaccountModelPreferences subAccount : subaccounts) {
            SubaccountPreferencesDto subAccountPref = new SubaccountPreferencesDto(
                    new com.bt.nextgen.api.account.v3.model.AccountKey(
                            EncodedString.fromPlainText(
                            subAccount.getAccountKey().getId()).toString()), toPreferencesDto(subAccount.getPreferences(),
 issuerMap));
            subAccountPrefs.add(subAccountPref);

        }
        return subAccountPrefs;
    }


}
