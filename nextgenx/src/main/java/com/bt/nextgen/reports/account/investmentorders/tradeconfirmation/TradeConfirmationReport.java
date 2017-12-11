package com.bt.nextgen.reports.account.investmentorders.tradeconfirmation;

import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.api.order.service.TradeOrderDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public abstract class TradeConfirmationReport extends AccountReportV2 {

    private static final String REPORT_TITLE = "Transaction confirmation";
    private static final String ORDER_ID = "order-id";
    private static final String ACCOUNT_ID = "account-id";

    @Autowired
    private OrderIntegrationService orderIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private TradeOrderDtoService tradeOrderDtoService;

    @Override
    protected AccountKey getAccountKey(Map<String, Object> params) {
        String encoded = (String) params.get(ACCOUNT_ID);
        AccountKey accountKey;

        if (encoded == null) {
            String orderId = (String) params.get(ORDER_ID);
            List<OrderTransaction> orderTransactions = orderIntegrationService.loadTransactionData(orderId,
                    new ServiceErrorsImpl());
            accountKey = AccountKey.valueOf(orderTransactions.get(0).getAccountId());
        } else {
            accountKey = AccountKey.valueOf(EncodedString.toPlainText(encoded));
        }

        return accountKey;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String orderId = (String) params.get(ORDER_ID);
        OrderKey key = new OrderKey(orderId);
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, tradeOrderDtoService, key).performOperation();
        TradeOrderDto tradeOrderDto = (TradeOrderDto) response.getData();
        return Collections.singletonList(new TradeConfirmationReportData(tradeOrderDto));
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params, Map<String, Object> dataCollections) {
        String adviser = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            adviser = getAccount(getAccountKey(params), dataCollections, getServiceType(params)).getAdviserName();
        }

        return getContent(getDisclaimerContent(), new String[] { adviser });
    }

    public abstract String getDisclaimerContent();

    @ReportBean("title")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }

    @SuppressWarnings("squid:S1172")
    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return REPORT_TITLE;
    }
}
