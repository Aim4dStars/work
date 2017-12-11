package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.regularinvestment.v2.model.RIPAction;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.AccountHelper;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Report("ripUpdateAuthorisationReportV2")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public class RipUpdateAuthorisationReport extends RipAuthorisationReport {

    @Autowired
    private RegularInvestmentDtoService regularInvestmentService;

    private static final String DECLARATION = "DS-IP-0080";
    private static final String CANCEL_DECLARATION = "DS-IP-0090";
    private static final String SUSPEND_DECLARATION = "DS-IP-0091";
    private static final String RENEW_DECLARATION = "DS-IP-0092";

    private static final String SUPER_DECLARATION = "DS-IP-0181";
    private static final String SUPER_CANCEL_DECLARATION = "DS-IP-0182";
    private static final String SUPER_SUSPEND_DECLARATION = "DS-IP-0183";
    private static final String SUPER_RENEW_DECLARATION = "DS-IP-0184";

    private static final String REPORT_TYPE = "Client authorisation - regular investment plan";
    private static final String REPORT_TITLE_PREFIX = "Your client authorisation for a ";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private AccountHelper accHelper;

    @Autowired
    private OptionsService optionsService;

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TYPE;
    }

    @Override
    @ReportBean("reportTitle")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        String action = (String) params.get("action");
        return REPORT_TITLE_PREFIX + getCurrentAction(action);
    }

    private String getCurrentAction(String action) {
        String displayValue = "regular investment plan";
        if (action != null) {
            RIPAction ripAction = RIPAction.getRIPAction(action);
            if (RIPAction.SUSPEND == ripAction) {
                displayValue = "suspension";
            } else if (RIPAction.CANCELLED == ripAction) {
                displayValue = "cancellation";
            } else if (RIPAction.RESUME == ripAction) {
                displayValue = "renewal";
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
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        RegularInvestmentDto ripDto = regularInvestmentService.find(key, serviceErrors);

        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));

        return Collections.singletonList(new RegularInvestmentReportData(ripDto, account));
    }

    @Override
    @ReportBean("declaration")
    public String getDeclaration(Map<String, Object> params) {
        String action = (String) params.get("action");
        boolean investorGuideFeature = optionsService.hasFeature(OptionKey.valueOf(OptionNames.DECLARATION_INVESTORGUIDE),
                getAccountKey(params), new FailFastErrorsImpl());

        String declaration = getDeclarationId(action, investorGuideFeature);
        return getContent(declaration);
    }

    private String getDeclarationId(String action, Boolean investorGuideFeature) {
        String declarationId = investorGuideFeature ? DECLARATION : SUPER_DECLARATION;
        if (action != null) {
            RIPAction ripAction = RIPAction.getRIPAction(action);
            if (RIPAction.SUSPEND == ripAction) {
                declarationId = investorGuideFeature ? SUSPEND_DECLARATION : SUPER_SUSPEND_DECLARATION;
            } else if (RIPAction.CANCELLED == ripAction) {
                declarationId = investorGuideFeature ? CANCEL_DECLARATION : SUPER_CANCEL_DECLARATION;
            } else if (RIPAction.RESUME == ripAction) {
                declarationId = investorGuideFeature ? RENEW_DECLARATION : SUPER_RENEW_DECLARATION;
            }
        }
        return declarationId;
    }
}
