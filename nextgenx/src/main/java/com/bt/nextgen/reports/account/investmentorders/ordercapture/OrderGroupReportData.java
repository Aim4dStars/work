package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import ch.lambdaj.function.matcher.Predicate;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderSummaryDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.selectFirst;

public class OrderGroupReportData {
    private OrderGroupDto orderGroup;
    private List<OrderItemData> orderItems = null;
    private OrderSummaryDto orderSummary;

    public OrderGroupReportData(OrderGroupDto orderGroup, OrderSummaryDto orderSummary) {
        this.orderGroup = orderGroup;
        this.orderSummary = orderSummary;
        orderItems = new ArrayList<>();
        for (OrderItemDto orderItem : orderGroup.getOrders()) {
            orderItems.add(new OrderItemData(orderGroup, orderItem));
        }
    }

    public String getTotalBuys() {
        return ReportFormatter.format(ReportFormat.CURRENCY, orderSummary.getTotalBuys());
    }

    public String getTotalSells() {
        return ReportFormatter.format(ReportFormat.CURRENCY, orderSummary.getTotalSells());
    }

    public String getNetCashMovement() {
        return ReportFormatter.format(ReportFormat.CURRENCY, orderSummary.getNetCashMovement());
    }

    public List<OrderItemData> getChildren() {
        return orderItems;
    }

    public boolean isPortfolioFeePresent() {
        boolean isPortfolioFeePresent = false;
        if (orderItems != null) {
            for (OrderItemData orderItem : orderItems) {
                if (orderItem.getPercentageFee() != null || orderItem.getSlidingScaleTierData().size() > 0) {
                    isPortfolioFeePresent = true;
                    break;
                }
            }
        }
        return isPortfolioFeePresent;
    }

    public List<String> getWarnings() {
        List<String> warnings = new ArrayList<>();
        for (DomainApiErrorDto domainError : orderGroup.getWarnings()) {
            if ("".equalsIgnoreCase(domainError.getDomain())) {
                warnings.add(domainError.getMessage());
            }
        }
        return warnings;
    }

    public boolean hasShareAssets() {
        return selectFirst(orderGroup.getOrders(), new Predicate<OrderItemDto>() {
            public boolean apply(OrderItemDto orderItem) {
                return orderItem.getAsset() instanceof ShareAssetDto;
            }
        }) != null;
    }

    public boolean hasPortfolioAssets() {
        return selectFirst(orderGroup.getOrders(), new Predicate<OrderItemDto>() {
            public boolean apply(OrderItemDto orderItem) {
                return orderItem.getAsset() instanceof ManagedFundAssetDto
                        || orderItem.getAsset() instanceof ManagedPortfolioAssetDto;
            }
        }) != null;
    }
}
