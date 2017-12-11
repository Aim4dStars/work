package com.bt.nextgen.service.integration.modelpreferences;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;

import java.util.List;

public interface ModelPreferenceIntegrationService {
    List<ModelPreference> getPreferencesForSubaccount(AccountKey accountKey, ServiceErrors serviceErrors);

    List<ModelPreference> updatePreferencesForSubaccount(AccountKey accountKey, List<ModelPreferenceAction> preferences,
            ServiceErrors serviceErrors);

    AccountModelPreferences getPreferencesForAccount(AccountKey accountKey, ServiceErrors serviceErrors);
}
