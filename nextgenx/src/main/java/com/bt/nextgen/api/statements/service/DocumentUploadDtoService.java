package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;

/**
 * Created by L062605 on 30/07/2015.
 */
public interface DocumentUploadDtoService {
    DocumentDto upload(DocumentDto dto);
    DocumentDto uploadNewVersion(DocumentDto dto);
}
