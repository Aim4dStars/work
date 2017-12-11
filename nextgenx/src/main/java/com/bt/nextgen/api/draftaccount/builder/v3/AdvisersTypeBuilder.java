package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import ns.btfin_com.party.intermediary.v1_1.IntermediaryOrganisationType;
import ns.btfin_com.party.v3_0.IndividualPartyType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AdvisersType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.btfin.panorama.onboarding.helper.AdviserHelper.adviser;
import static com.btfin.panorama.onboarding.helper.AdviserHelper.adviserDetails;
import static com.btfin.panorama.onboarding.helper.AdviserHelper.advisers;
import static com.btfin.panorama.onboarding.helper.AdviserHelper.intermediaryDetails;
import static com.btfin.panorama.onboarding.helper.AdviserHelper.organisation;
import static org.apache.commons.lang.StringUtils.isBlank;
@Service
public class AdvisersTypeBuilder {

    @Autowired
    private AuthorityTypeBuilder authorityTypeBuilder;

    @Autowired
    private FeesBuilder feesBuilder;


    public AdvisersType getAdvisersType(IClientApplicationForm clientApplicationForm, BrokerUser brokerUser) {
        final IndividualPartyType intermediaryDetails = intermediaryDetails(brokerUser.getFirstName(),
                brokerUser.getLastName(), getEmail(brokerUser), getBusinessPhone(brokerUser));
        final IntermediaryOrganisationType organisation = organisation(getPositionId(brokerUser),
                authorityTypeBuilder.getAuthorityType(clientApplicationForm.getAccountSettings().getProfessionalsPayment()),
                feesBuilder.getFees(clientApplicationForm.getFees(),clientApplicationForm.getAccountType() ));
        return advisers(adviser(brokerUser.getBankReferenceId(), adviserDetails(intermediaryDetails, organisation)));
    }

    private String getPositionId(BrokerUser brokerUser) {
        for (BrokerRole role: brokerUser.getRoles()) {
            if (role.getRole().equals(JobRole.ADVISER)) {
                return role.getKey().getId();
            }
        }
        throw new IllegalArgumentException("BrokerUser does not have an adviser role");
    }

    private String getBusinessPhone(BrokerUser brokerUser) {
        for (Phone phone : brokerUser.getPhones()) {
            if (AddressMedium.BUSINESS_TELEPHONE.equals(phone.getType())) {
                return fullNumber(phone);
            }
        }
        return null;
    }

    private String fullNumber(Phone phone) {
        final String areaCode = phone.getAreaCode();
        return String.format("%s%s", isBlank(areaCode) ? "": areaCode, phone.getNumber());
    }

    private String getEmail(BrokerUser brokerUser) {
        for (Email email : brokerUser.getEmails()) {
            if (AddressMedium.EMAIL_PRIMARY.equals(email.getType())) {
                return email.getEmail();
            }
        }
        return brokerUser.getEmails().get(0).getEmail();
    }
}
