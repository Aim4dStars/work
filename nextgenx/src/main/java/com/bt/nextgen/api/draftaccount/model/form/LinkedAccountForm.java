package com.bt.nextgen.api.draftaccount.model.form;


import java.util.Map;

/**
 * @deprecated Use the v1 version of this class instead.
 */
@Deprecated
class LinkedAccountForm implements ILinkedAccountForm{
    private final Map<String, String> linkedAccount;

    public LinkedAccountForm(Map<String, String> linkedAccount) {

        this.linkedAccount = linkedAccount;
    }

    public String getNickName() {
        return linkedAccount.get("nickname");
    }

    public String getAccountNumber() {
        return linkedAccount.get("accountnumber");
    }

    public String getAccountName() {
        return linkedAccount.get("accountname");
    }

    public String getBsb() {
        return linkedAccount.get("bsb");
    }

    public String getDirectDebitAmount() {
        return linkedAccount.get("directdebitamount");
    }

    @Override
    public boolean isAccountManuallyEntered() {
        String isAccountManuallyEntered = linkedAccount.get("isAccountManuallyEntered");
        return isAccountManuallyEntered != null ? Boolean.valueOf(isAccountManuallyEntered) : false;
    }
}
