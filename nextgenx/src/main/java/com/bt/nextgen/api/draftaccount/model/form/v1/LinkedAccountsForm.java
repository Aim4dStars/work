package com.bt.nextgen.api.draftaccount.model.form.v1;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountForm;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.linkedaccounts.LinkedAccount;
import com.bt.nextgen.api.draftaccount.schemas.v1.linkedaccounts.LinkedAccountsApplication;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Implementation of the {@code ILinkedAccountsForm} interface.
 */
class LinkedAccountsForm implements ILinkedAccountsForm {

    private final LinkedAccountsApplication linkedAccountsApplication;

    public LinkedAccountsForm(LinkedAccountsApplication linkedAccounts) {
        this.linkedAccountsApplication = linkedAccounts;
    }

    @Override
    public ILinkedAccountForm getPrimaryLinkedAccount() {
        return new LinkedAccountForm(this.linkedAccountsApplication.getPrimaryLinkedAccount());
    }

    @Override
    public List<ILinkedAccountForm> getOtherLinkedAccounts() {
        return Lambda.convert(linkedAccountsApplication.getOtherLinkedAccount(), new Converter<LinkedAccount, ILinkedAccountForm>() {
            @Override
            public ILinkedAccountForm convert(LinkedAccount linkedAccount) {
                return new LinkedAccountForm(linkedAccount);
            }
        });
    }

    @Override
    public boolean isEmpty() {
        //As primary linked account and account number for primarylinked account is mandatory for accounts with non optional linked accounts.
        return linkedAccountsApplication == null || StringUtils.isEmpty(linkedAccountsApplication.getPrimaryLinkedAccount()) || StringUtils.isEmpty(linkedAccountsApplication.getPrimaryLinkedAccount().getAccountnumber());
    }
}
