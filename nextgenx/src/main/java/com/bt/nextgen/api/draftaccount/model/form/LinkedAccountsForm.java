package com.bt.nextgen.api.draftaccount.model.form;


import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

import java.util.List;
import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class LinkedAccountsForm implements ILinkedAccountsForm{

    private final Map<String, Object> linkedAccounts;

    public LinkedAccountsForm(Map<String, Object> linkedAccounts) {
        this.linkedAccounts = linkedAccounts;
    }

    public LinkedAccountForm getPrimaryLinkedAccount() {
        return new LinkedAccountForm((Map<String, String>) linkedAccounts.get("primaryLinkedAccount"));
    }

    public List<ILinkedAccountForm> getOtherLinkedAccounts() {
        return Lambda.convert(linkedAccounts.get("otherLinkedAccount"), new Converter<Object, ILinkedAccountForm>() {
            @Override
            public ILinkedAccountForm convert(Object accountMap) {
                return new LinkedAccountForm((Map<String, String>) accountMap);
            }
        });
    }

    /**
     * This method will not be used and this class will soon be removed
     * @return
     */
    @Override
    public boolean isEmpty() {
        return false;
    }
}
