package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationDocuments;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentityDocument;

/**
 * Created by m040398 on 24/03/2016.
 */
final class IdentityVerificationForm implements IIdentityVerificationForm {

    private final IdentityDocument document;

    public IdentityVerificationForm(IdentityDocument document) {
        this.document = document;
    }

    @Override
    public boolean hasInternationalDocuments() {
        return null != document && null != document.getInternationaldocuments();
    }

    @Override
    public IIdentityVerificationDocuments getInternationalDocuments() {
        return hasInternationalDocuments() ? new IdentityVerificationDocuments(document.getInternationaldocuments()) : null;
    }

    @Override
    public boolean hasNonPhotoDocuments() {
        return null != document && null != document.getNonphotodocuments();
    }

    @Override
    public IIdentityVerificationDocuments getNonPhotoDocuments() {
        return hasNonPhotoDocuments() ?  new IdentityVerificationDocuments(document.getNonphotodocuments()) : null;
    }

    @Override
    public boolean hasPhotoDocuments() {
        return null != document && null != document.getPhotodocuments();
    }

    @Override
    public IIdentityVerificationDocuments getPhotoDocuments() {
        return hasPhotoDocuments() ? new IdentityVerificationDocuments(document.getPhotodocuments()) : null;
    }
}