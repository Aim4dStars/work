package com.bt.nextgen.reports.account;

import java.util.Map;

import net.sf.jasperreports.engine.Renderable;

import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;

@Report("ripReceiptReport")
public class RipReceiptReport extends RipAuthorisationReport {

    @Autowired
    private RegularInvestmentDtoService regularInvestmentService;

    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return "Receipt";
    }

    /**
     * Gets the sub report name.
     * 
     * @param params
     *            the params
     * @return the sub report name
     */
    @ReportBean("subReportType")
    public String getSubReportName(Map<String, String> params) {

        return "Regular investment plan";
    }

    @ReportBean("regularInvestmentDto")
    public RegularInvestmentDto getRegularInvestment(Map<String, String> params) {

        // Retrieve the regularInvestment based on rip_id.
        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        String ripId = params.get("rip_id");
        OrderGroupKey key = new OrderGroupKey(accountId, ripId);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        RegularInvestmentDto ripDto = regularInvestmentService.find(key, serviceErrors);

        return ripDto;
    }

    @ReportBean("ripStatus")
    public String getInvestmentStatus(Map<String, String> params) {
        RegularInvestmentDto invDto = this.getRegularInvestment(params);

        RIPStatus status = RIPStatus.ACTIVE;
        String strStatus = invDto.getRipStatus();
        for (RIPStatus ripStatus : RIPStatus.values()) {
            if (ripStatus.getDisplayName().equals(strStatus)) {
                status = ripStatus;
            }
        }

        if (RIPStatus.SUSPENDED == status) {
            return "Suspended";
        }

        if (RIPStatus.CANCELLED == status) {
            return "Cancelled";
        }
        return "Submitted";
    }

    @Override
    @ReportImage("paymentFromToIcon")
    public Renderable getPaymentFromToIcon(Map<String, String> params) {
        String imageLocation;
        final String status = getInvestmentStatus(params);
        if ("Suspended".equals(status)) {

            imageLocation = cmsService.getContent("suspendFromToIcon");
        } else if ("Cancelled".equals(status)) {
            imageLocation = cmsService.getContent("cancelFromToIcon");
        } else {
            imageLocation = cmsService.getContent("paymentFromToIcon");
        }
        return getRasterImage(imageLocation);
    }
}
