package com.bt.nextgen.api.statements.service.decorator;


import com.bt.nextgen.api.statements.decorator.FundAdminDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.SupplimentaryDocument;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by L075208 on 11/01/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class FundAdminDecoratorTest {

    private FundAdminDecorator decorator;

    @Mock
    private CmsService cmsService;

    @Test
    public void testDecorate_when_documentTiltleCode_matches() throws Exception {
        when(cmsService.getContent("Doc.IP.fa.pack")).thenReturn("ng/test1.pdf");
        when(cmsService.getContent("Doc.IP.fa.gettingStarted")).thenReturn("ng/test2.pdf");
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("2015-08-17T20:27:05.000+11:00");
        document.setDocumentType("SMSF");
        document.setDocumentTitleCode("SMAPAC");
        decorator = new FundAdminDecorator(cmsService, document, null);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList().size(), is(2));
        assertThat(extract(dto.getSupplimentaryDocumentList(), on(SupplimentaryDocument.class).getName()),
                contains("Panorama SMSF Administration Service Guide", "Getting Started Guide"));
        assertThat(extract(dto.getSupplimentaryDocumentList(), on(SupplimentaryDocument.class).getUrl()),
                contains("ng/test1.pdf", "ng/test2.pdf"));

    }


    @Test
    public void testDecorate_when_documentType_isnot_statement() throws Exception {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("17/08/2015 20:27:05");
        document.setDocumentType("SMSF");
        decorator = new FundAdminDecorator(cmsService, document, null);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList(), nullValue());
        verify(cmsService, never()).getContent(Matchers.anyString());

    }

    @Test
    public void testDecorate_when_documentType_isn_statement_not_allLink_found() throws Exception {
        when(cmsService.getContent("Doc.IP.fa.pack")).thenReturn("ng/test1.pdf");
        when(cmsService.getContent("Doc.IP.fa.gettingStarted")).thenReturn(null);
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setEndDate("2015-08-17T20:27:05.000+11:00");
        document.setDocumentType("SMSF");
        document.setDocumentTitleCode("SMAPAC");
        decorator = new FundAdminDecorator(cmsService, document, null);
        DocumentDto dto = decorator.decorate();
        assertThat(dto.getSupplimentaryDocumentList().size(), is(1));
        verify(cmsService, times(2)).getContent(Matchers.anyString());
    }
}
