package com.bt.nextgen.api.statements.permission;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.btfin.panorama.core.security.encryption.EncodedString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class DocumentRequestManager {

    private static final ThreadLocal<List<DocumentDto>> requestScope = new ThreadLocal<>();

    private DocumentRequestManager() {
    }

    public static void addDocument(DocumentDto document) {
        List<DocumentDto> documentDtos = requestScope.get();
        if (documentDtos == null) {
            documentDtos = new ArrayList<>();
            documentDtos.add(document);
            requestScope.set(documentDtos);
        } else {
            documentDtos.add(document);
        }
    }

    @Nullable
    public static DocumentDto getDocument(String key) {
        List<DocumentDto> documentDtos = requestScope.get();
        if(null != documentDtos) {
            for (DocumentDto documentDto : documentDtos) {
                DocumentKey documentKey = documentDto.getKey();
                String decriptedId = new EncodedString(documentKey.getDocumentId()).plainText();
                String decriptedKey = new EncodedString(key).plainText();
                if (decriptedId.equals(decriptedKey)) {
                    return documentDto;
                }
            }
        }
        return null;
    }

    public static DocumentDto getDocument(DocumentKey documentId) {
        return getDocument(documentId.getDocumentId());
    }

    public static void removeDocuments() {
        requestScope.remove();
    }
}
