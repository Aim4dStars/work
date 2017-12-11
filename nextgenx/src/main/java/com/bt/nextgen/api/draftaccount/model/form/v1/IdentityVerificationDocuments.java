package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.draftaccount.model.form.IIdentityDocument;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationDocuments;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.*;

/**
 * Created by F030695 on 29/03/2016.
 */
class IdentityVerificationDocuments implements IIdentityVerificationDocuments {

    private final List<IIdentityDocument> identityDocuments;

    public IdentityVerificationDocuments(InternationalDocuments internationalDocuments) {
        this.identityDocuments = getInternationalDocumentList(internationalDocuments);
    }

    public IdentityVerificationDocuments(NonPhotoDocuments nonPhotoDocuments) {
        this.identityDocuments = getNonPhotoDocumentList(nonPhotoDocuments);
    }

    public IdentityVerificationDocuments(PhotoDocuments photoDocuments) {
        this.identityDocuments = getPhotoDocumentList(photoDocuments);
    }

    /**
     * Add 'doc' to 'result' list of documents
     * @param doc
     * @param type
     * @param result
     */
    private void addDocument(DocumentId doc, IdentificationTypeEnum type, List<IIdentityDocument> result) {
        if (null != doc) {
            result.add(new IdentityDocument(doc, type));
        }
    }

    private List<IIdentityDocument> getInternationalDocumentList(InternationalDocuments internationalDocuments) {
        List<IIdentityDocument> documents = new ArrayList<>();
        addDocument(internationalDocuments.getDriverlicence(), IdentificationTypeEnum.DRIVERLICENCE, documents);
        addDocument(internationalDocuments.getNationalid(), IdentificationTypeEnum.NATIONALID, documents);
        addDocument(internationalDocuments.getPassport(), IdentificationTypeEnum.PASSPORT, documents);
        return documents;
    }

    private List<IIdentityDocument> getNonPhotoDocumentList(NonPhotoDocuments nonPhotoDocuments) {
        List<IIdentityDocument> documents = new ArrayList<>();
        addDocument(nonPhotoDocuments.getAtonotice(), IdentificationTypeEnum.ATONOTICE, documents);
        addDocument(nonPhotoDocuments.getBirthcertificate(), IdentificationTypeEnum.BIRTHCERTIFICATE, documents);
        addDocument(nonPhotoDocuments.getCitizenshipdocument(), IdentificationTypeEnum.CITIZENSHIPDOCUMENT, documents);
        addDocument(nonPhotoDocuments.getFinancialbenefits(), IdentificationTypeEnum.FINANCIALBENEFITS, documents);
        addDocument(nonPhotoDocuments.getHealthcard(), IdentificationTypeEnum.HEALTHCARD, documents);
        addDocument(nonPhotoDocuments.getPensioncard(), IdentificationTypeEnum.PENSIONCARD, documents);
        addDocument(nonPhotoDocuments.getUtilitiesnotice(), IdentificationTypeEnum.UTILITIESNOTICE, documents);
        return documents;
    }

    private List<IIdentityDocument> getPhotoDocumentList(PhotoDocuments photoDocuments) {
        List<IIdentityDocument> documents = new ArrayList<>();
        addDocument(photoDocuments.getPassport(), IdentificationTypeEnum.PASSPORT, documents);
        addDocument(photoDocuments.getAgecard(), IdentificationTypeEnum.AGECARD, documents);
        addDocument(photoDocuments.getDriverlicence(), IdentificationTypeEnum.DRIVERLICENCE, documents);
        return documents;
    }

    @Override
    public List<IIdentityDocument> getIdentityDocuments() {
        return identityDocuments;
    }
}