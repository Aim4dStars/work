package com.bt.nextgen.core.reporting.view;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;

import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.ReportSource;
import com.bt.nextgen.core.reporting.ReportTemplate;
import com.bt.nextgen.core.reporting.datasource.ReportDatasource;

public interface ReportView {
    void init(ReportIdentity reportId, ReportSource reportSource);

    ReportTemplate getReportTemplate();

    ReportDatasource getReportDataSource();

    void setReportDataSource(ReportDatasource datasource);

    void addSubreport(String id, JasperReport subreport);

    void setReportDataSources(List<ReportDatasource> datasource);

    ReportSource getReportSource();

    Map<String, ViewConfig> getViewConfigs();
}
