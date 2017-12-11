package com.bt.nextgen.reports.account.investmentorders.amendorder;

import com.bt.nextgen.api.order.model.ShareOrderDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

public class AmendOrderReportData {
    private final ShareOrderDto shareOrderDto;

    public AmendOrderReportData(ShareOrderDto shareOrderDto) {
        this.shareOrderDto = shareOrderDto;
    }

    public String getAssetName() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(shareOrderDto.getAsset().getAssetCode())) {
            builder.append(shareOrderDto.getAsset().getAssetCode());
            builder.append(" &#183 ");
        }
        return builder.append(shareOrderDto.getAsset().getAssetName()).toString();
    }

    public String getDisplayOrderId() {
        return shareOrderDto.getDisplayOrderId();
    }

    public String getOrderType() {
        return shareOrderDto.getOrderType();
    }

    public String getUnfilledAmount() {
        return ReportFormatter.format(ReportFormat.INTEGER,
                shareOrderDto.getQuantity().subtract(BigDecimal.valueOf(shareOrderDto.getFilledQuantity()))) + " units";
    }

    public String getFilledAmount() {
        return "(" + ReportFormatter.format(ReportFormat.INTEGER, shareOrderDto.getFilledQuantity()) + " of "
                + ReportFormatter.format(ReportFormat.INTEGER, shareOrderDto.getQuantity()) + " units already filled)";
    }

    public String getPriceType() {
        return shareOrderDto.getPriceType();
    }

    public String getLimitPrice() {
        return "Limit".equals(shareOrderDto.getPriceType()) && shareOrderDto.getLimitPrice() != null
                ? ReportFormatter.format(ReportFormat.LS_PRICE, shareOrderDto.getLimitPrice())
                : "-";
    }

    public String getExpiry() {
        return "GFD".equals(shareOrderDto.getExpiryType()) ? "Good for day (GFD)" : "Good till cancel (GTC)";
    }
}
