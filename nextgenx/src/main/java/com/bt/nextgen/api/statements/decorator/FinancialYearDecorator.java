package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.integration.financialdocument.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by L062329 on 19/08/2015.
 */
public class FinancialYearDecorator implements DtoDecorator {

    private DocumentDto documentDto;
    private DtoDecorator decorator;
    private Document document;

    public FinancialYearDecorator(DocumentDto documentDto, Document document) {
        this.documentDto = documentDto;
        this.document = document;
    }

    public FinancialYearDecorator(DtoDecorator decorator, DocumentDto documentDto, Document document) {
        this.decorator = decorator;
        this.documentDto = documentDto;
        this.document = document;
    }

    @Override
    public DocumentDto decorate() {
        if (document.getEndDate() != null) {
            documentDto.setFinancialYear(getFinancialYear(DateUtil.convertToDateTime(document.getEndDate(),"yyyy-MM-dd'T'HH:mm:ss.SSSZ" )));
        } else {
            documentDto.setFinancialYear(document.getFinancialYear());
        }
        if (decorator != null) {
            documentDto = decorator.decorate();
        }
        return documentDto;
    }

    public String getFinancialYear(DateTime endDate) {
        DateTime financialYear = ApiFormatter.parseDate("01 Jul " + endDate.getYear()).withTimeAtStartOfDay();
        if (DateTimeComparator.getInstance().compare(financialYear, endDate) <= 0) {
            return financialYear.getYear() + "/" + (financialYear.getYear() + 1);
        }
        return (financialYear.getYear() - 1) + "/" + financialYear.getYear();
    }
}
