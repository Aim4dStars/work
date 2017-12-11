package com.bt.nextgen.reports.account.drawdownstrategy;

import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;

import java.util.List;

public class DrawdownStrategyReportData {

    private DrawdownStrategy drawdownStrategy;
    private List<AssetPriorityReportData> assetPriorityList;

    public DrawdownStrategyReportData(DrawdownStrategy drawdownStrategy, List<AssetPriorityReportData> assetPriorityList) {
        this.drawdownStrategy = drawdownStrategy;
        this.assetPriorityList = assetPriorityList;
    }

    public String getDrawdownStrategy() {
        return drawdownStrategy.getDisplayName();
    }

    public String getDrawdownStrategyDescription() {
        return drawdownStrategy.getDescription();
    }

    public Boolean getDisplayPriorityList() {
        return DrawdownStrategy.ASSET_PRIORITY.equals(drawdownStrategy);
    }

    public List<AssetPriorityReportData> getAssetPriorityList() {
        return assetPriorityList;
    }
}
