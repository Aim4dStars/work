package com.bt.nextgen.reports.account.investmentorders.amendorder;

import com.bt.nextgen.api.order.model.ShareOrderDto;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Report("amendOrderReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public class AmendOrderReport extends AccountReportV2 {
    private static final String DISCLAIMER_CONTENT = "DS-IP-0146";
    private static final String DECLARATION_CONTENT = "DS-IP-0084";
    private static final String REPORT_TITLE = "Your client authorisation for an amended order";
    private static final String ORDER_ID_PARAM = "order-id";
    private static final String ORDER_PARAM = "order";
    private static final String ACCOUNT_ID_PARAM = "account-id";

    @Autowired
    private OrderIntegrationService orderIntegrationService;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    @Override
    protected AccountKey getAccountKey(Map<String, Object> params) {
        String encoded = (String) params.get(ACCOUNT_ID_PARAM);
        AccountKey accountKey;

        if (encoded == null) {
            String orderId = (String) params.get(ORDER_ID_PARAM);
            List<Order> orders = orderIntegrationService.loadOrder(orderId, null);
            accountKey = AccountKey.valueOf(orders.get(0).getAccountId());
        } else {
            accountKey = AccountKey.valueOf(EncodedString.toPlainText(encoded));
        }

        return accountKey;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String orderJson = (String) params.get(ORDER_PARAM);
        ShareOrderDto shareOrderDto;
        try {
            String sanitizedOrder = JsonSanitizer.sanitize(orderJson);
            shareOrderDto = mapper.readValue(sanitizedOrder, ShareOrderDto.class);
            return Collections.singletonList(new AmendOrderReportData(shareOrderDto));
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON Message illegal", e);
        }
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getContent(DISCLAIMER_CONTENT);
    }

    @ReportBean("declaration")
    public String getDeclaration(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getContent(DECLARATION_CONTENT);
    }

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
