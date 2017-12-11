package com.bt.nextgen.reports.account;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.order.model.OrderDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.service.OrderDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;

@Report("orderCancelReport")
public class OrderCancelReport extends AbstractOrderReport {

    private static final String DECLARATION_LISTED_SECURITY = "DS-IP-0083";
    private static final String DECLARATION_MANAGED_FUND = "DS-IP-0082";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0146";

    private static final String REPORT_TYPE = "Client authorisation";
    private static final String SUB_REPORT_TYPE = "order cancellation";

    @Autowired
    private OrderDtoService orderDtoService;

    @Autowired
    @Qualifier("avaloqOrderIntegrationService")
    private OrderIntegrationService orderIntegrationService;

    public OrderCancelReport() {
        super(REPORT_TYPE, SUB_REPORT_TYPE, DECLARATION_MANAGED_FUND);
    }

    @ReportBean("order")
    public OrderDto getOrder(Map<String, String> params) {
        String orderId = params.get(ORDER_ID_PARAM);
        OrderKey key = new OrderKey(orderId);
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, orderDtoService, key).performOperation();
        OrderDto orderDto = (OrderDto) response.getData();
        return orderDto;
    }

    @Override
    protected AccountKey getAccountKey(Map<String, String> params) {
        String orderId = params.get(ORDER_ID_PARAM);
        OrderKey key = new OrderKey(orderId);

        List<Order> orders;
        orders = orderIntegrationService.loadOrder(key.getOrderId(), null);
        if (!orders.isEmpty()) {
            Order order = orders.get(0);
            if (null != order && null != order.getAccountId()) {
                return new AccountKey(EncodedString.fromPlainText(order.getAccountId()).toString());
            }
        }
        return null;
    }

    /**
     * Gets the declaration. The declaration is based on the order's asset type.
     *
     * @param params
     *            the params
     * @return the declaration
     */
    @ReportBean("declaration")
    @Override
    public String getDeclaration(Map<String, String> params) {
        OrderDto order = getOrder(params);
        ContentKey key = new ContentKey(DECLARATION_MANAGED_FUND);
        if (AssetType.SHARE.getDisplayName().equals(order.getAsset().getAssetType())) {
            key = new ContentKey(DECLARATION_LISTED_SECURITY);
        }
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }
}
