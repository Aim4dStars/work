package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.order.model.OrderFeeDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.SlidingScaleTierDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.integration.order.PriceType;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderItemData {

    private final OrderItemDto orderItem;
    private final List<String> warnings = new ArrayList<>();

    public OrderItemData(OrderGroupDto orderGroup, OrderItemDto orderItem) {
        this.orderItem = orderItem;
        if (orderGroup.getWarnings() != null) {
            for (DomainApiErrorDto domainError : orderGroup.getWarnings()) {
                if (domainError.getDomain().equalsIgnoreCase(orderItem.getAsset().getAssetId())) {
                    warnings.add(domainError.getMessage());
                }
            }
        }
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getOrderType() {
        return StringUtils.capitalize(orderItem.getOrderType());
    }

    public String getRawAssetName() {
        return orderItem.getAsset().getAssetName();
    }

    public String getAssetCode() {
        return orderItem.getAsset().getAssetCode();
    }

    public String getAssetName() {
        String assetName = null;
        if (orderItem.getAsset() instanceof TermDepositAssetDto) {
            assetName = buildTermDepositAssetName(orderItem);
        } else if(orderItem.getAsset() instanceof TermDepositAssetDtoV2){
            assetName = buildTermDepositAssetNameFromV2(orderItem);
        }
         else if (orderItem.getAsset() instanceof ManagedFundAssetDto) {
            ManagedFundAssetDto managedFundAsset = (ManagedFundAssetDto) orderItem.getAsset();
            assetName = buildAssetName(managedFundAsset);
        } else if (orderItem.getAsset() instanceof ShareAssetDto) {
            ShareAssetDto shareAsset = (ShareAssetDto) orderItem.getAsset();
            assetName = buildAssetName(shareAsset);
        } else if (orderItem.getAsset() instanceof ManagedPortfolioAssetDto) {
            ManagedPortfolioAssetDto managedPortfolioAsset = (ManagedPortfolioAssetDto) orderItem.getAsset();
            assetName = buildAssetName(managedPortfolioAsset);
        }
        return assetName;
    }

    private String buildTermDepositAssetNameFromV2(OrderItemDto orderItem) {
        StringBuilder builder = new StringBuilder();
        TermDepositAssetDtoV2 termDepositAssetDto = (TermDepositAssetDtoV2) orderItem.getAsset();
        return builder.append(termDepositAssetDto.getAssetName()).append(" ").append(termDepositAssetDto.getTerm())
                .append(" months term deposit").toString();
    }

    public String buildAssetName(AssetDto assetDto) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(assetDto.getAssetCode())) {
            builder.append("<b>");
            builder.append(assetDto.getAssetCode());
            builder.append(" &#183 ");
            builder.append("</b> ");
        }
        return builder.append(" ").append(assetDto.getAssetName()).toString();
    }

    public String buildTermDepositAssetName(OrderItemDto orderItem) {
        StringBuilder builder = new StringBuilder();
        TermDepositAssetDto termDepositAssetDto = (TermDepositAssetDto) orderItem.getAsset();
        return builder.append(termDepositAssetDto.getAssetName()).append(" ").append(termDepositAssetDto.getTerm())
                .append(" months term deposit").toString();
    }

    public String getRate() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, orderItem.getIntRate());
    }

    public String getInterestPaymentFrequency() {
        if (orderItem.getAsset() instanceof TermDepositAssetDto) {
            TermDepositAssetDto termDepositAssetDto = (TermDepositAssetDto) orderItem.getAsset();
            return termDepositAssetDto.getInterestPaymentFrequency();
        }else if(orderItem.getAsset() instanceof TermDepositAssetDtoV2) {
            TermDepositAssetDtoV2 termDepositAssetDto = (TermDepositAssetDtoV2) orderItem.getAsset();
            return termDepositAssetDto.getInterestPaymentFrequency();
        }
        return null;
    }


    public String getAssetType() {
        return orderItem.getAssetType();
    }

    public String getRawAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, orderItem.getAmount());
    }

    public String getAmount() {
        if (Boolean.TRUE.equals(orderItem.getSellAll())) {
            return "Sell all";
        } else {
            return ReportFormatter.format(ReportFormat.CURRENCY, orderItem.getAmount());
        }
    }

    public String getDistributionMethod() {
        return orderItem.getDistributionMethod();
    }

    public String getTransactionFee() {
        BigDecimal adminFeeRate = orderItem.getAdminFeeRate() != null ? orderItem.getAdminFeeRate().abs() : BigDecimal.ZERO;
        String transactionFee = ReportFormatter.format(ReportFormat.CURRENCY, adminFeeRate);
        if (orderItem.getAsset() instanceof ShareAssetDto) {
            transactionFee += "*";
        }
        return transactionFee;
    }

    public String getPriceType() {
        return PriceType.forIntlId(orderItem.getPriceType()).getDisplayName();
    }

    public String getExpiry() {
        return orderItem.getExpiry();
    }

    public String getUnits(){
        String units = ReportFormatter.format(ReportFormat.UNITS, BigDecimal.valueOf(orderItem.getUnits().doubleValue()));
        if(orderItem.getSellAll()){
            units = "Sell all (" + units
                    + ")";
        }
        return units;
    }

    public String getLimitPrice() {
        BigDecimal price = null;
        String limitPrice = null;
        if ("lim".equals(orderItem.getPriceType())) {
            limitPrice = ReportFormatter.format(ReportFormat.LS_PRICE, orderItem.getPrice());
        } else {
            limitPrice = ReportFormatter.format(ReportFormat.LS_PRICE, price);
        }
        return limitPrice;
    }

    public String getEstimated() {
        return ReportFormatter.format(ReportFormat.CURRENCY, orderItem.getEstimated());
    }

    public Boolean getIsEstimated() {
        Boolean isEstimated = false;
        if (Boolean.TRUE.equals(orderItem.getSellAll()) || orderItem.getAsset() instanceof ShareAssetDto) {
            isEstimated = true;
        }
        return isEstimated;
    }

    public String getOrderId() {
        return orderItem.getOrderId();
    }

    public String getPercentageFee() {
        String percentFee = null;
        List<OrderFeeDto> fees = orderItem.getFees();
        for (OrderFeeDto fee : fees) {
            if (fee.getStructure() == FeesComponentType.PERCENTAGE_FEE) {
                percentFee = ReportFormatter.format(ReportFormat.PERCENTAGE, fee.getPercentFee().getRate());
            }
        }
        return percentFee;
    }

    public Boolean getShowDistributionMethod() {
        if ("sell".equalsIgnoreCase(orderItem.getOrderType()) || StringUtils.isEmpty(getDistributionMethod())) {
            return false;
        }
        return true;
    }

    public List<SlidingScaleTierData> getSlidingScaleTierData(){
        List<SlidingScaleTierData> slidingScaleTiers = new ArrayList<>();
        List<OrderFeeDto> fees = orderItem.getFees();
        for (OrderFeeDto fee : fees) {
            if (fee.getStructure() == FeesComponentType.SLIDING_SCALE_FEE) {
                BigDecimal lowerBound = BigDecimal.ZERO;
                for (SlidingScaleTierDto tier : fee.getSlidingFee().getTiers()) {
                    slidingScaleTiers.add(new SlidingScaleTierData(lowerBound, tier.getUpperBound(), tier.getRate()));
                    lowerBound = tier.getUpperBound();
                }
            }
        }
        return slidingScaleTiers;
    }

    public String getIncomePreference() {
        if (orderItem.getAsset() instanceof ManagedPortfolioAssetDto) {
            if (orderItem.getIncomePreference() != null) {
                return orderItem.getIncomePreference();
            }
        }
        return null;
    }
}
