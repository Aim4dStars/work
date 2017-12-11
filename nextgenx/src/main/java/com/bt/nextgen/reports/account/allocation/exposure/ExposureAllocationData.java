package com.bt.nextgen.reports.account.allocation.exposure;

import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.HoldingAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.TermDepositAssetAllocationByExposureDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.asset.AssetClass;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExposureAllocationData {
    private String exposureType;
    private AllocationByExposureDto exposureDto;
    private List<ExposureAllocationData> children;

    public ExposureAllocationData(AllocationByExposureDto exposureDto, String exposureType) {
        this.children = new ArrayList<>();
        this.exposureType = exposureType;
        this.exposureDto = exposureDto;
        if (exposureDto instanceof KeyedAllocByExposureDto) {
            KeyedAllocByExposureDto keyAllocByExposureDto = (KeyedAllocByExposureDto) exposureDto;
            for (AllocationByExposureDto allocationExposureDto : keyAllocByExposureDto.getAllocations()) {
                this.children.add(new ExposureAllocationData(allocationExposureDto, exposureType));
            }
        } else if (exposureDto instanceof AssetAllocationByExposureDto) {
			List<ExposureAllocationData> holdings = new ArrayList<>();
			Boolean addChildren = false;
            AssetAllocationByExposureDto assetAllocationExposureDto = (AssetAllocationByExposureDto) exposureDto;
            for (AllocationByExposureDto allocationExposureDto : assetAllocationExposureDto.getAllocations()) {
				holdings.add(new ExposureAllocationData(allocationExposureDto,exposureType));
                if (!allocationExposureDto.getName().equals(assetAllocationExposureDto.getName()) || (allocationExposureDto.getSource() != null && assetAllocationExposureDto.getSource() != null && !allocationExposureDto.getSource().equals(assetAllocationExposureDto.getSource()))) {
					addChildren = true;
                }
            }
            if(addChildren){
        	    this.children.addAll(holdings);
			}
        } else if (exposureDto instanceof AggregateAllocationByExposureDto) {
            AggregateAllocationByExposureDto aggregateDto = (AggregateAllocationByExposureDto) exposureDto;
            for (AllocationByExposureDto allocationExposureDto : aggregateDto.getAllocations()) {
                this.children.add(new ExposureAllocationData(allocationExposureDto, exposureType));
            }
        }
    }

    public String getCashValue() {
        return getExposureValue(AssetClass.CASH);
    }

    public String getAustralianShareValue() {
        return getExposureValue(AssetClass.AUSTRALIAN_SHARES);
    }

    public String getInternationalShareValue() {
        return getExposureValue(AssetClass.INTERNATIONAL_SHARES);
    }

    public String getAustralianPropertyValue() {
        return getExposureValue(AssetClass.AUSTRALIAN_PROPERTY);
    }

    public String getInternationalPropertyValue() {
        return getExposureValue(AssetClass.INTERNATIONAL_PROPERTY);
    }

    public String getAustralianFixedValue() {
        return getExposureValue(AssetClass.AUSTRALIAN_FIXED_INTEREST);
    }

    public String getAustralianFloatingValue() {
        return getExposureValue(AssetClass.AUSTRALIAN_FLOATING_INTEREST);
    }

    public String getInternationalFixedValue() {
        return getExposureValue(AssetClass.INTERNATIONAL_FIXED_INTEREST);
    }

    public String getAlternativesValue() {
        return getExposureValue(AssetClass.ALTERNATIVES);
    }

    public String getOtherValue() {
        return getExposureValue(AssetClass.OTHER);
    }

    public String getTotalValue() {
        if ("dollar".equals(exposureType)) {
            return ReportFormatter.format(ReportFormat.CURRENCY, exposureDto.getBalance());
        } else if ("assetPercent".equals(exposureType)) {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, BigDecimal.ONE);
        } else {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, exposureDto.getAccountPercent());
        }
    }

    public String getExposureValue(AssetClass sector) {
        if ("dollar".equals(exposureType)) {
            return getDollarExposureValue(sector);
        } else if ("assetPercent".equals(exposureType)) {
            return getAssetPercentExposureValue(sector);
        } else {
            return getAccountAllocationExposureValue(sector);
        }
    }

    private String getDollarExposureValue(AssetClass sector) {
        Map<String, BigDecimal> dollarMap = exposureDto.getAllocationDollar();
        BigDecimal dollarVal = dollarMap.get(sector.name());
        if (dollarVal.equals(BigDecimal.ZERO)) {
            dollarVal = null;
            return ReportFormatter.format(ReportFormat.CURRENCY, dollarVal);
        } else {
            return ReportFormatter.format(ReportFormat.CURRENCY, dollarVal);
        }
    }

    private String getAssetPercentExposureValue(AssetClass sector) {
        Map<String, BigDecimal> allocationPercentMap = exposureDto.getAssetAllocationPercentage();
        BigDecimal percentageVal = allocationPercentMap.get(sector.name());
        if (percentageVal.equals(BigDecimal.ZERO)) {
            percentageVal = null;
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentageVal);
        } else {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentageVal);
        }
    }

    private String getAccountAllocationExposureValue(AssetClass sector) {
        Map<String, BigDecimal> allocationPercentMap = exposureDto.getAccountAllocationPercentage();
        BigDecimal percentageVal = allocationPercentMap.get(sector.name());
        if (percentageVal.equals(BigDecimal.ZERO)) {
            percentageVal = null;
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentageVal);
        } else {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentageVal);
        }
    }

    public Boolean getIsExternal() {
        return exposureDto.getIsExternal();
    }

    public List<ExposureAllocationData> getChildren() {
        return children;
    }

    public String getName() {
        StringBuilder builder = new StringBuilder();
        if (exposureDto instanceof TermDepositAssetAllocationByExposureDto && !exposureDto.getIsExternal()) {
            builder.append(buildTermDepositAssetName(exposureDto));
        } else {
            builder.append(buildAssetName(exposureDto));
        }
        return builder.toString();
    }

    private String buildTermDepositAssetName(AllocationByExposureDto exposureDto) {
        TermDepositAssetAllocationByExposureDto termDepositExposureDto = (TermDepositAssetAllocationByExposureDto) exposureDto;
        StringBuilder builder = new StringBuilder();
        builder.append(termDepositExposureDto.getName());
        builder.append("<br/><font color=\"#A5A5A5\"> Matures on ");
        builder.append(ReportFormatter.format(ReportFormat.SHORT_DATE, termDepositExposureDto.getMaturityDate())).append("<br/>");
        builder.append(termDepositExposureDto.getTerm()).append(" term interest payment ")
                .append(termDepositExposureDto.getPaymentFrequency()).append("</font>");
        return builder.toString();
    }

    private String buildAssetName(AllocationByExposureDto exposureDto) {
        String assetCode = null;
        String assetName = exposureDto.getName();
        StringBuilder builder = new StringBuilder();
        if (exposureDto instanceof AssetAllocationByExposureDto) {
            assetCode = ((AssetAllocationByExposureDto) exposureDto).getAssetCode();
        } else if (exposureDto instanceof HoldingAllocationByExposureDto) {
            assetCode = ((HoldingAllocationByExposureDto) exposureDto).getAssetCode();
        }
        if (StringUtils.isNotBlank(assetCode)) {
            builder.append("<b>");
            builder.append(assetCode);
            builder.append(" &#183 ");
            builder.append("</b> ");
        }
        if (!children.isEmpty()) {
            builder.append("<b>" + assetName + "</b>");
        } else {
            builder.append(assetName);
        }
        if (exposureDto.getSource() != null) {
            builder.append("<br/>" + exposureDto.getSource());
        }
        return builder.toString();
    }

    public String getTotalBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, exposureDto.getBalance());
    }

    public String getInternalBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, exposureDto.getInternalBalance());
    }

    public String getExternalBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, exposureDto.getExternalBalance());
    }

    public String getAssetBrand() {
        if (exposureDto instanceof TermDepositAssetAllocationByExposureDto) {
            return ((TermDepositAssetAllocationByExposureDto) exposureDto).getBrand();
        }
        return null;
    }

    public String getType() {
        return exposureDto.getType();
    }

    public String getSummaryDescription() {
        return exposureDto.getName() + " Total";
    }

}
