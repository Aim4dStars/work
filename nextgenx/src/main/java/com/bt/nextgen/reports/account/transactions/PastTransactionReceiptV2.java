package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryReportService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.btfin.panorama.core.security.encryption.EncodedString;
import net.sf.jasperreports.engine.Renderable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Java bean class for past transaction receipt pdf generation
 */

@Report("pastTransactionReceiptV2")
public class PastTransactionReceiptV2 extends AccountReportV2 {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String PAYMENT_FROM_TO_ICON = "paymentFromToIcon";
    public static final String TRANSACTION_RECEIPT = "Transaction receipt";

    @Autowired
    private CmsService cmsService;

    @Autowired
    private CashTransactionHistoryReportService pastTransactionReportService;

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {

        String receiptNo = EncodedString.toPlainText((String) params.get("receiptNo"));
        String accountId = (String) params.get("account-id");
        String direction = (String) params.get("direction");
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        DateTime startDateTime = DateUtil.convertToDateTime(startDate, DATE_FORMAT_PATTERN);
        DateTime endDateTime = DateUtil.convertToDateTime(endDate, DATE_FORMAT_PATTERN);

        CashTransactionHistoryDto cashTransactionHistoryDto =
                pastTransactionReportService.retrievePastTransaction(accountId, direction, startDateTime, endDateTime, receiptNo);
        return Collections.singletonList(new PastTransactionReceiptData(cashTransactionHistoryDto));
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return TRANSACTION_RECEIPT;
    }

    @ReportBean("reportTitle")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getReportType(params, dataCollections);
    }

    @ReportImage("fromToIcon")
    public Renderable getFromToIcon(Map<String, Object> params) throws IOException {
        return getRasterImage(cmsService.getContent(PAYMENT_FROM_TO_ICON));
    }
}
