package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.SubaccountModelPreferences;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/")
public class AccountModelPreferencesImpl implements AccountModelPreferences {

    @ServiceElementList(xpath = "//bp_list/bp/bp_head_list/bp_head/bp_pref_list/bp_pref", type = ModelPreferenceImpl.class)
    private List<ModelPreference> preferences;

    @ServiceElementList(xpath = "//bp_list/bp/cont_list/cont/cont_head_list/cont_head", type = SubaccountModelPreferencesImpl.class)
    private List<SubaccountModelPreferences> subaccounts;

    @ServiceElement(xpath = "//bp_list/bp/bp_head_list/bp_head/bp/val", converter = AccountKeyConverter.class)
    private AccountKey accountKey;

    public List<ModelPreference> getPreferences() {
        if (preferences != null)
            return Collections.unmodifiableList(preferences);
        return Collections.emptyList();
    }

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    @Override
    public List<SubaccountModelPreferences> getSubaccountPreferences() {
        if (subaccounts != null)
            return Collections.unmodifiableList(subaccounts);
        return Collections.emptyList();
    }
}
