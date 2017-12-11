package com.bt.nextgen.service.avaloq.transfer.transfergroup;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.PreferenceAction;

@ServiceBean(xpath = "mp_pref_item")
public class ModelPreferenceImpl implements ModelPreferenceAction {

    @ServiceElement(xpath = "issuer_id/val", converter = AccountKeyConverter.class)
    private AccountKey issuerKey;

    @ServiceElement(xpath = "pref_type_id/val", staticCodeCategory = "PREFERENCE_TYPE")
    private Preference preference;

    @ServiceElement(xpath = "pref_action_id/val", staticCodeCategory = "PREFERENCE_ACTION")
    private PreferenceAction action;

    public ModelPreferenceImpl() {
        super();
    }

    public ModelPreferenceImpl(AccountKey issuer, Preference preference, PreferenceAction action) {
        this.issuerKey = issuer;
        this.preference = preference;
        this.action = action;
    }

    @Override
    public Preference getPreference() {
        return preference;
    }

    @Override
    public AccountKey getIssuerKey() {
        return issuerKey;
    }

    @Override
    public PreferenceAction getAction() {
        return action;
    }

    public void setIssuerKey(AccountKey issuerKey) {
        this.issuerKey = issuerKey;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public void setAction(PreferenceAction action) {
        this.action = action;
    }
}
