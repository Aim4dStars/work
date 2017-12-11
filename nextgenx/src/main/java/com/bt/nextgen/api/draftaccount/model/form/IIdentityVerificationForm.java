package com.bt.nextgen.api.draftaccount.model.form;

import java.util.Map;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IIdentityVerificationForm {

    boolean hasInternationalDocuments();

    IIdentityVerificationDocuments getInternationalDocuments();

    boolean hasNonPhotoDocuments();

    IIdentityVerificationDocuments getNonPhotoDocuments();

    boolean hasPhotoDocuments();

    IIdentityVerificationDocuments getPhotoDocuments();

}
