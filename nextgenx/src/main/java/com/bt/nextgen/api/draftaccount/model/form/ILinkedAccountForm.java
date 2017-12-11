package com.bt.nextgen.api.draftaccount.model.form;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface ILinkedAccountForm {
    public String getNickName();

    public String getAccountNumber();

    public String getAccountName();

    public String getBsb();

    public String getDirectDebitAmount();

    public boolean isAccountManuallyEntered();

}
