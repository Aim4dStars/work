package com.bt.nextgen.service.integration.order;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.Preference;

public interface ModelPreferenceAction
{
    AccountKey getIssuerKey();

    Preference getPreference();

    PreferenceAction getAction();
}
