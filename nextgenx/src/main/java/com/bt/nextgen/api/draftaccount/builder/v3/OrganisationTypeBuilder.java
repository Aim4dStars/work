package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.*;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import ns.btfin_com.identityverification.v1_1.DocumentType;
import ns.btfin_com.identityverification.v1_1.IDVRegulatoryBodyType;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationInformationOrgTypeType;
import ns.btfin_com.identityverification.v1_1.ValidityPeriodType;
import ns.btfin_com.party.v3_0.IdentificationChoiceType;
import ns.btfin_com.party.v3_0.OrganisationType;
import ns.btfin_com.party.v3_0.OrganisationTypeType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationOrgType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationsOrgType;
import ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.btfin.panorama.onboarding.helper.PartyHelper.document;
import static com.btfin.panorama.onboarding.helper.PartyHelper.identification;
import static com.btfin.panorama.onboarding.helper.PartyHelper.identifications;
import static com.btfin.panorama.onboarding.helper.PartyHelper.organisation;
import static com.btfin.panorama.onboarding.helper.PartyHelper.search;
import static org.apache.commons.lang.StringUtils.isBlank;

@Service
@SuppressWarnings("squid:S1200")
public class OrganisationTypeBuilder {

    @Autowired
    private final AddressTypeBuilder addressTypeBuilder;

    private final IDVPerformedByIntermediaryTypeBuilder idvPerformedByIntermediaryTypeBuilder = new IDVPerformedByIntermediaryTypeBuilder();

    protected PurposeOfBusinessRelationshipTypeBuilder purposeOfBusinessRelationshipTypeBuilder;

    public OrganisationTypeBuilder(AddressTypeBuilder addressTypeBuilder) {
        this.addressTypeBuilder = addressTypeBuilder;
        this.purposeOfBusinessRelationshipTypeBuilder = new PurposeOfBusinessRelationshipTypeBuilder();
    }

    public OrganisationTypeBuilder() {
        this(new AddressTypeBuilder());
    }

    /**
     * For company acting as trustee SoW is not captured. The SoW captured for main entity is assigned to company.
     * @param serviceErrors
     * @param mainEntityForm Corp SMSF / Trust form
     * @param companyTrusteeForm Company as trustee form
     * @param accountSettings accountsettings
     */

    public OrganisationType getCompanyAsTrustee(final IOrganisationForm mainEntityForm, final IOrganisationForm companyTrusteeForm,
                                                final IAccountSettingsForm accountSettings, final BrokerUser adviser, final Broker dealer, ServiceErrors serviceErrors) {
        OrganisationType organisation = getOrganisationBase(companyTrusteeForm, adviser, dealer, serviceErrors);
        organisation.setPurposeOfBusinessRelationship(purposeOfBusinessRelationshipTypeBuilder.purpose(mainEntityForm, accountSettings));
        return organisation;
    }

    public OrganisationType getOrganisation(final IOrganisationForm organisationForm, final IAccountSettingsForm accountSettings, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final OrganisationType organisation = getOrganisationBase(organisationForm, adviser, dealer, serviceErrors);
        organisation.setPurposeOfBusinessRelationship(purposeOfBusinessRelationshipTypeBuilder.purpose(organisationForm, accountSettings));
        return organisation;
    }

    protected OrganisationType getOrganisationBase(final IOrganisationForm form, BrokerUser adviser, Broker dealer, ServiceErrors serviceErrors) {
        final String abn = isBlank(form.getABN()) ? null : form.getABN();
        final OrganisationType organisation = organisation(form.getName(), null, abn, form.getACN(),
                form.getRegisteredForGST(), toOrganisationType(form.getOrganisationType()), form.getIndustryUcmCode());
        organisation.setRegisteredAddress(addressTypeBuilder.getAddressTypeWithOccupier(form, new RegisteredResidentialAddressDetailType(), serviceErrors));
        organisation.setPartyIdentificationInformations(getPartyIdentificationInformationsOrgType(form));
        organisation.setPartyIdentificationPerformedBy(idvPerformedByIntermediaryTypeBuilder.intermediary(adviser, dealer));
        return organisation;
    }

    private PartyIdentificationInformationsOrgType getPartyIdentificationInformationsOrgType(IOrganisationForm organisationForm) {
        return identifications(getPartyIdentificationInformationOrgType(organisationForm));
    }

    private PartyIdentificationInformationOrgType getPartyIdentificationInformationOrgType(IOrganisationForm organisationForm) {
        final PartyIdentificationInformationOrgType partyIdentificationInformation = identification(getIdentificationChoiceType(organisationForm));
        if (organisationForm instanceof ITrustForm) {
            ITrustForm trustForm = (ITrustForm) organisationForm;
            if (organisationForm.hasIDVDocument()) {
                IIdvDocument idvDocument = trustForm.getIdentityDocument().getIdvDocument();
                partyIdentificationInformation.setIdentificationNumber(idvDocument.getDocumentNumber());
                ValidityPeriodType validityPeriod = new ValidityPeriodType();
                validityPeriod.setStartDate(idvDocument.getDocumentDate());
                partyIdentificationInformation.setValidityPeriod(validityPeriod);
            } else if (trustForm.getTrustType() == ITrustForm.TrustType.GOVT_SUPER) {
                ValidityPeriodType validityPeriod = new ValidityPeriodType();
                validityPeriod.setStartDate(trustForm.getIDVSearchDate());
                partyIdentificationInformation.setValidityPeriod(validityPeriod);
            }
        }
        return partyIdentificationInformation;
    }

    private IdentificationChoiceType getIdentificationChoiceType(IOrganisationForm organisationForm) {
        if (organisationForm.hasIDVDocument()) {
            return getIdentificationDocument((ITrustForm) organisationForm);
        } else {
            return getIdentificationSearchType(organisationForm);
        }
    }

    private IdentificationChoiceType getIdentificationDocument(ITrustForm trustform) {
        IIdvDocument idvDocument = trustform.getIdentityDocument().getIdvDocument();
        return document(getIdentificationType(idvDocument.getDocumentType()), getDocumentType(idvDocument.getVerifiedFrom()), idvDocument.getName());
    }

    private DocumentType getDocumentType(String verifiedFrom) {
        if ("original".equalsIgnoreCase(verifiedFrom)) {
            return DocumentType.ORIGINAL;
        }
        return DocumentType.CERTIFIED_COPY;
    }

    private PartyIdentificationInformationOrgTypeType getIdentificationType(String documentType) {
        switch (documentType) {
            case "letteridv":
                return PartyIdentificationInformationOrgTypeType.SOLICITOR_LETTER;
            case "trustdeed":
                return PartyIdentificationInformationOrgTypeType.TRUST_DEED;
            case "atonotice":
                return PartyIdentificationInformationOrgTypeType.TAXATION_NOTICE;
            default:
                throw new UnsupportedOperationException("Unknown document identification type = " + documentType);
        }
    }

    private IdentificationChoiceType getIdentificationSearchType(IOrganisationForm organisationForm) {
        final String searchUrl;
        if (organisationForm instanceof ITrustForm) {
            searchUrl = ((ITrustForm) organisationForm).getIdvURL();
        } else {
            searchUrl = organisationForm.getIDVDocIssuer();
        }
        return search(searchUrl, toIDVRegulatoryBody(organisationForm.getRegulatoryBody()));
    }

    static IDVRegulatoryBodyType toIDVRegulatoryBody(IOrganisationForm.RegulatoryBody body) {
        if (body != null) {
            switch (body) {
                case APRA:
                    return IDVRegulatoryBodyType.APRA;
                case ASIC:
                    return IDVRegulatoryBodyType.ASIC;
                case ATO:
                    return IDVRegulatoryBodyType.ATO;
                default:
                    break;
            }
        }
        return null;
    }

    static OrganisationTypeType toOrganisationType(com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm.OrganisationType organisationType) {
        if (organisationType != null) {
            switch (organisationType) {
                case COMPANY:
                    return OrganisationTypeType.COMPANY;
                case SMSF:
                    return OrganisationTypeType.SMSF;
                case TRUST:
                    return OrganisationTypeType.TRUST;
                default:
                    break;
            }
        }
        return OrganisationTypeType.OTHER;
    }
}
