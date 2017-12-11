package com.bt.nextgen.service.integration.financialdocument;

import com.bt.nextgen.core.domain.key.StringIdKey;

/**
 * Created by L062329 on 17/07/2015.
 */
public final class DocumentKey extends StringIdKey {

    private DocumentKey(String id) {
        super(id);
    }

    public static DocumentKey valueOf(String documentId) {
        if (documentId == null)
            return null;
        else
            return new DocumentKey(documentId);
    }
}
