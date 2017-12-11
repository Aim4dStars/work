package com.bt.nextgen.api.draftaccount.builder.v3;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.ICompanyForm;
import com.bt.nextgen.api.draftaccount.model.form.ICrsTaxDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IDirectorDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.model.form.ISmsfForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm;
import com.bt.nextgen.api.draftaccount.model.form.ITrustForm.StandardTrustDescription;
import com.bt.nextgen.api.draftaccount.model.form.ITrusteeDetailsForm;
import com.bt.nextgen.core.exception.ApplicationConfigurationException;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import ns.btfin_com.party.v3_0.InvestmentAccountPartyRoleTypeType;
import ns.btfin_com.party.v3_0.OrganisationType;
import ns.btfin_com.party.v3_0.PartyDetailType;
import ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType;
import ns.btfin_com.party.v3_0.SettlorOfTrustPartyDetailType;
import ns.btfin_com.party.v3_0.TFNRegistrationType;
import ns.btfin_com.party.v3_0.TrustDetailsType;
import ns.btfin_com.party.v3_0.TrustTypeType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvestorsType;
import ns.btfin_com.product.common.investmentaccount.v2_0.InvolvedPartyDetailsType;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressType;
import ns.btfin_com.sharedservices.common.address.v3_0.AddressesType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.ITrustForm.SettlorofTrustType.INDIVIDUAL;
import static com.bt.nextgen.api.draftaccount.model.form.ITrustForm.SettlorofTrustType.ORGANISATION;
import static com.bt.nextgen.api.draftaccount.model.form.ITrustForm.TrustType.FAMILY;
import static com.bt.nextgen.api.draftaccount.model.form.ITrustForm.TrustType.REGISTERED_MIS;
import static com.btfin.panorama.core.util.StringUtil.nullIfBlank;
import static com.btfin.panorama.onboarding.helper.AddressHelper.addresses;
import static com.btfin.panorama.onboarding.helper.InvestorHelper.investor;
import static com.btfin.panorama.onboarding.helper.InvestorHelper.investors;
import static com.btfin.panorama.onboarding.helper.PartyHelper.individualSettlor;
import static com.btfin.panorama.onboarding.helper.PartyHelper.investmentScheme;
import static com.btfin.panorama.onboarding.helper.PartyHelper.organisationSettlor;
import static com.btfin.panorama.onboarding.helper.PartyHelper.party;
import static com.btfin.panorama.onboarding.helper.PartyHelper.regulatedTrust;
import static com.btfin.panorama.onboarding.helper.PartyHelper.standardTrust;
import static com.btfin.panorama.onboarding.helper.PartyHelper.superannuationFund;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_DIRECTOR_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SECRETARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SHAREHOLDER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.COMPANY_SIGNATORY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_MEMBER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.SMSF_TRUSTEE_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIAL_OWNER_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_BENEFICIARY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_RESPONSIBLE_ENTITY_ROLE;
import static ns.btfin_com.party.v3_0.PartyRoleInRelatedOrganisationType.TRUST_TRUSTEE_ROLE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@SuppressWarnings({"squid:S1200", "findbugs:BC_UNCONFIRMED_CAST"})
@Service
public class InvestorsTypeBuilder {

    // =======================================================================
    // Instance members
    // =======================================================================

    @Autowired
    private AddressTypeBuilder addressTypeBuilder;

    @Autowired
    private AuthorityTypeBuilder authorityTypeBuilder;

    @Autowired
    private ContactDetailsBuilder contactDetailsBuilder;

    @Autowired
    @Qualifier("customerIdentifiersBuilder")
    private CustomerIdentifiersBuilder customerIdentifiersBuilder;

    @Autowired
    private IndividualTypeBuilder individualTypeBuilder;

    @Autowired
    private OrganisationTypeBuilder organisationTypeBuilder;

    @Autowired
    private TaxFieldsBuilder taxFieldsBuilder;

    @Autowired
    private ExistingInvestorTypeBuilder existingInvestorTypeBuilder;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    private AddressV2CacheService addressV2CacheService;

    // =======================================================================
    // Utility methods
    // =======================================================================

    protected void setAsicName(OrganisationType organisation, String asicName) {
        organisation.setASICName(asicName);
    }

    private InvestorType getCompanyTrustee(IClientApplicationForm form, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        ICompanyForm companyForm = form.getCompanyTrustee();

        InvolvedPartyDetailsType partyDetails = getCompanyTrusteePartyDetails(companyForm, serviceErrors);
        partyDetails.setCorrelationSequenceNumber(companyForm.getCorrelationSequenceNumber().toString());
        if (isNotBlank(companyForm.getCisKey())) {
            partyDetails.setCustomerIdentifiers(customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKey(companyForm.getCisKey()));
        }

        OrganisationType organisation = organisationTypeBuilder.getCompanyAsTrustee(getMainEntityForm(form), companyForm, form.getAccountSettings(), adviser, dealer, serviceErrors);
        setAsicName(organisation, companyForm.getAsicName());
        InvestorType investor = getOrganisationInvestorType(partyDetails, organisation);
        investor.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE);

        final List<PartyRoleInRelatedOrganisationType> roles = investor.getPartyRoleInRelatedOrganisation();
        switch(form.getAccountType()) {
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                roles.add(SMSF_TRUSTEE_ROLE);
                break;
            case CORPORATE_TRUST:
                if (REGISTERED_MIS == form.getTrust().getTrustType()) {
                    roles.add(TRUST_RESPONSIBLE_ENTITY_ROLE);
                    break;
                } //fall through in case <> REGISTERED_MIS
            case INDIVIDUAL_TRUST:
                roles.add(TRUST_TRUSTEE_ROLE);
                break;
            default:
                break;
        }
        return investor;
    }

    private IOrganisationForm getMainEntityForm(IClientApplicationForm form) {
        IClientApplicationForm.AccountType accountType = form.getAccountType();
        switch (accountType){
            case CORPORATE_TRUST:
                return form.getTrust();
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
                return form.getSmsf();
            default:
                throw new ApplicationConfigurationException("Invalid account type");
        }
    }

    public InvestorType getCompanyDetails(IClientApplicationForm form, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        ICompanyForm companyForm = form.getCompanyDetails();

        InvolvedPartyDetailsType partyDetails = getCompanyDetailsPartyDetails(companyForm, serviceErrors);
        partyDetails.setCorrelationSequenceNumber(companyForm.getCorrelationSequenceNumber().toString());
        if (isNotBlank(companyForm.getCisKey())) {
            partyDetails.setCustomerIdentifiers(customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKey(companyForm.getCisKey()));
        }

        OrganisationType organisation = organisationTypeBuilder.getOrganisation(companyForm, form.getAccountSettings(), adviser, dealer, serviceErrors);
        setAsicName(organisation, companyForm.getAsicName());
        InvestorType investor = getOrganisationInvestorType(partyDetails, organisation);
        investor.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE);
        return investor;
    }

    private InvestorType getSmsf(IClientApplicationForm form, TrustDetailsType trustDetails, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        ISmsfForm smsfForm = form.getSmsf();

        InvolvedPartyDetailsType partyDetails = getOrganisationPartyDetails(smsfForm, serviceErrors);
        partyDetails.setCorrelationSequenceNumber(smsfForm.getCorrelationSequenceNumber().toString());
        OrganisationType organisation = organisationTypeBuilder.getOrganisation(smsfForm, form.getAccountSettings(), adviser, dealer, serviceErrors);

        organisation.setTrustDetails(trustDetails);

        if (isNotBlank(smsfForm.getCisKey())) {
            partyDetails.setCustomerIdentifiers(customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKey(smsfForm.getCisKey()));
        }

        InvestorType investor = getOrganisationInvestorType(partyDetails, organisation);
         investor.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE);
        return investor;
    }

    private InvestorType getTrust(IClientApplicationForm form, TrustDetailsType trustDetails, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        ITrustForm trustForm = form.getTrust();

        InvolvedPartyDetailsType partyDetails = getOrganisationPartyDetails(trustForm, serviceErrors);
        partyDetails.setCorrelationSequenceNumber(trustForm.getCorrelationSequenceNumber().toString());
        OrganisationType organisation = organisationTypeBuilder.getOrganisation(trustForm, form.getAccountSettings(), adviser, dealer, serviceErrors);
        organisation.setTrustDetails(trustDetails);

        if (isNotBlank(trustForm.getCisKey())) {
            partyDetails.setCustomerIdentifiers(customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKey(trustForm.getCisKey()));
        }

        setAsicName(organisation, trustForm.getBusinessName());

        InvestorType investor = getOrganisationInvestorType(partyDetails, organisation);
        investor.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE);
        return investor;
    }

    protected TrustDetailsType getTrustDetails(IOrganisationForm organisationForm, IShareholderAndMembersForm shareholderAndMembers, TrustTypeType trustType) {
        TrustDetailsType trustDetail = new TrustDetailsType();
        XMLGregorianCalendar dateOfRegistration = organisationForm.getDateOfRegistration();
        if (dateOfRegistration != null) {
        	trustDetail.setTrustRegisteredDate(organisationForm.getDateOfRegistration());
        }

        if (StringUtils.isNotBlank(organisationForm.getRegistrationState())) {
            trustDetail.setTrustRegisteredState(organisationForm.getRegistrationState());
        }

        if (shareholderAndMembers.hasbeneficiaryClasses()) {
            trustDetail.setTrustMembershipClassDetails(shareholderAndMembers.getBeneficiaryClassDetails());
        }
        trustDetail.setTrustType(trustType);
        return trustDetail;
    }

    private InvestorType getOrganisationInvestorType(InvolvedPartyDetailsType partyDetails, OrganisationType organisation) {
        partyDetails.setPartyDetails(party(organisation));
        return investor(partyDetails);
    }

    private InvolvedPartyDetailsType getCompanyTrusteePartyDetails(ICompanyForm form, ServiceErrors serviceErrors) {
        InvolvedPartyDetailsType partyDetails = getBasicCompanyPartyDetails(form, serviceErrors);
        addTaxDetails(partyDetails, form.getCrsTaxDetails(), serviceErrors);
        return partyDetails;
    }

    private InvolvedPartyDetailsType getCompanyDetailsPartyDetails(ICompanyForm form, ServiceErrors serviceErrors) {
        InvolvedPartyDetailsType partyDetails = getBasicCompanyPartyDetails(form, serviceErrors);
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(partyDetails, form.getCrsTaxDetails());
        return partyDetails;
    }

    private InvolvedPartyDetailsType getBasicCompanyPartyDetails(ICompanyForm form, ServiceErrors serviceErrors){
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        partyDetails.setPostalAddresses(addresses(
                addressTypeBuilder.getDefaultAddressType(
                        form.getPlaceOfBusinessAddress(),
                        new AddressType(),
                        serviceErrors
                )
        ));
        return partyDetails;
    }

    private InvolvedPartyDetailsType getOrganisationPartyDetails(IOrganisationForm form, ServiceErrors serviceErrors) {
        InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        partyDetails.setPostalAddresses(addresses(
                addressTypeBuilder.getDefaultAddressType(
                        form.getRegisteredAddress(),
                        new AddressType(),
                        serviceErrors)
        ));
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(partyDetails, form.getCrsTaxDetails());
        return partyDetails;
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public InvestorsType getInvestorsType(IClientApplicationForm form, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final InvestorsType investors;
        final IClientApplicationForm.AccountType accountType = form.getAccountType();
        switch (accountType) {
            case INDIVIDUAL:
            case JOINT:
            case SUPER_PENSION:
            case SUPER_ACCUMULATION:
                investors = individualsInvestorType(form, accountType, adviser, dealer, serviceErrors);
                break;

            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
                investors = corporateSmsfInvestorsType(form, accountType, adviser, dealer, serviceErrors);
                break;

            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                investors = individualSmsfInvestorsType(form, accountType, adviser, dealer, serviceErrors);
                break;

            case CORPORATE_TRUST:
                investors = corporateTrustInvestorsType(form, accountType, adviser, dealer, serviceErrors);
                break;

            case INDIVIDUAL_TRUST:
                investors = individualTrustInvestorsType(form, accountType, adviser, dealer, serviceErrors);
                break;

            case COMPANY:
                investors = companyInvestorsType(form, accountType, adviser, dealer, serviceErrors);
                break;

            default:
                throw new UnsupportedOperationException("Don't know how to get investors type for " + accountType.value());
        }
        return investors;
    }

    private InvestorsType companyInvestorsType(IClientApplicationForm form, IClientApplicationForm.AccountType accountType,
                                               BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final List<IExtendedPersonDetailsForm> directorsSecretariesSignatories = form.getDirectorsSecretariesSignatories();
        final List<InvestorType> newInvestors = new ArrayList<>();
        newInvestors.addAll(getNewDirectors(getNewPersons(directorsSecretariesSignatories), accountType, adviser, dealer,
            form.getAccountSettings(), serviceErrors));
        newInvestors.add(getCompanyDetails(form, adviser, dealer, serviceErrors));
        newInvestors.addAll(getAllAdditionalPersons(form, adviser, dealer, serviceErrors));
        return investors(newInvestors, getExistingDirectors(getExistingPersons(directorsSecretariesSignatories), accountType));
    }

    private InvestorsType individualTrustInvestorsType(IClientApplicationForm form, IClientApplicationForm.AccountType accountType,
                                                       BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final List<IExtendedPersonDetailsForm> trustees = form.getTrustees();
        final ITrustForm individualTrustForm = form.getTrust();
        final ITrustForm.TrustType trustType = individualTrustForm.getTrustType();
        final List<InvestorType> newInvestors = new ArrayList<>();
        newInvestors.add(getTrust(form, getTrustDetails(individualTrustForm, form.getShareholderAndMembers(), getTrustType(individualTrustForm)), adviser, dealer, serviceErrors));
        newInvestors.addAll(getNewTrustees(getNewPersons(trustees), accountType, adviser, dealer, trustType, form.getAccountSettings(),
            serviceErrors));
        newInvestors.addAll(getAllAdditionalPersons(form, adviser, dealer, serviceErrors));
        return investors(newInvestors, getExistingTrustees(getExistingPersons(trustees), accountType, trustType));
    }

    private InvestorsType individualsInvestorType(IClientApplicationForm form, IClientApplicationForm.AccountType accountType,
                                                  BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final List<IExtendedPersonDetailsForm> allInvestors = form.getInvestors();
        return investors(
                getAllNewInvestors(getNewPersons(allInvestors), accountType, adviser, dealer, form.getAccountSettings(), serviceErrors),
                getAllExistingInvestors(getExistingPersons(allInvestors), accountType));
    }

    private InvestorsType corporateSmsfInvestorsType(IClientApplicationForm form, IClientApplicationForm.AccountType accountType,
                                                     BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final List<IExtendedPersonDetailsForm> allDirectors = form.getDirectors();
        final List<InvestorType> newInvestors = new ArrayList<>();

        newInvestors.addAll(getNewDirectors(getNewPersons(allDirectors), accountType, adviser, dealer, form.getAccountSettings(), serviceErrors));
        newInvestors.add(getCompanyTrustee(form, adviser, dealer, serviceErrors));
        newInvestors.add(getSmsf(form, getTrustDetails(form.getSmsf(), form.getShareholderAndMembers(), regulatedTrust()), adviser, dealer, serviceErrors));
        newInvestors.addAll(getAllAdditionalPersons(form, adviser, dealer, serviceErrors));
        return investors(newInvestors, getExistingDirectors(getExistingPersons(allDirectors), accountType));
    }

    private InvestorsType individualSmsfInvestorsType(IClientApplicationForm form, IClientApplicationForm.AccountType accountType,
                                                      BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final List<IExtendedPersonDetailsForm> allTrustees = form.getTrustees();
        final List<InvestorType> newInvestors = new ArrayList<>();
        newInvestors.add(getSmsf(form, getTrustDetails(form.getSmsf(), form.getShareholderAndMembers(), regulatedTrust()), adviser, dealer, serviceErrors));
        newInvestors.addAll(getSmsfTrustees(getNewPersons(allTrustees), accountType, adviser, dealer, form.getAccountSettings(), serviceErrors));
        newInvestors.addAll(getAllAdditionalPersons(form, adviser, dealer, serviceErrors));
        return investors(newInvestors, getSmsfExistingTrustees(getExistingPersons(allTrustees), accountType));
    }

    private InvestorsType corporateTrustInvestorsType(IClientApplicationForm form, IClientApplicationForm.AccountType accountType,
                                                      BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final List<IExtendedPersonDetailsForm> directors = form.getDirectors();
        final ITrustForm trustForm = form.getTrust();
        final List<InvestorType> newInvestors = new ArrayList<>();
        newInvestors.add(getTrust(form, getTrustDetails(trustForm, form.getShareholderAndMembers(), getTrustType(trustForm)), adviser, dealer, serviceErrors));
        newInvestors.add(getCompanyTrustee(form, adviser, dealer, serviceErrors));
        newInvestors.addAll(getNewDirectors(getNewPersons(directors), accountType, adviser, dealer, form.getAccountSettings(), serviceErrors));
        newInvestors.addAll(getAllAdditionalPersons(form, adviser, dealer, serviceErrors));
        return investors(newInvestors, getExistingDirectors(getExistingPersons(directors), accountType));
    }

    private TrustTypeType getTrustType(ITrustForm trustForm) {
        final TrustTypeType trustType;
        switch (trustForm.getTrustType()) {
            case FAMILY:
            case OTHER:
                trustType = familyOtherTrustType(trustForm);
                break;

            case REGULATED:
                trustType = regulatedTrust(trustForm.getRegulatorLicenseNumber());
                break;

            case REGISTERED_MIS:
                trustType = investmentScheme(trustForm.getArsn());
                break;

            case GOVT_SUPER:
                trustType = governmentSuperTrustType(trustForm);
                break;

            default:
                throw new UnsupportedOperationException("Don't know the trusttype of " + trustForm.getTrustType().value());
        }

        return trustType;
    }

    protected TrustTypeType governmentSuperTrustType(ITrustForm trustForm) {
        return superannuationFund(trustForm.getNameOfLegislation());
    }

    protected TrustTypeType familyOtherTrustType(ITrustForm trustForm) {
        final String description = nullIfBlank(getTrustDescription(trustForm));
        final String otherDescription = nullIfBlank(trustForm.getDescriptionOther());
        final SettlorOfTrustPartyDetailType settlor = trustForm.hasSettlorOfTrust() ? settlorOfTrust(trustForm) : null;
        return standardTrust(description, otherDescription, settlor);
    }

    private String getTrustDescription(ITrustForm trustForm) {
        final String userId;
        if(FAMILY.equals(trustForm.getTrustType())){
            userId = StandardTrustDescription.FAMILY.value();
        } else {
            userId = StandardTrustDescription.fromString(trustForm.getDescription()).value();
        }
        final Code code = staticIntegrationService.loadCodeByUserId(CodeCategory.TRUST_TYPE_DESC, userId, new FailFastErrorsImpl());
        return code.getName();
    }

    /**
     * Create a settlor of trust node - new for the V3 schema.
     * @return settler of the trust.
     * @param trustForm the trust details form.
     */
    protected SettlorOfTrustPartyDetailType settlorOfTrust(ITrustForm trustForm) {
        if(ORGANISATION.equals(trustForm.getSettlorOfTrust())){
            return organisationSettlor(trustForm.getOrganisationName());
        }
        else if(INDIVIDUAL.equals(trustForm.getSettlorOfTrust())){
            return individualSettlor(trustForm.getFirstName(), trustForm.getLastName());
        }
        return null;
    }

    private List<InvestorType> getNewTrustees(List<IExtendedPersonDetailsForm> newTrustees, final IClientApplicationForm.AccountType accountType,
                                              final BrokerUser adviser, final Broker dealer, final ITrustForm.TrustType trustType,
                                              final IAccountSettingsForm accountSettings, final ServiceErrors serviceErrors) {
        return Lambda.convert(newTrustees, new Converter<IExtendedPersonDetailsForm, InvestorType>() {

            @Override
            public InvestorType convert(IExtendedPersonDetailsForm trustee) {
                final InvestorType investor = convertToInvestorType(accountType, trustee, adviser, dealer, accountSettings, serviceErrors);
                addTrustPartyRole(trustee,investor,trustType);
                return investor;
            }
        });
    }

    private void addTrustPartyRole(IExtendedPersonDetailsForm trustee,InvestorType investor,ITrustForm.TrustType trustType){
        List<PartyRoleInRelatedOrganisationType> roles = investor.getPartyRoleInRelatedOrganisation();
        if(REGISTERED_MIS != trustType) {
            roles.add(TRUST_TRUSTEE_ROLE);
        }else{
            roles.add(TRUST_RESPONSIBLE_ENTITY_ROLE);
        }
        roles.add(TRUST_BENEFICIAL_OWNER_ROLE);
        if (trustee.isBeneficiary()) {
            roles.add(TRUST_BENEFICIARY_ROLE);
        }
    }

    private List<InvestorType> getSmsfTrustees(List<IExtendedPersonDetailsForm> newTrustees, final IClientApplicationForm.AccountType accountType,
                                               final BrokerUser adviser, final Broker dealer, final IAccountSettingsForm accountSettingsForm,
                                               final ServiceErrors serviceErrors) {
        return Lambda.convert(newTrustees, new Converter<IExtendedPersonDetailsForm, InvestorType>() {

            @Override
            public InvestorType convert(IExtendedPersonDetailsForm trustee) {
                return convertToInvestorType(accountType, trustee, adviser, dealer, accountSettingsForm, serviceErrors);
            }
        });
    }

    private List<InvestorType> getSmsfExistingTrustees(List<IExtendedPersonDetailsForm> existingPersons, final IClientApplicationForm.AccountType accountType) {
        return Lambda.convert(existingPersons, new Converter<IExtendedPersonDetailsForm, InvestorType>() {
            @Override
            @SuppressWarnings("findbugs:BC_UNCONFIRMED_CAST")
            public InvestorType convert(IExtendedPersonDetailsForm investor) {
                return existingInvestorTypeBuilder.getSmsfTrusteeType((ITrusteeDetailsForm) investor, accountType);
            }
        });
    }

    private List<InvestorType> getExistingTrustees(List<IExtendedPersonDetailsForm> existingPersons, final IClientApplicationForm.AccountType accountType, final ITrustForm.TrustType trustType) {
        return Lambda.convert(existingPersons, new Converter<IExtendedPersonDetailsForm, InvestorType>() {
            @Override
            @SuppressWarnings("findbugs:BC_UNCONFIRMED_CAST")
            public InvestorType convert(IExtendedPersonDetailsForm investor) {
                return existingInvestorTypeBuilder.getTrusteeType((ITrusteeDetailsForm) investor, accountType, trustType);
            }
        });
    }

    private <E extends IExtendedPersonDetailsForm> List<E> getNewPersons(List<E> allInvestors) {
        return Lambda.filter(new LambdaMatcher<IExtendedPersonDetailsForm>() {
            @Override
            protected boolean matchesSafely(IExtendedPersonDetailsForm investor) {
                return !investor.isExistingPerson();
            }
        }, allInvestors);
    }

    private <E extends IExtendedPersonDetailsForm> List<E> getExistingPersons(List<E> allInvestors) {
        return Lambda.filter(new LambdaMatcher<IExtendedPersonDetailsForm>() {
            @Override
            protected boolean matchesSafely(IExtendedPersonDetailsForm investor) {
                return investor.isExistingPerson();
            }
        }, allInvestors);
    }

    public List<InvestorType> getNewDirectors(final List<IExtendedPersonDetailsForm> directors, final IClientApplicationForm.AccountType
        accountType, final BrokerUser adviser, final Broker dealer, final IAccountSettingsForm accountSettings, final ServiceErrors serviceErrors) {
        return Lambda.convert(directors, new Converter<IExtendedPersonDetailsForm, InvestorType>() {

            @Override
            public InvestorType convert(IExtendedPersonDetailsForm director) {
                InvestorType investor = convertToInvestorType(accountType, director, adviser, dealer, accountSettings, serviceErrors);
                return investor;
            }
        });
    }

    private List<InvestorType> getExistingDirectors(final List<IExtendedPersonDetailsForm> existingInvestors, final IClientApplicationForm.AccountType accountType) {
        return Lambda.convert(existingInvestors, new Converter<IExtendedPersonDetailsForm, InvestorType>() {
            @Override
            public InvestorType convert(IExtendedPersonDetailsForm investor) {
                return existingInvestorTypeBuilder.getDirectorType((IDirectorDetailsForm) investor, accountType);
            }
        });
    }

    private List<InvestorType> getAllNewInvestors(final List<IExtendedPersonDetailsForm> investors, final IClientApplicationForm.AccountType
        accountType, final BrokerUser adviser, final Broker dealer, final IAccountSettingsForm accountSettings, final ServiceErrors serviceErrors) {
        return Lambda.convert(investors, new Converter<IExtendedPersonDetailsForm, InvestorType>() {

            @Override
            public InvestorType convert(IExtendedPersonDetailsForm investor) {
                InvestorType investorType = convertToInvestorType(accountType, investor, adviser, dealer, accountSettings, serviceErrors);
                return investorType;
            }
        });
    }

    private InvestorType convertToInvestorType(IClientApplicationForm.AccountType accountType, IExtendedPersonDetailsForm investor, BrokerUser adviser, Broker dealer, IAccountSettingsForm accountSettings, ServiceErrors serviceErrors) {
        //party details
        final InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        contactDetailsBuilder.populateContactDetailsField(partyDetails, investor);
        partyDetails.setCorrelationSequenceNumber(investor.getCorrelationSequenceNumber().toString());
        partyDetails.setPartyDetails(getPartyDetailType(investor, adviser, dealer, accountSettings, serviceErrors));
        partyDetails.setPostalAddresses(getPostalAddresses(investor, serviceErrors));

        setCustomerNumberForGCMInvestor(investor, partyDetails);

        final InvestorType investorType = new InvestorType();
        investorType.setInvestorDetails(partyDetails);
        investorType.setAuthorityProfiles(authorityTypeBuilder.getInvestorAuthorityProfilesType(investor.getPaymentSetting()));
        if (investor.isApprover()) {
            investorType.getAuthorityProfiles().getAuthorityProfile().add(authorityTypeBuilder.getInvestorAuthorityProfileTypeForApplicationApproval());
        }
        addRoles(accountType, investor, investorType);
        if(investor.isPrimaryContact()){
            investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.CONTACT_PERSON_ROLE);
        }
        taxFieldsBuilder.populateTax(investorType.getInvestorDetails(), investor, accountType);
        return investorType;
    }

    private void setCustomerNumberForGCMInvestor(IExtendedPersonDetailsForm investor, InvolvedPartyDetailsType partyDetails) {
        if(isNotBlank(investor.getCisId()) && isNotBlank(investor.getUserName())){
            partyDetails.setCustomerIdentifiers(customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKeyAndZNumber(investor.getCisId(), investor.getUserName()));
        }
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    private void addRoles(IClientApplicationForm.AccountType accountType, IExtendedPersonDetailsForm investor, InvestorType investorType) {
        switch (accountType) {
            case INDIVIDUAL:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                addPrimaryOwnerRole(investorType);
                break;
            case JOINT:
                addPrimaryOrSecondaryRole(investor, investorType);
                break;
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
                addRolesForCorporateSmsf((IDirectorDetailsForm) investor, investorType, accountType);
                break;

            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
                addRolesForIndividualSmsf((ITrusteeDetailsForm) investor, investorType);
                break;

            case INDIVIDUAL_TRUST:
                addRolesForIndividualTrust(investorType);
                break;
            case CORPORATE_TRUST:
                addRolesForCorporateTrust((IDirectorDetailsForm) investor, investorType);
                break;
            case COMPANY:
                addRolesForCompany((IDirectorDetailsForm) investor, investorType);
                break;
            default:
                throw new UnsupportedOperationException("Don't know what roles to set for account type " + accountType);
        }
    }

    private void addPrimaryOrSecondaryRole(IExtendedPersonDetailsForm investor, InvestorType investorType) {
        if (investor.isPrimaryContact()) {
            addPrimaryOwnerRole(investorType);
        } else {
            addSecondaryRole(investorType);
        }
    }

    private void addRolesForCompany(IDirectorDetailsForm investor, InvestorType investorType) {
        final List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        switch (investor.getRole()) {
            case DIRECTOR:
                roles.add(COMPANY_DIRECTOR_ROLE);
                break;
            case SECRETARY:
                roles.add(COMPANY_SECRETARY_ROLE);
                break;
            case SIGNATORY:
                roles.add(COMPANY_SIGNATORY_ROLE);
                break;
            default:
                break;
        }
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE);

        if ((investor.isBeneficialOwner() || investor.isShareholder())&& !roles.contains(COMPANY_BENEFICIAL_OWNER_ROLE)){
            roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
        }
    }

    private void addRolesForIndividualTrust(InvestorType investorType) {
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE);
    }

    @SuppressWarnings({ "squid:S1172", "unused" })
    private void addPrimaryOwnerRole(InvestorType investorType) {
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.PRIMARY_OWNER_ROLE);
    }

    private void addSecondaryRole(InvestorType investorType) {
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.SECONDARY_OWNER_ROLE);
    }

    private void addRolesForCorporateTrust(IDirectorDetailsForm trustDirector, InvestorType investorType) {
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE);
        final List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        switch (trustDirector.getRole()) {
            case DIRECTOR:
                roles.add(COMPANY_DIRECTOR_ROLE);
                break;
            case SECRETARY:
                roles.add(COMPANY_SECRETARY_ROLE);
                break;
            case SIGNATORY:
                roles.add(COMPANY_SIGNATORY_ROLE);
                break;
            case TRUSTEE:
                roles.add(TRUST_TRUSTEE_ROLE);
                break;
            default:
                break;
        }

        if (trustDirector.isBeneficiary() && !roles.contains(TRUST_BENEFICIARY_ROLE)) {
            roles.add(TRUST_BENEFICIARY_ROLE);
        }
        if (trustDirector.isShareholder() && !roles.contains(COMPANY_BENEFICIAL_OWNER_ROLE)) {
            roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
        }
        if (trustDirector.isControllerOfTrust() && !roles.contains(TRUST_BENEFICIAL_OWNER_ROLE)) {
            roles.add(TRUST_BENEFICIAL_OWNER_ROLE);
        }
    }

    private void addRolesForIndividualSmsf(ITrusteeDetailsForm trustee, InvestorType investorType) {
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE);
        final List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        roles.add(SMSF_TRUSTEE_ROLE);
        roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
        if (trustee.isMember()) {
            roles.add(SMSF_MEMBER_ROLE);
        }
    }

    private void addRolesForCorporateSmsf(IDirectorDetailsForm director, InvestorType investorType, IClientApplicationForm.AccountType accountType) {
        investorType.getInvestmentAccountPartyRole().add(InvestmentAccountPartyRoleTypeType.ACCOUNT_SERVICER_ROLE);
        final List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        switch (director.getRole()) {
            case DIRECTOR:
                roles.add(COMPANY_DIRECTOR_ROLE);
                break;
            case SECRETARY:
                roles.add(COMPANY_SECRETARY_ROLE);
                break;
            case SIGNATORY:
                roles.add(COMPANY_SIGNATORY_ROLE);
                break;
            case TRUSTEE:
                roles.add(SMSF_TRUSTEE_ROLE);
                break;
            default:
                break;
        }

        if (director.isMember()) {
            roles.add(SMSF_MEMBER_ROLE);
        }
        if (director.isShareholder()) {
            roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
            if(accountType == NEW_CORPORATE_SMSF) {
                roles.add(COMPANY_SHAREHOLDER_ROLE);
            }
        }
        if (director.isBeneficialOwner() && !roles.contains(COMPANY_BENEFICIAL_OWNER_ROLE)) {
            roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
        }
        if(director.isCompanySecretary()){
            roles.add(COMPANY_SECRETARY_ROLE);

        }
    }

    private PartyDetailType getPartyDetailType(IPersonDetailsForm person, BrokerUser adviser, Broker dealer, IAccountSettingsForm accountSettings, ServiceErrors serviceErrors) {
        return party(individualTypeBuilder.getIndividualType(person, adviser, dealer, accountSettings, serviceErrors));
    }

    private AddressesType getPostalAddresses(IExtendedPersonDetailsForm person, ServiceErrors serviceErrors) {
        final boolean gcm = person.isGcmRetrievedPerson();
        if (gcm) {
            return addresses(addressTypeBuilder.getDefaultAddressType(person.getGCMRetAddresses(), new AddressType(), gcm, serviceErrors));
        } else {
            return addresses(addressTypeBuilder.getDefaultAddressType(person.getPostalAddress(), new AddressType(), gcm, serviceErrors));
        }
    }

    private List<InvestorType> getAllExistingInvestors(final List<IExtendedPersonDetailsForm> existingInvestors, final IClientApplicationForm.AccountType accountType) {
        return Lambda.convert(existingInvestors, new Converter<IExtendedPersonDetailsForm, InvestorType>() {
            @Override
            public InvestorType convert(IExtendedPersonDetailsForm investor) {
                return existingInvestorTypeBuilder.getInvestorType(investor, accountType);
            }
        });
    }

    private List<InvestorType> getAllAdditionalPersons(final IClientApplicationForm form, final BrokerUser adviser, final Broker dealer, final ServiceErrors serviceErrors) {
        return Lambda.convert(form.getAdditionalShareholdersAndMembers(), new Converter<IExtendedPersonDetailsForm, InvestorType>() {

            @Override
            public InvestorType convert(IExtendedPersonDetailsForm shareholderAndMember) {
                InvestorType investor = convertShareholderAndMemberToInvestorType(form.getAccountType(), shareholderAndMember, adviser, dealer, form.getAccountSettings(), serviceErrors);
                addTaxDetails(investor.getInvestorDetails(), shareholderAndMember, serviceErrors);
                return investor;
            }
        });
    }

    private void addTaxDetails(InvolvedPartyDetailsType partyDetailsType, ICrsTaxDetailsForm crsTaxDetailsForm, ServiceErrors serviceErrors) {
        taxFieldsBuilder.populateCrsTaxRelatedFieldsForNewInvestor(partyDetailsType, crsTaxDetailsForm);
    }

    private InvestorType convertShareholderAndMemberToInvestorType(IClientApplicationForm.AccountType accountType, IExtendedPersonDetailsForm shareholderAndMember, BrokerUser adviser,
                                                                   Broker dealer, IAccountSettingsForm accountSettings, ServiceErrors serviceErrors) {
        final InvestorType investorType = getAdditionalMember(shareholderAndMember, adviser, dealer, accountSettings, serviceErrors);
        final List<PartyRoleInRelatedOrganisationType> roles = investorType.getPartyRoleInRelatedOrganisation();
        if (shareholderAndMember.isMember()) {
            roles.add(SMSF_MEMBER_ROLE);
        }
        if (shareholderAndMember.isShareholder()) {
            roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
        }
        if (shareholderAndMember.isBeneficiary()) {
            roles.add(TRUST_BENEFICIARY_ROLE);
        }
        if (shareholderAndMember.isControllerOfTrust()) {
            roles.add(TRUST_BENEFICIAL_OWNER_ROLE);
        }
        if (shareholderAndMember.isBeneficialOwner()) {
            if (!roles.contains(COMPANY_BENEFICIAL_OWNER_ROLE)) {
                roles.add(COMPANY_BENEFICIAL_OWNER_ROLE);
            }
        }
        return investorType;
    }

    private InvestorType getAdditionalMember(IPersonDetailsForm shareholderAndMember, final BrokerUser adviser, final Broker dealer,
                                             IAccountSettingsForm accountSettings, ServiceErrors serviceErrors) {
        final InvolvedPartyDetailsType partyDetails = new InvolvedPartyDetailsType();
        partyDetails.setCorrelationSequenceNumber(shareholderAndMember.getCorrelationSequenceNumber().toString());
        if (shareholderAndMember.hasResidentialAddress()) {
            partyDetails.setPostalAddresses(addresses(addressTypeBuilder.getDefaultAddressType(shareholderAndMember.getResidentialAddress(), new AddressType(), serviceErrors)));
        }
        partyDetails.setPartyDetails(getPartyDetailType(shareholderAndMember, adviser, dealer, accountSettings, serviceErrors));
        return investor(partyDetails);
    }
}
