package com.bt.nextgen.api.authorisedfund.service;

import com.bt.nextgen.service.integration.account.AccountKey;

/**
 * Created by L067218 on 18/04/2016.
 */
public interface AuthorisedFundsDtoService {

    /**
     * Checks whether the account exists in the authorised fund list retrieved from ICC
     * @param accountKey accountKey
     * @return boolean value
     */
    public boolean isAccountAuthorised(AccountKey accountKey);
}
