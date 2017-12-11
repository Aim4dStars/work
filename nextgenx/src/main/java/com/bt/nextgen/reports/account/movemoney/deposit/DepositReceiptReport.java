package com.bt.nextgen.reports.account.movemoney.deposit;

import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.api.movemoney.v3.service.DepositDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.movemoney.ReceiptReport;
import com.bt.nextgen.reports.account.movemoney.ReceiptReportData;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Report("depositReceipt")
public class DepositReceiptReport extends ReceiptReport {

    private static final String INFO_CONTENT = "Ins-IP-0316";
    private static final String DECLARATION_CONTENT = "DS-IP-0189";

    @Autowired
    private DepositDtoService depositService;

    @Autowired
    private ContentDtoService contentService;

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        DepositKey key = new DepositKey((String) params.get("deposit-id"));
        DepositDto depositDto = depositService.find(key, new FailFastErrorsImpl());
        return Collections.singletonList(new ReceiptReportData(depositDto));
    }

    /**
     * This method returns the declaration
     */
    @ReportBean("declaration")
    public String getDeclaration() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DECLARATION_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    /**
     * This method returns the information message
     */
    @ReportBean("infoMessage")
    public String getInfoMessage() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(INFO_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }
}
