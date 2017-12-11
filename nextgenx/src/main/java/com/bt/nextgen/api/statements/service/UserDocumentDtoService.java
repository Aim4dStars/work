package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

import java.io.IOException;


/**
 * Created by L081361 on 27/11/2015.
 */
public interface UserDocumentDtoService extends SearchByCriteriaDtoService<DocumentDto> {

    DocumentDto loadDocument(DocumentKey documentKey) throws IOException;
}

