package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.model.form.v1.LinkedAccountsFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.linkedaccounts.LinkedAccountsApplication;

import java.util.Map;

/**
 * Created by m040398 on 15/03/2016.
 */
public final class LinkedAccountsFormFactory {

    private LinkedAccountsFormFactory() {}

    public static  ILinkedAccountsForm getNewLinkedAccountsForm(Object linkedAccounts) {
        if(linkedAccounts instanceof Map) {
            return new LinkedAccountsForm((Map)linkedAccounts);
        }
        else if(linkedAccounts instanceof LinkedAccountsApplication){
            return LinkedAccountsFormFactoryV1.getLinkedAccountsForm((LinkedAccountsApplication)linkedAccounts);
        }
        else{
            throw new IllegalStateException("unknown linkedAccounts object: " + linkedAccounts);
        }
    }
}
