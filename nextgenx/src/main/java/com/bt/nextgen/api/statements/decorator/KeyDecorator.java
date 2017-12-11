package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.btfin.panorama.core.security.encryption.EncodedString;

/**
 *
 */
public class KeyDecorator implements DtoDecorator {

    private DtoDecorator decorator;
    private String documentId;
    private String accountId;
    private DocumentDto documentDto;

    public KeyDecorator(String documentId, String accountId) {
        this.documentId= documentId;
        this.accountId = accountId;
    }

    public KeyDecorator(String documentId, String accountId, DocumentDto documentDto) {
        this.documentId = documentId;
        this.accountId = accountId;
        this.documentDto = documentDto;
    }

    public KeyDecorator(DtoDecorator decorator, String documentId, String accountId) {
        this.decorator = decorator;
        this.documentId = documentId;
        this.accountId = accountId;
    }

    @Override
    public DocumentDto decorate() {
        if(decorator!= null) {
            documentDto = decorator.decorate();
        }
        if (documentDto==null){
            documentDto = new DocumentDto();
        }
        DocumentKey key = new DocumentKey();
        if(accountId!= null) {
            key.setAccountId(EncodedString.fromPlainText(accountId).toString());
        }
        key.setDocumentId(EncodedString.fromPlainText(documentId).toString());
        documentDto.setKey(key);
        return documentDto;
    }
}
