package com.bt.nextgen.util.matcher;

import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.avaloq.client.ClientIdentifier;
import com.btfin.panorama.service.integration.account.WrapAccount;

public class AccountOwnerMatcher extends LambdaMatcher<WrapAccount> {
    private final ClientIdentifier personId;

    public AccountOwnerMatcher(ClientIdentifier personId) {
        this.personId = personId;
    }

    @Override
    protected boolean matchesSafely(WrapAccount account) {
        return account.getOwnerClientKeys().contains(personId);
    }
}
