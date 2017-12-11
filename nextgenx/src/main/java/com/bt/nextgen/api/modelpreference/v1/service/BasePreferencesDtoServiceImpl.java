package com.bt.nextgen.api.modelpreference.v1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.modelpreference.v1.model.PreferenceDto;
import com.bt.nextgen.api.order.model.PreferenceActionDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.order.ModelPreferenceActionImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.SubaccountModelPreferences;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;

class BasePreferencesDtoServiceImpl {
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    List<PreferenceDto> toPreferencesDto(List<ModelPreference> data, Map<AccountKey, IssuerAccount> issuerMap) {
        List<PreferenceDto> prefs = new ArrayList<>();
        DateTime now = new DateTime();
        for (ModelPreference pref : data) {
            if (pref.getEndDate() != null && pref.getEndDate().isBefore(now)) {
                // expired preference
                continue;
            }
            IssuerAccount issuer = issuerMap.get(pref.getIssuer());
            String issuerName = null;
            if (issuer != null) {
                issuerName = issuer.getAccountName();
            }
            prefs.add(new PreferenceDto(pref.getIssuer().getId(), issuerName, pref.getPreference().name(), pref.getEffectiveDate()));

        }
        return prefs;
    }

    List<ModelPreferenceAction> toPreferences(List<PreferenceActionDto> data) {
        List<ModelPreferenceAction> prefs = new ArrayList<>();
        for (PreferenceActionDto pref : data) {
            ModelPreferenceAction preference = new ModelPreferenceActionImpl(AccountKey.valueOf(pref.getIssuerId()),
                    pref.getPreference(), pref.getAction());
            prefs.add(preference);
        }
        return prefs;
    }

    Map<AccountKey, IssuerAccount> getIssuerMap(AccountModelPreferences preferences, ServiceErrors serviceErrors) {
        HashSet<AccountKey> issuers = new HashSet<>();

        issuers.addAll(Lambda.extract(preferences.getPreferences(), Lambda.on(ModelPreference.class).getIssuer()));
        for (SubaccountModelPreferences subAccount : preferences.getSubaccountPreferences()) {
            issuers.addAll(Lambda.extract(subAccount.getPreferences(), Lambda.on(ModelPreference.class).getIssuer()));
        }
        if (!issuers.isEmpty()) {
            List<IssuerAccount> accounts = accountService.loadIssuerAccount(issuers, serviceErrors);
            return Lambda.index(accounts, Lambda.on(IssuerAccount.class).getAccountKey());
        }
        return Collections.emptyMap();
    }

    Map<AccountKey, IssuerAccount> getIssuerMap(List<ModelPreference> preferences, ServiceErrors serviceErrors) {
        HashSet<AccountKey> issuers = new HashSet<>();
        issuers.addAll(Lambda.extract(preferences, Lambda.on(ModelPreference.class).getIssuer()));
        if (!issuers.isEmpty()) {
            List<IssuerAccount> accounts = accountService.loadIssuerAccount(issuers, serviceErrors);
            return Lambda.index(accounts, Lambda.on(IssuerAccount.class).getAccountKey());
        }
        return Collections.emptyMap();
    }
}