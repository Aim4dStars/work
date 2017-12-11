package com.bt.nextgen.util.matcher;

import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.domain.PersonDetail;

public class PersonDetailMatcher extends LambdaMatcher<PersonDetail> {

    private ClientKey clientKey;

    public PersonDetailMatcher(ClientKey clientKey) {
        this.clientKey = clientKey;
    }

    @Override
    protected boolean matchesSafely(PersonDetail person) {
        return person.getClientKey().equals(this.clientKey);
    }
}
