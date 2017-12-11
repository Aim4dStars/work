package com.bt.nextgen.reports.account.performance;

import com.bt.nextgen.core.reporting.stereotype.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewImpl;
import com.bt.nextgen.core.reporting.view.ViewColumnConfig.Markup;
import com.bt.nextgen.core.reporting.view.ViewColumnConfigImpl;
import com.bt.nextgen.core.reporting.view.ViewConfig;
import com.bt.nextgen.core.reporting.view.ViewConfigImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReportView("modelPerformanceReportView")
public class ModelPerformanceReportView extends ReportViewImpl {
    private static int PAGE_WIDTH = 760;

    @Override
    public Map<String, ViewConfig> getViewConfigs() {
        Map<String, ViewConfig> configs = new HashMap<String, ViewConfig>();

        List<AccountPerformanceTypeData> data = (List<AccountPerformanceTypeData>) getReportDataSource().getBeans();

        configs.put("performance", getPerformanceViewConfigs(data.get(0)));
        configs.put("netReturn", getPerformanceViewConfigs(data.get(1)));
        return configs;
    }

    private ViewConfig getPerformanceViewConfigs(AccountPerformanceTypeData performanceData) {
        ViewConfigImpl config = new ViewConfigImpl();

        // Generate data source field mapping
        config.getDataSourceFields().addAll(generateDataSourceFields(PerformanceRowData.class));

        List<String> colHeaders = performanceData.getHeaders();
        int colCount = colHeaders.size();
        int fixWidth = 110;
        
        int[] widths = getColumnsWidth(colCount, fixWidth);

        config.addViewColumnConfig(new ViewColumnConfigImpl(colHeaders.get(0), "$F{description}", fixWidth));

        for (int i = 1; i < colHeaders.size(); i++) {
            ViewColumnConfigImpl colConfig = new ViewColumnConfigImpl(colHeaders.get(i), "$F{dataPeriod" + i + "}",
                    i == colHeaders.size() - 1 ? widths[1] : widths[0]);
            colConfig.setHeaderMarkup(Markup.HTML);
            config.addViewColumnConfig(colConfig);
        }

        return config;
    }


    private int[] getColumnsWidth(int colCount, int fixWidth) {
        int mod = 40;
        int width = (PAGE_WIDTH - fixWidth) / (colCount - 1);
        if (width < mod) {
            width = (PAGE_WIDTH) / (colCount - 2);
        } else {
            mod = width;
        }

        int[] widths = { width, mod + 8 };

        return widths;
    }
}
