package com.bt.nextgen.util.matcher;

import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.integration.domain.InvestorRole;

public class NominatedInvestorRoleMatcher extends LambdaMatcher<InvestorRole> {

    @Override
    protected boolean matchesSafely(InvestorRole investorRole) {
        switch (investorRole) {
            case Shareholder:
            case Member:
            case Also_Member:
            case Beneficiary:
            case Account_Beneficiary:
            case BeneficialOwner:
            case ControllerOfTrust:
                return true;
            default:
                return false;
        }
    }
}
