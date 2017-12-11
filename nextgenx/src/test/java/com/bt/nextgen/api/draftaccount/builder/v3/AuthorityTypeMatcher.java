package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfileType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

public class AuthorityTypeMatcher {

    public static Matcher<? super List<InvestorAuthorityProfileType>> hasAuthorityType(final AuthorityTypeType authorityTypeType) {
        return new TypeSafeMatcher<List<InvestorAuthorityProfileType>>() {

            private List<InvestorAuthorityProfileType> actualInvestorAuthorityProfileTypes;

            @Override
            protected boolean matchesSafely(List<InvestorAuthorityProfileType> investorAuthorityProfileTypes) {
                for (InvestorAuthorityProfileType investorAuthorityProfileType : investorAuthorityProfileTypes) {
                    if (authorityTypeType.equals(investorAuthorityProfileType.getAuthorityType())) {
                        return true;
                    }
                }
                actualInvestorAuthorityProfileTypes = investorAuthorityProfileTypes;
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(authorityTypeType.value()).appendText("But was [");
                for (InvestorAuthorityProfileType actualInvestorAuthorityProfileType : actualInvestorAuthorityProfileTypes) {
                    description.appendText(actualInvestorAuthorityProfileType.getAuthorityType().value()).appendText(",");
                }
                description.appendText("]");
            }
        };
    }

}

