package com.bt.nextgen.reports.account;

import java.util.Map;

import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

/**
 * The Class OrderReceiptReport generates the necessary report data for the
 * asset order receipt. The data consists of AssetDto.
 */
public class OrderReceiptReport extends OrderReport {

    private static final String FEE_DISCLAIMER_RECEIPT = "DS-IP-0087";
    private static final String REPORT_TYPE = "Order receipt";
    private static final String SUB_REPORT_TYPE = "";

    public OrderReceiptReport() {
        super(REPORT_TYPE, SUB_REPORT_TYPE);
    }

    /**
     * Gets the fee disclaimer.
     * 
     * @param params
     *            the params
     * @return the declaration
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("feeDisclaimer")
    public String getFeeDisclaimer(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(FEE_DISCLAIMER_RECEIPT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }
}
