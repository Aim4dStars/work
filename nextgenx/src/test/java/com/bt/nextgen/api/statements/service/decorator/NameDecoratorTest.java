package com.bt.nextgen.api.statements.service.decorator;

import com.bt.nextgen.api.statements.decorator.NameDecorator;
import com.bt.nextgen.api.statements.decorator.RoleDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by L062605 on 19/08/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class NameDecoratorTest {

    NameDecorator nameDecorator;

    @Mock
    RoleDecorator roleDecorator;

    @Test
    public void decoratorTest() {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setDocumentTitleCode("STMANN");
        cmisDocument.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        cmisDocument.setStartDate("2015-08-17T20:27:05.000+11:00");
        cmisDocument.setMimeType("application/pdf");
        cmisDocument.setEndDate("2015-08-20T20:27:05.000+11:00");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        DocumentDto dto = nameDecorator.decorate();

        Assert.assertEquals("Annual investment statement (17 Aug 2015 - 20 Aug 2015).pdf", dto.getDocumentName());

        cmisDocument.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        cmisDocument.setDocumentTitleCode(null);
        cmisDocument.setStartDate("2015-08-17T20:27:05.000+11:00");
        cmisDocument.setMimeType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        cmisDocument.setEndDate("2015-08-20T20:27:05.000+11:00");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        dto = nameDecorator.decorate();
        Assert.assertEquals("Statement (17 Aug 2015 - 20 Aug 2015).pptx", dto.getDocumentName());

        Mockito.when(roleDecorator.decorate()).thenReturn(dto);
        cmisDocument.setDocumentType(DocumentCategories.INVESTMENTS.getCode());
        cmisDocument.setDocumentName("Abc.pdf");
        cmisDocument.setMimeType("application/pdf");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        dto = nameDecorator.decorate();
        Assert.assertEquals("Abc.pdf", dto.getDocumentName());


        cmisDocument.setDocumentType(DocumentCategories.INVESTMENTS.getCode());
        cmisDocument.setDocumentName("Abc.txt");
        nameDecorator = new NameDecorator(roleDecorator, new DocumentDto(), cmisDocument);
        dto = nameDecorator.decorate();
        Mockito.verify(roleDecorator, Mockito.times(1)).decorate();
    }

    @Test
    public void testFilesWithoutExtension() {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setDocumentType(DocumentCategories.SMSF.getCode());
        cmisDocument.setDocumentName("mydoc");
        cmisDocument.setMimeType("application/pdf");
        cmisDocument.setEndDate("2015-08-20T20:27:05.000+11:00");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        DocumentDto dto = nameDecorator.decorate();
        Assert.assertEquals("mydoc.pdf", dto.getDocumentName());

        cmisDocument.setDocumentName("mydoc.pdf");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        dto = nameDecorator.decorate();
        Assert.assertEquals("mydoc.pdf", dto.getDocumentName());
    }


    @Test
    public void decoratorStatement_SuperSTM_Test() {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setDocumentTitleCode("SPCENT");
        cmisDocument.setDocumentName("superstatements");
        cmisDocument.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        cmisDocument.setStartDate("2015-08-17T20:27:05.000+11:00");
        cmisDocument.setMimeType("application/pdf");
        cmisDocument.setEndDate("2015-08-20T20:27:05.000+11:00");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        DocumentDto dto = nameDecorator.decorate();
        Assert.assertEquals("superstatements.pdf", dto.getDocumentName());
    }

    @Test
    public void decoratorStatement_SuperSPRBEN_Test() {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setDocumentTitleCode("SPRBEN");
        cmisDocument.setDocumentName("Rollover Benefit Statement");
        cmisDocument.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        cmisDocument.setStartDate("2015-08-17T20:27:05.000+11:00");
        cmisDocument.setMimeType("application/pdf");
        cmisDocument.setEndDate("2015-08-20T20:27:05.000+11:00");
        nameDecorator = new NameDecorator(new DocumentDto(), cmisDocument);
        DocumentDto dto = nameDecorator.decorate();
        Assert.assertEquals("Rollover Benefit Statement.pdf", dto.getDocumentName());
    }
}
