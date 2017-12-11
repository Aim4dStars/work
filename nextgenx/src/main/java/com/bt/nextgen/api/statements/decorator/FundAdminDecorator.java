package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.SupplimentaryDocument;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.integration.financialdocument.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L075208 on 11/01/2016.
 */
@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
public class FundAdminDecorator implements DtoDecorator {

    private DocumentDto documentDto;
    private Document document;
    private DtoDecorator dtoDecorator;
    private CmsService cmsService;

    public FundAdminDecorator(CmsService cmsService, DocumentDto documentDto, Document document, DtoDecorator dtoDecorator) {
        this.cmsService = cmsService;
        this.documentDto = documentDto;
        this.document = document;
        this.dtoDecorator = dtoDecorator;
    }

    public FundAdminDecorator(CmsService cmsService, Document document, DtoDecorator dtoDecorator) {
        this.cmsService = cmsService;
        this.document = document;
        this.dtoDecorator = dtoDecorator;
    }

    @Override
    public DocumentDto decorate() {
        if (documentDto == null) {
            documentDto = new DocumentDto();
        }
        if ("SMAPAC".equalsIgnoreCase(document.getDocumentTitleCode())) {
            documentDto.setSupplimentaryDocumentList(getSupplimentaryDocuments());
        }
        if (dtoDecorator != null) {
            documentDto = dtoDecorator.decorate();
        }
        return documentDto;
    }

    private List<SupplimentaryDocument> getSupplimentaryDocuments() {
        List<SupplimentaryDocument> supplimentaryDocuments = new ArrayList<SupplimentaryDocument>();
        if(documentDto.getSupplimentaryDocumentList()!= null) {
            supplimentaryDocuments = documentDto.getSupplimentaryDocumentList();
        }
        addToList("Doc.IP.fa.pack", supplimentaryDocuments, "Panorama SMSF Administration Service Guide");
        addToList("Doc.IP.fa.gettingStarted", supplimentaryDocuments, "Getting Started Guide");
        return supplimentaryDocuments;
    }

    private void addToList(String cmsKey, List<SupplimentaryDocument> supplimentaryDocuments, String name) {
        String contents = cmsService.getContent(cmsKey);
        if (contents != null) {
            supplimentaryDocuments.add(new SupplimentaryDocument(name, contents));
        }
    }
}