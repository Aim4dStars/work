package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfileType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorAuthorityProfilesType;
import org.springframework.stereotype.Service;
import static com.btfin.panorama.onboarding.helper.AuthorityHelper.authorities;
import static com.btfin.panorama.onboarding.helper.AuthorityHelper.clientAuthority;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.APPLICATION_MAINTENANCE;

@Service
public class AuthorityTypeBuilder {


    public InvestorAuthorityProfilesType getInvestorAuthorityProfilesType(PaymentAuthorityEnum accountSettings) {
        return authorities(getInvestorAuthorityProfileType(accountSettings), clientAuthority(APPLICATION_MAINTENANCE));
    }

    public InvestorAuthorityProfileType getInvestorAuthorityProfileType(PaymentAuthorityEnum accountSettings) {
        return clientAuthority(getAuthorityType(accountSettings));
    }

    public InvestorAuthorityProfileType getInvestorAuthorityProfileTypeForApplicationApproval() {
        return clientAuthority(AuthorityTypeType.APPLICATION_APPROVAL);
    }

    public AuthorityTypeType getAuthorityType(PaymentAuthorityEnum accountSettings) {
        switch (accountSettings) {
            case NOPAYMENTS:
                return AuthorityTypeType.LIMITED_CHANGE;
            case LINKEDACCOUNTSONLY:
                return AuthorityTypeType.LIMITED_TRANSACTION;
            default:
                return AuthorityTypeType.FULL_TRANSACTION;
        }
    }
}
