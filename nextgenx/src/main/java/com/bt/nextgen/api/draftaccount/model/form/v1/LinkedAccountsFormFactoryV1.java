package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.linkedaccounts.LinkedAccountsApplication;

/**
 * Factory class to access com.bt.nextgen.api.draftaccount.model.form.v1.LinkedAccountsForm
 */
public class LinkedAccountsFormFactoryV1 {

    private LinkedAccountsFormFactoryV1(){
    }

    public static ILinkedAccountsForm getLinkedAccountsForm(LinkedAccountsApplication linkedAccountsApplication){
        return new LinkedAccountsForm(linkedAccountsApplication);
    }
}
