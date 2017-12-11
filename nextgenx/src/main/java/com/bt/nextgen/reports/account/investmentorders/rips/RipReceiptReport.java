package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Report("ripReceiptReportV2")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public class RipReceiptReport extends RipAuthorisationReport {

    private static final Logger logger = LoggerFactory.getLogger(RipReceiptReport.class);

    private static final String REPORT_TYPE = "Receipt - regular investment plan";
    private static final String REPORT_TITLE_PREFIX = "Your regular investment plan was successfully ";
    private static final String RIP_DATA_KEY = "RipReceiptReport.ripData.";

    @Autowired
    private RegularInvestmentDtoService regularInvestmentService;

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TYPE;
    }

    @Override
    @ReportBean("reportTitle")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get("account-id");
        String ripId = (String) params.get("rip_id");

        OrderGroupKey key = new OrderGroupKey(accountId, ripId);
        RegularInvestmentDto ripDto = getRipData(key, dataCollections);

        return REPORT_TITLE_PREFIX + getCurrentStatus(ripDto.getRipStatus());
    }

    private String getCurrentStatus(String status) {
        String displayValue = "submitted";
        if (status != null) {
            RIPStatus ripStatus = RIPStatus.forDisplay(status);
            if (RIPStatus.SUSPENDED.equals(ripStatus)) {
                displayValue = "suspended";
            } else if (RIPStatus.CANCELLED.equals(ripStatus)) {
                displayValue = "cancelled";
            }
        }
        return displayValue;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        // Load details for existing RIP using ID
        String accountId = (String) params.get("account-id");
        String ripId = (String) params.get("rip_id");

        OrderGroupKey key = new OrderGroupKey(accountId, ripId);
        RegularInvestmentDto ripDto = getRipData(key, dataCollections);

        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));

        return Collections.singletonList(new RegularInvestmentReportData(ripDto, account));
    }

    private RegularInvestmentDto getRipData(OrderGroupKey key, Map<String, Object> dataCollections) {
        String cacheKey = RIP_DATA_KEY + key.getAccountId();
        synchronized (dataCollections) {
            RegularInvestmentDto ripData = (RegularInvestmentDto) dataCollections.get(cacheKey);
            if (ripData == null) {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                ripData = regularInvestmentService.find(key, serviceErrors);
                dataCollections.put(cacheKey, ripData);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of rip receipt report: {}", serviceErrors.getErrorList());
                }
            }
            return ripData;
        }
    }
}
