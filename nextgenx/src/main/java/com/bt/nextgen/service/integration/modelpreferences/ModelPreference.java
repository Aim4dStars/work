package com.bt.nextgen.service.integration.modelpreferences;

import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;

public interface ModelPreference
{
    AccountKey getIssuer();

    Preference getPreference();

    DateTime getEffectiveDate();

    DateTime getEndDate();
}
