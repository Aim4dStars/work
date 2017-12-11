package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.model.form.IIdentityDocument;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationDocuments;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.util.AustralianStatesUtil;
import ns.btfin_com.identityverification.v1_1.IdentificationIndDocumentType;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationInformationIndTypeType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationIndType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationsIndType;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.btfin.panorama.onboarding.helper.PartyHelper.document;
import static com.btfin.panorama.onboarding.helper.PartyHelper.identification;
import static com.btfin.panorama.onboarding.helper.PartyHelper.identifications;
import static org.apache.commons.lang.StringUtils.upperCase;

@Service
public class PartyIdentificationInformationBuilder {

    private static final String AUSTRALIA_CODE = "AU";

    public PartyIdentificationInformationsIndType getPartyIdentificationInformation(IPersonDetailsForm personDetailsForm) {
        final IIdentityVerificationForm idvForm = personDetailsForm.getIdentityVerificationForm();
        if (idvForm.hasInternationalDocuments()) {
            return getPartyIdentificationInformationFromInternationalDocuments(idvForm.getInternationalDocuments());
        } else if (idvForm.hasNonPhotoDocuments()) {
            return getPartyIdentificationInformationFromNonPhotoDocuments(idvForm.getNonPhotoDocuments());
        } else if (idvForm.hasPhotoDocuments()) {
            return getPartyIdentificationInformationFromPhotoDocuments(idvForm.getPhotoDocuments());
        }
        return null;
    }

    private PartyIdentificationInformationsIndType getPartyIdentificationInformationFromInternationalDocuments(IIdentityVerificationDocuments internationalDocuments) {
        final List<IIdentityDocument> identityDocuments = internationalDocuments.getIdentityDocuments();
        final PartyIdentificationInformationIndType[] identifications = new PartyIdentificationInformationIndType[identityDocuments.size()];
        for (int i = 0; i < identityDocuments.size(); i++) {
            final IIdentityDocument identityDocument = identityDocuments.get(i);
            final PartyIdentificationInformationIndTypeType idType = identificationType(identityDocument);
            final IdentificationIndDocumentType document = document(identityDocument.isVerificationSourceOriginal(), idType,
                    upperCase(identityDocument.getDocumentIssuer()), identityDocument.isEnglishTranslationSighted());
            identifications[i] = partyIdentification(document, identityDocument);
        }

        return identifications(identifications);
    }

    private PartyIdentificationInformationsIndType getPartyIdentificationInformationFromNonPhotoDocuments(IIdentityVerificationDocuments nonPhotoDocuments) {
        final List<IIdentityDocument> identityDocuments = nonPhotoDocuments.getIdentityDocuments();
        final PartyIdentificationInformationIndType[] identifications = new PartyIdentificationInformationIndType[identityDocuments.size()];
        for (int i = 0; i < identityDocuments.size(); i++) {
            final IIdentityDocument identityDocument = identityDocuments.get(i);
            final PartyIdentificationInformationIndTypeType idType = identificationType(identityDocument);
            final String issuerName;
            String state = null;
            switch (idType) {
                case CITIZENSHIP_CERTIFICATE:
                    issuerName = "Australian Government";
                    break;
                case HEALTH_CARD:
                case PENSION_CARD:
                    issuerName = "Centrelink";
                    break;
                case TAXATION_NOTICE:
                    issuerName = "ATO";
                    break;
                case FINANCIAL_BENEFITS_NOTICE:
                    state = AustralianStatesUtil.getAustralianStateCode(identityDocument.getDocumentIssuer());
                    issuerName = identityDocument.getDocumentIssuer();
                    break;
                case UTILITIES_NOTICE:
                    issuerName = identityDocument.getDocumentIssuer();
                    break;
                default:
                    issuerName = null;
                    break;
            }
            final IdentificationIndDocumentType document = document(identityDocument.isVerificationSourceOriginal(),
                    issuerName, idType, state, AUSTRALIA_CODE, identityDocument.isEnglishTranslationSighted());
            identifications[i] = partyIdentification(document, identityDocument);
        }
        return identifications(identifications);
    }

    private PartyIdentificationInformationsIndType getPartyIdentificationInformationFromPhotoDocuments(IIdentityVerificationDocuments photoDocuments) {
        final List<IIdentityDocument> identityDocuments = photoDocuments.getIdentityDocuments();
        final PartyIdentificationInformationIndType[] identifications = new PartyIdentificationInformationIndType[identityDocuments.size()];
        for (int i = 0; i < identityDocuments.size(); i++) {
            final IIdentityDocument identityDocument = identityDocuments.get(i);
            final PartyIdentificationInformationIndTypeType idType = identificationType(identityDocument);
            String state = null;
            switch (idType) {
                case DRIVERS_LICENSE:
                case IDENTIFICATION_CARD:
                    state = AustralianStatesUtil.getAustralianStateCode(identityDocument.getDocumentIssuer());
                    break;
                default:
                    break;
            }
            final IdentificationIndDocumentType document = document(identityDocument.isVerificationSourceOriginal(),
                    idType, state, AUSTRALIA_CODE, identityDocument.isEnglishTranslationSighted());
            identifications[i] = partyIdentification(document, identityDocument);
        }
        return identifications(identifications);
    }

    private PartyIdentificationInformationIndType partyIdentification(IdentificationIndDocumentType document,
            IIdentityDocument identityDocument) {
        return identification(identityDocument.getDocumentNumber(), document, identityDocument.getIssueDate(),
                identityDocument.getExpiryDate());
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    private PartyIdentificationInformationIndTypeType identificationType(IIdentityDocument identityDocument) {
        final PartyIdentificationInformationIndTypeType idType;
        switch (identityDocument.getIdentificationType()) {
            case DRIVERLICENCE:
                idType = PartyIdentificationInformationIndTypeType.DRIVERS_LICENSE;
                break;
            case NATIONALID:
            case AGECARD:
                idType = PartyIdentificationInformationIndTypeType.IDENTIFICATION_CARD;
                break;
            case PASSPORT:
                idType = PartyIdentificationInformationIndTypeType.PASSPORT;
                break;
            case ATONOTICE:
                idType = PartyIdentificationInformationIndTypeType.TAXATION_NOTICE;
                break;
            case BIRTHCERTIFICATE:
                idType = PartyIdentificationInformationIndTypeType.BIRTH_CERTIFICATE;
                break;
            case CITIZENSHIPDOCUMENT:
                idType = PartyIdentificationInformationIndTypeType.CITIZENSHIP_CERTIFICATE;
                break;
            case FINANCIALBENEFITS:
                idType = PartyIdentificationInformationIndTypeType.FINANCIAL_BENEFITS_NOTICE;
                break;
            case HEALTHCARD:
                idType = PartyIdentificationInformationIndTypeType.HEALTH_CARD;
                break;
            case PENSIONCARD:
                idType = PartyIdentificationInformationIndTypeType.PENSION_CARD;
                break;
            case UTILITIESNOTICE:
                idType = PartyIdentificationInformationIndTypeType.UTILITIES_NOTICE;
                break;
            default:
                idType = null;
                break;
        }
        return idType;
    }
}
