package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.SubaccountModelPreferences;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "cont_head")
public class SubaccountModelPreferencesImpl implements SubaccountModelPreferences {

    @ServiceElementList(xpath = "cont_pref_list/cont_pref", type = ModelPreferenceImpl.class)
    private List<ModelPreference> preferences;

    @ServiceElement(xpath = "cont/val", converter = AccountKeyConverter.class)
    private AccountKey accountKey;

    public List<ModelPreference> getPreferences() {
        if (preferences != null) {
            return Collections.unmodifiableList(preferences);
        }
        return Collections.emptyList();
    }

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }
}
