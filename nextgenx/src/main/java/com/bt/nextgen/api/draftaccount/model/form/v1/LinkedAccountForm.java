package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.linkedaccounts.LinkedAccount;

/**
 * Implementation of the {@code ILinkedAccountForm} interface.
 */
class LinkedAccountForm implements ILinkedAccountForm {

    private final LinkedAccount linkedAccount;

    public LinkedAccountForm(LinkedAccount linkedAccount) {
        this.linkedAccount = linkedAccount;
    }

    @Override
    public String getNickName() {
        return this.linkedAccount.getNickname();
    }

    @Override
    public String getAccountNumber() {
        return this.linkedAccount.getAccountnumber();
    }

    @Override
    public String getAccountName() {
        return this.linkedAccount.getAccountname();
    }

    @Override
    public String getBsb() {
        return this.linkedAccount.getBsb();
    }

    @Override
    public String getDirectDebitAmount() {
        return this.linkedAccount.getDirectdebitamount();
    }

    @Override
    public boolean isAccountManuallyEntered() {
        return this.linkedAccount.getIsAccountManuallyEntered() != null ? this.linkedAccount.getIsAccountManuallyEntered() : false;
    }
}
