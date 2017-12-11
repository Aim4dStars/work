package com.bt.nextgen.util.matcher;

import com.bt.nextgen.service.avaloq.account.AccountStatus;

import com.bt.nextgen.core.util.LambdaMatcher;
import com.btfin.panorama.service.integration.account.WrapAccount;

import java.util.Arrays;
import java.util.List;

public class AccountStatusMatcher extends LambdaMatcher<WrapAccount> {

    private List<AccountStatus> accountStatuslist = null;

    public AccountStatusMatcher(AccountStatus... accountStatus) {
        this.accountStatuslist = Arrays.asList(accountStatus);
    }

    @Override
    protected boolean matchesSafely(WrapAccount wrapAccount) {
        for(AccountStatus accountStatus : accountStatuslist)
        {
            if(wrapAccount.getAccountStatus()== accountStatus)
                return true;
        }
        return false;
    }
}
