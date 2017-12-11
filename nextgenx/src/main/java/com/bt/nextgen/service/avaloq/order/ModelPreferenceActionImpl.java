package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.PreferenceAction;

public class ModelPreferenceActionImpl implements ModelPreferenceAction {

    private AccountKey issuerKey;

    private Preference preference;

    private PreferenceAction action;


    public ModelPreferenceActionImpl() {
    }

    public ModelPreferenceActionImpl(AccountKey issuerKey, Preference preference, PreferenceAction action) {
        this.issuerKey = issuerKey;
        this.preference = preference;
        this.action = action;
    }

    @Override
    public AccountKey getIssuerKey() {
        return issuerKey;
    }

    @Override
    public Preference getPreference() {
        return preference;
    }

    @Override
    public PreferenceAction getAction() {
        return action;
    }

}
