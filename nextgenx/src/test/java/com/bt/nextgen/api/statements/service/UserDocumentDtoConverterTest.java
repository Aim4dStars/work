package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L081361 on 1/12/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserDocumentDtoConverterTest {

    @InjectMocks
    private UserDocumentDtoConverter userDocumentDtoConverter;

    List<Document> documents = new ArrayList<>();
    List<DocumentDto> documentDtos = new ArrayList<>();

    @Before
    public void setup() {

        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument1 = new CmisDocumentImpl();
        cmisDocument1.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0"));
        cmisDocument1.setDocumentName("IMRCTI_Doc");
        cmisDocument1.setStatus("Draft");
        cmisDocument1.setSize(BigInteger.valueOf(123456));
        cmisDocument1.setDocumentType("STM");
        cmisDocument1.setMimeType("application/pdf");
        cmisDocument1.setUploadedRole("INVESTMENT_MANAGER");
        cmisDocument1.setUploadedDate(new DateTime());
        cmisDocument1.setStartDate("17/08/2015 20:27:05");
        cmisDocument1.setEndDate("20/08/2015 20:27:05");
        cmisDocument1.setDocumentTitleCode("IMRCTI");
        cmisDocument1.setFileName("imrcti");
        cmisDocument1.setFileExtension("PDF");
        documents.add(cmisDocument1);


    }


    @Test
    public void testGetDocumentDtoList() {

        List<DocumentDto> dtoList = userDocumentDtoConverter.getDocumentDtoList(documents);
        Assert.assertNotNull(dtoList);
        Assert.assertEquals(documents.size(), 1);
        DocumentDto dto = dtoList.get(0);
        Document document = documents.get(0);
        Assert.assertEquals("IMRCTI_Doc.pdf", dto.getDocumentName());
        Assert.assertEquals(VisibilityRoles.INVESTMENT_MANAGER.getDescription(), dto.getUploadedRole());
        Assert.assertEquals(document.getMimeType(), dto.getFileType());
        Assert.assertEquals(document.getSize(), dto.getSize());
    }

}
