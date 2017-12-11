package com.bt.nextgen.service.integration.modelpreferences;

import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

public interface SubaccountModelPreferences 
{
    AccountKey getAccountKey();
    List<ModelPreference> getPreferences();
}
