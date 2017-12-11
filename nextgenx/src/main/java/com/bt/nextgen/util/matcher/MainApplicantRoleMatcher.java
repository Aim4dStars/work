package com.bt.nextgen.util.matcher;

import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.integration.domain.InvestorRole;

public class MainApplicantRoleMatcher extends LambdaMatcher<InvestorRole> {

    @Override
    protected boolean matchesSafely(InvestorRole investorRole) {
        switch (investorRole) {
            case Director:
            case Trustee:
            case Signatory:
            case Secretary:
            case Owner:
            case Primary_Contact:
                return true;
            default:
                return false;
        }
    }
}
