package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import org.joda.time.DateTime;

@ServiceBean(xpath = "*")
public class ModelPreferenceImpl implements ModelPreference {

    @ServiceElement(xpath = "issuer/val", converter = AccountKeyConverter.class)
    private AccountKey issuer;

    @ServiceElement(xpath = "pref_type/val", staticCodeCategory = "PREFERENCE_TYPE")
    private Preference preference;

    @ServiceElement(xpath = "valid_from/val", converter = DateTimeTypeConverter.class)
    private DateTime effectiveDate;

    @ServiceElement(xpath = "valid_to/val", converter = DateTimeTypeConverter.class)
    private DateTime endDate;

    public ModelPreferenceImpl() {
    }

    public ModelPreferenceImpl(AccountKey issuer, Preference preference, DateTime effectiveDate) {
        this.issuer = issuer;
        this.preference = preference;
        this.effectiveDate = effectiveDate;
    }

    @Override
    public AccountKey getIssuer() {
        return issuer;
    }

    @Override
    public Preference getPreference() {
        return preference;
    }

    @Override
    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }
}
