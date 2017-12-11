package com.bt.nextgen.reports.account.drawdownstrategy;

import com.bt.nextgen.api.drawdown.v2.model.AssetPriorityDto;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DrawdownStrategyReportDataConverter {

    public DrawdownStrategyReportData toReportData(DrawdownStrategy drawdownStrategy, DrawdownDetailsDto details) {
        List<AssetPriorityReportData> assetPriorityReportData = null;

        if (DrawdownStrategy.ASSET_PRIORITY.equals(drawdownStrategy)) {
            assetPriorityReportData = toAssetPriorityReportData(details);
        }

        return new DrawdownStrategyReportData(drawdownStrategy, assetPriorityReportData);
    }

    private List<AssetPriorityReportData> toAssetPriorityReportData(DrawdownDetailsDto details) {
        List<AssetPriorityReportData> assetPriorityReportData = new ArrayList<>();

        List<AssetPriorityDto> priorityDtoList = details.getPriorityDrawdownList();
        Collections.sort(priorityDtoList, new Comparator<AssetPriorityDto>() {
            @Override
            public int compare(AssetPriorityDto arg0, AssetPriorityDto arg1) {
                return arg0.getDrawdownPriority().compareTo(arg1.getDrawdownPriority());
            }
        });

        for (AssetPriorityDto assetPriority : priorityDtoList) {
            AssetPriorityReportData reportData = new AssetPriorityReportData(assetPriority.getAssetCode(),
                    assetPriority.getAssetName(), assetPriority.getStatus(), assetPriority.getMarketValue(),
                    assetPriority.getDrawdownPriority());
            assetPriorityReportData.add(reportData);
        }

        return assetPriorityReportData;
    }
}
