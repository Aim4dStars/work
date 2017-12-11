package com.bt.nextgen.reports.account.movemoney.contribution;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.model.TransactionKey;
import com.bt.nextgen.api.transaction.service.TransactionDtoService;
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

@Report("contributionReceipt")
public class ContributionReceiptReport extends ReceiptReport {

    @Autowired
    private TransactionDtoService transactionService;

    @Autowired
    private ContentDtoService contentService;

    private static final String DISCLAIMER_CONTENT = "DS-IP-0145";

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        TransactionKey transactionKey = new TransactionKey((String) params.get("account-id"),
                (String) params.get("transaction-id"));
        TransactionDto transaction = transactionService.find(transactionKey, new FailFastErrorsImpl());
        return Collections.singletonList(new ReceiptReportData(transaction));
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }
}
