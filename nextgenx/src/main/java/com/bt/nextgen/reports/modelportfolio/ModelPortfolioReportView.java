package com.bt.nextgen.reports.modelportfolio;

import com.bt.nextgen.core.reporting.stereotype.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewImpl;
import com.bt.nextgen.core.reporting.view.ViewConfig;

import java.util.HashMap;
import java.util.Map;

@ReportView("modelPortfolioReportView")
public class ModelPortfolioReportView extends ReportViewImpl
{
	@Override
	public Map <String, ViewConfig> getViewConfigs()
	{
		Map <String, ViewConfig> configs = new HashMap <String, ViewConfig>();

		return configs;
	}
}
