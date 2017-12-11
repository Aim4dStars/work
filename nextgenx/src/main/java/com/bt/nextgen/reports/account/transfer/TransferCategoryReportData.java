package com.bt.nextgen.reports.account.transfer;

import java.util.List;

public class TransferCategoryReportData {

    private String category;
    private List<TransferAssetReportData> children;

    public TransferCategoryReportData(String category, List<TransferAssetReportData> children) {
        this.category = category;
        this.children = children;
    }

    public String getCategory() {
        return category;
    }

    public List<TransferAssetReportData> getChildren() {
        return children;
    }

}
