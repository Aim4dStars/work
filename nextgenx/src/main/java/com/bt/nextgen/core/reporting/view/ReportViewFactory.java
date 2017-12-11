package com.bt.nextgen.core.reporting.view;

import com.bt.nextgen.core.reporting.ReportIdentity;

public interface ReportViewFactory
{
	ReportView createReportView(ReportIdentity reportKey);
}
