package com.bt.nextgen.reports.account.movemoney;

import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import net.sf.jasperreports.engine.Renderable;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Bean class for cash transaction(payment/deposit) receipt pdf generation
 */

@Report(value = "cashTransactionReceipt", filename = "Transaction receipt")
public class TransactionReceiptReport extends AccountReportV2 {

    private static final String CACHE_KEY = "cashTransaction";
    private static final String DEFAULT_TITLE = "Transaction receipt";
    private static final String PARAM_RECEIPT_ID = "receipt-id";
    private static final String SIMPLE_TRANSACTION_TYPE = "Investment";

    @Autowired
    private TransactionReceiptHelper transactionReceiptHelper;

    @Autowired
    private AccountProductsHelper accountProductsHelper;

    @Autowired
    private CmsService cmsService;

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        final String receiptNo = EncodedString.toPlainText((String) params.get(PARAM_RECEIPT_ID));
        final TransactionReceiptReportData data = transactionReceiptHelper.getReceiptData(receiptNo);
        if (data != null) {
            // Set transactionType to Investment for Simple users
            final WrapAccountDetail account = getAccount(getAccountKey(params), dataCollections, getServiceType(params));
            final String subscriptionType = accountProductsHelper.getSubscriptionType(account, new ServiceErrorsImpl());
            if (DirectOffer.SIMPLE.getSubscriptionType().equals(subscriptionType)) {
                data.setTransactionType(SIMPLE_TRANSACTION_TYPE);
            }

            dataCollections.put(CACHE_KEY + receiptNo, data);
            return Collections.singletonList(data);
        }
        return new ArrayList<>();
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        synchronized (dataCollections) {
            final String receiptNo = EncodedString.toPlainText((String) params.get(PARAM_RECEIPT_ID));
            if (dataCollections.containsKey(CACHE_KEY + receiptNo)) {
                final TransactionReceiptReportData reportData = (TransactionReceiptReportData) dataCollections.get(CACHE_KEY + receiptNo);
                return reportData.getTransactionType() + " receipt";
            }
            return DEFAULT_TITLE;
        }
    }

    @ReportBean("reportTitle")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getReportType(params, dataCollections);
    }

    @ReportImage("fromToIcon")
    public Renderable getFromToIcon(Map<String, Object> params) throws IOException {
        return getRasterImage(cmsService.getContent("paymentFromToIcon"));
    }
}
