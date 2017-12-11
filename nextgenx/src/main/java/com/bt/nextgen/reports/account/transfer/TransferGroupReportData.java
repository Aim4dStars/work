package com.bt.nextgen.reports.account.transfer;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferGroupReportData {

    private String orderId;
    private List<TransferCategoryReportData> children = new ArrayList<>();
    private List<DomainApiErrorDto> pageLevelWarnings;
    private TransferSummaryReportData summaryData;
    private boolean hasWarnings = false;
    private String preferenceFlag = "None";
    private String sourceContainerName;
    private String sourceAssetCode;
    private String destContainerName;
    private String destAssetCode;
    private String holdingValue;
    private String incomePreference;
    private String cashAssetName;

    public TransferGroupReportData(InspecieTransferDto dto, Map<String, Object> params) {
        this.pageLevelWarnings = dto.getWarnings();
        this.holdingValue = (String) params.get("holdingvalue");
        this.preferenceFlag = (String) params.get("preference");
        this.destAssetCode = (String) params.get("destAssetCode");
        this.destContainerName = (String) params.get("destAssetName");
        this.sourceContainerName = (String) params.get("sourceAssetName");
        this.sourceAssetCode = (String) params.get("sourceAssetCode");
        this.orderId = dto.getKey() == null ? "--" : dto.getKey().getTransferId();
        this.incomePreference = (String) params.get("incomePreference");
        this.cashAssetName = (String) params.get("cashAssetName");

        // retrieve warnings
        processTransferAsset(dto.getTransferAssets());
    }

    private void processTransferAsset(List<TransferAssetDto> assetDtoList) {
        Map<String, TransferCategoryReportData> map = new HashMap<>();
        BigDecimal totalTransferAmount = BigDecimal.ZERO;
        BigDecimal nonCashTransferAmount = BigDecimal.ZERO;
        for (TransferAssetDto aDto : assetDtoList) {
            String assetType = aDto.getAsset().getAssetType();
            if (AssetType.CASH.getGroupDescription().equalsIgnoreCase(assetType) || aDto.getIsCashTransfer()) {
                totalTransferAmount = totalTransferAmount.add(aDto.getQuantity());
                assetType = AssetType.CASH.getGroupDescription();
                if (!aDto.getIsCashTransfer()) {
                    nonCashTransferAmount = nonCashTransferAmount.add(aDto.getQuantity());
                } else {
                    continue;
                }
            } else {
                String amt = aDto.getAmount().replaceAll("[^\\d.]+", "");
                BigDecimal dollarAmount = BigDecimal.valueOf(Double.parseDouble(amt));
                totalTransferAmount = totalTransferAmount.add(dollarAmount);
                nonCashTransferAmount = nonCashTransferAmount.add(dollarAmount);
                this.hasWarnings = this.hasWarnings || (aDto.getVettWarnings() != null && !aDto.getVettWarnings().isEmpty());
            }

            if (!map.containsKey(assetType)) {
                TransferCategoryReportData categoryData = new TransferCategoryReportData(assetType,
                        new ArrayList<TransferAssetReportData>());
                map.put(assetType, categoryData);
            }
            map.get(assetType).getChildren().add(new TransferAssetReportData(aDto));
        }

        this.children = new ArrayList<TransferCategoryReportData>();
        getTransferAssetOnTypes(AssetType.SHARE, map);
        getTransferAssetOnTypes(AssetType.MANAGED_FUND, map);
        getTransferAssetOnTypes(AssetType.CASH, map);

        // Add in summary data
        this.summaryData = new TransferSummaryReportData(totalTransferAmount, nonCashTransferAmount);
        List<TransferAssetReportData> summaryList = new ArrayList<>();
        summaryList.add(new TransferAssetReportData("Estimated transfer amount", getNonCashTransferAmount()));
        summaryList.add(new TransferAssetReportData(cashAssetName + " transfer", getCashOnlyTransferAmount()));
        TransferCategoryReportData categoryData = new TransferCategoryReportData("Transfer summary", summaryList);
        this.children.add(categoryData);

    }

    public String getOrderId() {
        return orderId;
    }

    public String getPortfolioValue() {
        return holdingValue;
    }

    public String getPreferenceFlag() {
        return preferenceFlag;
    }

    public List<TransferCategoryReportData> getChildren() {
        return children;
    }

    public TransferSummaryReportData getTransferSummary() {
        return summaryData;
    }

    public String getTotalTransferAmount() {
        return summaryData.getTotalTransferAmount();
    }

    public String getCashOnlyTransferAmount() {
        return summaryData.getCashTransferAmount();
    }

    public String getNonCashTransferAmount() {
        return summaryData.getNonCashTransferAmount();
    }

    public TransferSummaryReportData getSummaryData() {
        return summaryData;
    }

    public String getSourceContainerName() {
        return sourceContainerName;
    }

    public String getSourceAssetCode() {
        return sourceAssetCode;
    }

    public String getDestContainerName() {
        return destContainerName;
    }

    public String getDestAssetCode() {
        return destAssetCode;
    }

    @SuppressWarnings("squid:S1452")
    @ReportBean("pageLevelWarnings")
    public Collection<?> getPageLevelWarnings() throws IOException {
        if (pageLevelWarnings != null) {
            return pageLevelWarnings;
        }

        return Collections.emptyList();
    }

    private void getTransferAssetOnTypes(AssetType assetType, Map<String, TransferCategoryReportData> map) {
        if (map.containsKey(assetType.getGroupDescription())) {
            this.children.add(map.get(assetType.getGroupDescription()));
        }
    }

    public boolean getHasWarnings() {
        return hasWarnings;
    }

    public String getIncomePreference() {
        return incomePreference;
    }

    public String getCashAssetName() {
        return cashAssetName;
    }

}
