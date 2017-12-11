package com.bt.nextgen.api.draftaccount.builder.v3;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.util.EmailFilterUtil;
import com.bt.nextgen.api.client.util.PhoneFilterUtil;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPlaceOfBirth;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrusteeDetailsForm;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerIdentifiers;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType;
import ns.btfin_com.party.v3_0.PartyDetailType;
import ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType;
import ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberUsageTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressesType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailUsageTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.builder.v3.ContactsTypeBuilder.standardContactNumber;
import static com.bt.nextgen.api.draftaccount.model.form.ITrustForm.TrustType.REGISTERED_MIS;
import static com.btfin.panorama.core.util.StringUtil.nullIfBlank;
import static com.btfin.panorama.onboarding.helper.AuthorityHelper.clientAuthority;
import static com.btfin.panorama.onboarding.helper.ContactHelper.contact;
import static com.btfin.panorama.onboarding.helper.ContactHelper.contacts;
import static com.btfin.panorama.onboarding.helper.EmailHelper.emailAddress;
import static com.btfin.panorama.onboarding.helper.EmailHelper.emailAddresses;
import static com.btfin.panorama.onboarding.helper.InvestmentAccountHelper.existingInvestorDetails;
import static com.btfin.panorama.onboarding.helper.InvestorHelper.investor;
import static com.btfin.panorama.onboarding.helper.PartyHelper.customerIdentifiers;
import static com.btfin.panorama.onboarding.helper.PartyHelper.existingIndividual;
import static com.btfin.panorama.onboarding.helper.PartyHelper.party;
import static ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType.APPLICATION_APPROVAL;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType.SECONDARY_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE;

@Service
@SuppressWarnings("squid:S1200")
public class ExistingInvestorTypeBuilder {

    @Autowired
    private AuthorityTypeBuilder authorityTypeBuilder;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private ExistingCustomerIdentifiersBuilder existingCustomerIdentifiersBuilder;

    @Autowired
    private AddressTypeBuilder addressTypeBuilder;

    @Autowired
    private TaxFieldsBuilder taxFieldsBuilder;

    @Autowired
    private FeatureTogglesService featureTogglesService;


    private static final Logger LOGGER = LoggerFactory.getLogger(ExistingInvestorTypeBuilder.class);


    public InvestorType getInvestorType(IExtendedPersonDetailsForm investor, IClientApplicationForm.AccountType accountType) {
        return getExistingInvestorType(investor, true, accountType);
    }

    public InvestorType getDirectorType(IDirectorDetailsForm director, IClientApplicationForm.AccountType accountType) {
        return getExistingInvestorTypeForRole(director, director.getRole(), accountType);
    }

    public InvestorType getSmsfTrusteeType(ITrusteeDetailsForm trustee, IClientApplicationForm.AccountType accountType) {
        return getExistingInvestorTypeForRole(trustee, IOrganisationForm.OrganisationRole.TRUSTEE, accountType);
    }

    public InvestorType getTrusteeType(ITrusteeDetailsForm trustee, IClientApplicationForm.AccountType accountType, ITrustForm.TrustType trustType) {
        return getExistingTrusteeInvestorType(trustee, IOrganisationForm.OrganisationRole.TRUSTEE, accountType, trustType);
    }

    private InvestorType getExistingInvestorTypeForRole(IExtendedPersonDetailsForm person, IOrganisationForm.OrganisationRole role, IClientApplicationForm.AccountType accountType) {
        InvestorType investorType = getExistingInvestorTypeForOrganisation(person, person.isApprover());
        String partyRoleValue = role.getJsonValue() + "Role";
        switch(role) {
            case DIRECTOR:
            case SECRETARY:
            case SIGNATORY:
                partyRoleValue = "Company" + partyRoleValue;
                break;
            case TRUSTEE:
                switch(accountType) {
                    case CORPORATE_TRUST:
                    case INDIVIDUAL_TRUST:
                        partyRoleValue = "Trust" + partyRoleValue;
                        break;
                    case INDIVIDUAL_SMSF:
                    case NEW_INDIVIDUAL_SMSF:
                    case NEW_CORPORATE_SMSF:
                    case CORPORATE_SMSF:
                        partyRoleValue = "SMSF" + partyRoleValue;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        PartyRoleInRelatedOrganisationType partyRole = PartyRoleInRelatedOrganisationType.fromValue(partyRoleValue);
        investorType.getPartyRoleInRelatedOrganisation().add(partyRole);
        addAdditionalRoles(person, investorType, accountType);
        return investorType;
    }

    private InvestorType getExistingTrusteeInvestorType(IExtendedPersonDetailsForm person, IOrganisationForm.OrganisationRole role, IClientApplicationForm.AccountType accountType, ITrustForm.TrustType trustType) {
        InvestorType investorType = getExistingInvestorTypeForOrganisation(person, person.isApprover());
        List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        if(!REGISTERED_MIS.equals(trustType)) {
            String partyRoleValue = "Trust" + role.getJsonValue() + "Role";
            PartyRoleInRelatedOrganisationType partyRole = PartyRoleInRelatedOrganisationType.fromValue(partyRoleValue);
            roles.add(partyRole);
        }
        else{
            roles.add(PartyRoleInRelatedOrganisationType.TRUST_RESPONSIBLE_ENTITY_ROLE);
        }
        if (!roles.contains(PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE)) {
            roles.add(PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE);
        }
        addAdditionalRoles(person, investorType, accountType);
        return investorType;
    }

    private void addAdditionalRoles(IExtendedPersonDetailsForm person, InvestorType investorType, IClientApplicationForm.AccountType accountType) {
        List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        if (person.isMember()) {
            roles.add(PartyRoleInRelatedOrganisationType.SMSF_MEMBER_ROLE);
        }

        if(person.isShareholder()) {
            switch (accountType) {
                case NEW_CORPORATE_SMSF:
                    roles.add(PartyRoleInRelatedOrganisationType.COMPANY_SHAREHOLDER_ROLE);
                    break;
                default:
                    roles.add(PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE);
                    break;
            }
        }

        if (person.isBeneficiary()) {
            roles.add(PartyRoleInRelatedOrganisationType.TRUST_BENEFICIARY_ROLE);
        }

        if(person.isBeneficialOwner()){
            switch(accountType){
                case INDIVIDUAL_TRUST:
                case CORPORATE_TRUST:
                    if(!roles.contains(COMPANY_BENEFICIAL_OWNER_ROLE)) {
                        roles.add(PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE);
                    }
                    break;
                default:
                    roles.add(PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE);
                    break;
            }
        }

        if (person.isControllerOfTrust()) { //this is used only by corporate trust
            roles.add(PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE);
        }
        if(person instanceof IDirectorDetailsForm)     {
            IDirectorDetailsForm director = (IDirectorDetailsForm)person;
            if(director.isCompanySecretary()){
                roles.add(PartyRoleInRelatedOrganisationType.COMPANY_SECRETARY_ROLE);
            }
        }
    }

    private InvestorType getExistingInvestorTypeForOrganisation(IExtendedPersonDetailsForm investor, boolean accountApprover) {
        InvestorType investorType = createExistingInvestorTypeWithAccountApproverInfo(investor, accountApprover);
        investorType.getInvestmentAccountPartyRole().add(ACCOUNT_SERVICER_ROLE);
        return investorType;
    }

    private InvestorType createExistingInvestorTypeWithAccountApproverInfo(IExtendedPersonDetailsForm investor, boolean accountApprover) {
        InvestorType investorType = investor(getInvolvedPartyDetailsType(investor));
        investorType.setAuthorityProfiles(authorityTypeBuilder.getInvestorAuthorityProfilesType(investor.getPaymentSetting()));

        boolean retrieveCrsEnabled = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("retrieveCrsEnabled");
        if(retrieveCrsEnabled) {
            taxFieldsBuilder.populateCrsTax(investorType.getInvestorDetails(), investor, true);
        } else {
            taxFieldsBuilder.populateTaxRelatedFields(investorType.getInvestorDetails(),investor, true);
        }


        if (accountApprover) {
            setAsAccountApprover(investorType);
        }
        if(investor.isPrimaryContact()){
            investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.CONTACT_PERSON_ROLE);
        }
        return investorType;
    }

    private InvestorType getExistingInvestorType(IExtendedPersonDetailsForm investor, boolean accountApprover, IClientApplicationForm.AccountType accountType) {
        InvestorType investorType = createExistingInvestorTypeWithAccountApproverInfo(investor, accountApprover);
        addAccountPartyRoleType(investorType, investor, accountType);
        return investorType;
    }

    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1142"})
    private void addAccountPartyRoleType(InvestorType investorType, IExtendedPersonDetailsForm investor, IClientApplicationForm.AccountType accountType) {
        switch (accountType){
            case INDIVIDUAL:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                investorType.getInvestmentAccountPartyRole().add(PRIMARY_OWNER_ROLE);
                break;
            case JOINT:
                investorType.getInvestmentAccountPartyRole().add(investor.isPrimaryContact() ? PRIMARY_OWNER_ROLE : SECONDARY_OWNER_ROLE);
                break;
            case CORPORATE_TRUST:
            case INDIVIDUAL_TRUST:
            case CORPORATE_SMSF:
            case INDIVIDUAL_SMSF:
            case COMPANY:
                investorType.getInvestmentAccountPartyRole().add(ACCOUNT_SERVICER_ROLE);
                break;
            default:
        }
    }

    private InvolvedPartyDetailsType getInvolvedPartyDetailsType(IExtendedPersonDetailsForm investor) {
        final InvestorDetail investorDetail = (InvestorDetail) clientIntegrationService.loadClientDetails(ClientKey.valueOf(EncodedString.toPlainText(investor.getClientKey())),
                new ServiceErrorsImpl());
        //Tactical solution for june release to set Brand Silo - US29203
        setBrandSiloFromPersonDetCallForESBIntercep(investorDetail);

        final PartyDetailType partyDetail = getPartyDetail(investorDetail, investor);
        if (partyDetail.getIndividual().getResidentialAddress() == null){
            throw new IllegalStateException("Client(gcmId="+investorDetail.getGcmId()+ ") must have a residential address");
        }
        final InvolvedPartyDetailsType investorDetails = existingInvestorDetails(partyDetail, existingCustomerIdentifiers(investorDetail));

        final Email primaryEmail = new EmailFilterUtil().getPrimaryEmail(investorDetail.getEmails());
        if (primaryEmail == null) {
            throw new IllegalStateException("Client(gcmId="+investorDetail.getGcmId()+ ") must have a primary email address");
        }
        investorDetails.setEmailAddresses(getEmailAddress(primaryEmail.getEmail()));

        final Phone primaryMobile = new PhoneFilterUtil().getPrimaryMobile(investorDetail.getPhones());
        if (primaryMobile == null) {
            throw new IllegalStateException("Client(gcmId="+investorDetail.getGcmId()+ ") must have a primary mobile phone number");
        }
        investorDetails.setContacts(getMobilePhoneContact(primaryMobile.getNumber()));

        return investorDetails;
    }

    private void setBrandSiloFromPersonDetCallForESBIntercep(InvestorDetail investorDetail) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO) == null) {
            request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, investorDetail.getBrandSiloId());
        }
    }

    private CustomerIdentifiers existingCustomerIdentifiers(InvestorDetail investorDetail) {
        final Collection<CustomerIdentifier> existingCustomerIdentifiers = new ArrayList<>();
        existingCustomerIdentifiers.add(existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithGcmId(investorDetail.getGcmId()));

        if (investorDetail.getCISKey() != null) {
            existingCustomerIdentifiers.add(existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithCisKey(investorDetail.getCISKey().getId()));
        } else {
            LOGGER.error("Client(gcmId="+investorDetail.getGcmId()+ "): missing cis key");
            throw new IllegalStateException();
        }

        if (investorDetail.getWestpacCustomerNumber() != null) {
            existingCustomerIdentifiers.add(existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithCustomerNumber(investorDetail.getWestpacCustomerNumber()));
        } else {
            LOGGER.error("Client(gcmId="+investorDetail.getGcmId()+ "): missing customer number");
            throw new IllegalStateException();
        }
        return customerIdentifiers(existingCustomerIdentifiers);
    }

    private ContactsType getMobilePhoneContact(String mobile) {
        String formattedMobile = mobile;
        if(mobile.startsWith("61")){
            formattedMobile = "+" + mobile;
        }
        return contacts(contact(null, standardContactNumber(formattedMobile), ContactNumberTypeCode.MOBILE, ContactNumberUsageTypeCode.ALL_HOURS));
    }

    private EmailAddressesType getEmailAddress(String emailAddress) {
        return emailAddresses(emailAddress(null, emailAddress, EmailUsageTypeCode.ALL_HOURS));
    }

    private PartyDetailType getPartyDetail(InvestorDetail investor, IExtendedPersonDetailsForm personForm) {
        final IndividualType individual = existingIndividual(investor.getFirstName(), investor.getLastName(), personForm.getPreferredName(), personForm.getAlternateName());
        individual.setResidentialAddress(getResidentialAddress(investor.getAddresses()));
        individual.setFormerName(nullIfBlank(personForm.getFormerName()));
        final IPlaceOfBirth placeOfBirth = personForm.getPlaceOfBirth();
        if (placeOfBirth != null) {
            individual.setCountryOfBirth(placeOfBirth.getCountryOfBirth());
            individual.setStateOfBirth(placeOfBirth.getStateOfBirth());
            individual.setCityOfBirth(placeOfBirth.getCityOfBirth());
        }
        return party(individual);
    }

    private RegisteredResidentialAddressDetailType getResidentialAddress(List<Address> addresses) {
        Address residentialAddress = Lambda.selectFirst(addresses, new LambdaMatcher<Address>() {
            @Override
            protected boolean matchesSafely(Address address) {
                return address.isDomicile();
            }
        });
        if (residentialAddress != null) {
            return addressTypeBuilder.getAddressType(
                    residentialAddress,
                    new RegisteredResidentialAddressDetailType()
            );
        }
        return null;
    }

    protected void setAsAccountApprover(InvestorType existingInvestorType) {
        existingInvestorType.getAuthorityProfiles().getAuthorityProfile().add(clientAuthority(APPLICATION_APPROVAL));
    }
}
