package com.bt.nextgen.api.draftaccount.model.form;

import java.util.List;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface ILinkedAccountsForm {
    public ILinkedAccountForm getPrimaryLinkedAccount();

    public List<ILinkedAccountForm> getOtherLinkedAccounts();


    /**
     * This method checks whether linkedAccount is optional based on the mandatory field
     * account number is populated or not.
     * @return
     */
    public boolean isEmpty();
}
