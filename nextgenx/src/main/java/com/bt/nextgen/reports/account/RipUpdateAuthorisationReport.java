package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.regularinvestment.v2.model.RIPAction;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

@Report("ripUpdateAuthorisationReport")
public class RipUpdateAuthorisationReport extends RipAuthorisationReport {
    @Autowired
    private RegularInvestmentDtoService regularInvestmentService;

    private static final String DECLARATION = "DS-IP-0080";
    private static final String SUPER_DECLARATION = "DS-IP-0181";

    private static final String CANCEL_DECLARATION = "DS-IP-0090";
    private static final String SUPER_CANCEL_DECLARATION = "DS-IP-0182";

    private static final String SUSPEND_DECLARATION = "DS-IP-0091";
    private static final String SUPER_SUSPEND_DECLARATION = "DS-IP-0183";

    private static final String RENEW_DECLARATION = "DS-IP-0092";
    private static final String SUPER_RENEW_DECLARATION = "DS-IP-0184";

    @ReportBean("regularInvestmentDto")
    @Override
    public RegularInvestmentDto getRegularInvestment(Map<String, String> params) throws IOException {

        // Retrieve the regularInvestment based on rip_id.
        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        String ripId = params.get("rip_id");
        OrderGroupKey key = new OrderGroupKey(accountId, ripId);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        RegularInvestmentDto ripDto = regularInvestmentService.find(key, serviceErrors);

        return ripDto;
    }

    @ReportBean("currentAction")
    public String getCurrentAction(Map<String, String> params) throws JRException {
        String strAction = "Regular investment plan";
        String action = params.get("action");

        if (action != null) {
            RIPAction ripAction = RIPAction.getRIPAction(action);
            if (RIPAction.SUSPEND == ripAction) {
                strAction = "Suspension";
            }
            if (RIPAction.CANCELLED == ripAction) {
                strAction = "Cancellation";
            }
            if (RIPAction.RESUME == ripAction) {
                strAction = "Renewal";
            }
        }
        return strAction;
    }

    @Override
    @ReportBean("declaration")
    public String getDescription(Map<String, String> params) {
        Boolean isSuper = Boolean.valueOf(params.get(IS_SUPER));
        String declaration = getDisclaimerId(DECLARATION, isSuper);
        String action = params.get("action");

        if (action != null) {
            RIPAction ripAction = RIPAction.getRIPAction(action);
            if (RIPAction.SUSPEND == ripAction) {
                declaration = getDisclaimerId(SUSPEND_DECLARATION, isSuper);
            }
            if (RIPAction.CANCELLED == ripAction) {
                declaration = getDisclaimerId(CANCEL_DECLARATION, isSuper);
            }
            if (RIPAction.RESUME == ripAction) {
                declaration = getDisclaimerId(RENEW_DECLARATION, isSuper);
            }
        }
        return cmsService.getContent(declaration);
    }

    private String getDisclaimerId(String disclaimerId, Boolean isSuper) {
        String filteredId = disclaimerId;
        if (isSuper) {
            if (DECLARATION.equals(disclaimerId)) {
                filteredId = SUPER_DECLARATION;
            }
            if (SUSPEND_DECLARATION.equals(disclaimerId)) {
                filteredId = SUPER_SUSPEND_DECLARATION;
            }
            if (CANCEL_DECLARATION.equals(disclaimerId)) {
                filteredId = SUPER_CANCEL_DECLARATION;
            }
            if (RENEW_DECLARATION.equals(disclaimerId)) {
                filteredId = SUPER_RENEW_DECLARATION;
            }
        }
        return filteredId;
    }

    @Override
    @ReportImage("paymentFromToIcon")
    public Renderable getPaymentFromToIcon(Map<String, String> params) {
        final String action = params.get("action");
        String imageLocation = null;
        if (action != null) {
            RIPAction ripAction = RIPAction.getRIPAction(action);
            if (RIPAction.SUSPEND == ripAction) {
                imageLocation = cmsService.getContent("suspendFromToIcon");
            }
            if (RIPAction.CANCELLED == ripAction) {
                imageLocation = cmsService.getContent("cancelFromToIcon");
            }
        }
        if (imageLocation == null) {
            imageLocation = cmsService.getContent("paymentFromToIcon");
        }
        return getRasterImage(imageLocation);
    }
}
