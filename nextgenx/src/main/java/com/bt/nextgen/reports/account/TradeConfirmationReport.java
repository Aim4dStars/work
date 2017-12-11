package com.bt.nextgen.reports.account;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.api.order.service.TradeOrderDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderTransaction;

@Report("tradeConfirmationReport")
public class TradeConfirmationReport extends AccountReport {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0077";
    private static final String ORDER_ID = "order-id";
    private static final String ACCOUNT_ID = "account-id";

    @Autowired
    private TradeOrderDtoService tradeOrderDtoService;

    @Autowired
    private OrderIntegrationService orderIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Override
    @ReportBean("accounts")
    public Collection<AccountDto> getAccount(Map<String, String> params) {
        String accountId = params.get(ACCOUNT_ID);
        String orderId = params.get(ORDER_ID);
        if (accountId != null) {
            return super.getAccount(params);
        } else {
            List<OrderTransaction> orderTransactions = orderIntegrationService.loadTransactionData(orderId,
                    new ServiceErrorsImpl());
            params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING,
                    EncodedString.fromPlainText(orderTransactions.get(0).getAccountId()).toString());
            return super.getAccount(params);
        }
    }

    @ReportBean("tradeOrder")
    public TradeOrderDto getOrder(Map<String, String> params) {
        String orderId = params.get(ORDER_ID);
        OrderKey key = new OrderKey(orderId);
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, tradeOrderDtoService, key).performOperation();
        TradeOrderDto tradeOrderDto = (TradeOrderDto) response.getData();
        return tradeOrderDto;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, String> params) {
        String adviser = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            adviser = getAccount(params).iterator().next().getAdviserName();
        }

        return cmsService.getDynamicContent(DISCLAIMER_CONTENT, new String[] { adviser });
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return "Transaction confirmation";
    }
}
