package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.IdentificationTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class IdentityVerificationDocuments implements IIdentityVerificationDocuments {

    private final Map<String, Object> identityDocumentsMap;


    public IdentityVerificationDocuments(Map<String, Object> identityDocumentsMap) {
        this.identityDocumentsMap = identityDocumentsMap;
    }

    public List<IIdentityDocument> getIdentityDocuments() {
        List<IIdentityDocument> identityDocuments = new ArrayList<>();
        for (Entry<String, Object> identityDocument : this.identityDocumentsMap.entrySet()) {
            if ("valid".equalsIgnoreCase(identityDocument.getKey())) {
                continue;
            }

            identityDocuments.add(new IdentityDocument(IdentificationTypeEnum.fromValue(identityDocument.getKey()), (Map<String, String>) identityDocument.getValue()));
        }

        return identityDocuments;
    }
}
