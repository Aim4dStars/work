package com.bt.nextgen.api.statements.service.decorator;

import com.bt.nextgen.api.statements.decorator.FinancialYearDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by L062605 on 19/08/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class FinancialYearDecoratorTest {

    FinancialYearDecorator financialYearDecorator;


    @Test
    public void decoratorTest() {
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setDocumentTitleCode("STMANN");
        cmisDocument.setEndDate("2015-08-17T20:27:05.000+11:00");

        financialYearDecorator = new FinancialYearDecorator(new DocumentDto(), cmisDocument);
        DocumentDto dto = financialYearDecorator.decorate();
        Assert.assertEquals("2015/2016", dto.getFinancialYear());

        cmisDocument.setDocumentTitleCode("STMANN");
        cmisDocument.setEndDate("2015-03-20T20:27:05.000+11:00");
        financialYearDecorator = new FinancialYearDecorator(new DocumentDto(), cmisDocument);
        dto=financialYearDecorator.decorate();
        Assert.assertEquals("2014/2015", dto.getFinancialYear());

        cmisDocument.setDocumentTitleCode("STMANN");
        cmisDocument.setEndDate("2015-05-20T20:27:05.000+11:00");
        financialYearDecorator = new FinancialYearDecorator(new DocumentDto(), cmisDocument);
        dto=financialYearDecorator.decorate();
        Assert.assertEquals("2014/2015", dto.getFinancialYear());


    }

    @Test
    public void calculateFinancialYear() {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument = new CmisDocumentImpl();
        cmisDocument.setDocumentTitleCode("STMANN");
        cmisDocument.setEndDate("20/08/2015 20:27:05");

        financialYearDecorator = new FinancialYearDecorator(new DocumentDto(), cmisDocument);
        DateTime endDate = format.parseDateTime("20/08/2015 20:27:05");
        Assert.assertEquals("2015/2016", financialYearDecorator.getFinancialYear(endDate));

        endDate = format.parseDateTime("20/06/2015 20:27:05");
        Assert.assertEquals("2014/2015", financialYearDecorator.getFinancialYear(endDate));

        endDate = format.parseDateTime("30/06/2015 23:59:59");
        Assert.assertEquals("2014/2015", financialYearDecorator.getFinancialYear(endDate));

        endDate = format.parseDateTime("01/07/2015 00:00:00");
        Assert.assertEquals("2015/2016", financialYearDecorator.getFinancialYear(endDate));

        endDate = format.parseDateTime("01/07/2015 00:00:01");
        Assert.assertEquals("2015/2016", financialYearDecorator.getFinancialYear(endDate));


        endDate = format.parseDateTime("01/07/2014 00:00:01");
        Assert.assertEquals("2014/2015", financialYearDecorator.getFinancialYear(endDate));

        endDate = format.parseDateTime("01/07/2012 00:00:01");
        Assert.assertEquals("2012/2013", financialYearDecorator.getFinancialYear(endDate));

        endDate = format.parseDateTime("30/06/2012 23:59:59");
        Assert.assertEquals("2011/2012", financialYearDecorator.getFinancialYear(endDate));

    }
}
