package com.bt.nextgen.core.reporting.view;

import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.ReportSource;
import com.bt.nextgen.core.reporting.ReportTemplate;
import com.bt.nextgen.core.reporting.datasource.ReportDatasource;

import net.sf.jasperreports.engine.JasperReport;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ReportViewImpl implements ReportView {
    private ReportSource reportSource;

    private ReportTemplate reportTemplate;

    private ReportDatasource dataSource;

    private List<ReportDatasource> dataSources;

    @Override
    public void init(ReportIdentity reportId, ReportSource reportSource) {
        this.reportSource = reportSource;
        this.reportTemplate = reportSource.getReportTemplate(reportId);
    }

    @Override
    public ReportSource getReportSource() {
        return reportSource;
    }

    @Override
    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    @Override
    public abstract Map<String, ViewConfig> getViewConfigs();

    public void addSubreport(String id, JasperReport subreport) {
        getReportDataSource().getParameters().put(id, subreport);
    }

    @Override
    public ReportDatasource getReportDataSource() {
        return dataSource;
    }

    @Override
    public void setReportDataSource(ReportDatasource dataSource) {
        this.dataSource = dataSource;
    }

    protected List<ReportDatasource> getReportDataSources() {
        return dataSources;
    }

    @Override
    public void setReportDataSources(List<ReportDatasource> dataSources) {
        this.dataSources = dataSources;
    }

    protected List<DataSourceField> generateDataSourceFields(Class<?> sourceClass) {
        List<DataSourceField> fields = new ArrayList<DataSourceField>();
        for (Method method : sourceClass.getMethods()) {
            // Pick up getters with no params only
            if (method.getName().indexOf("get") == 0 && method.getParameterTypes().length == 0
                    && !"getClass".equals(method.getName()) && !"void".equals(method.getReturnType().getName())) {
                String fieldName = method.getName().replace("get", "");
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);

                fields.add(new DataSourceFieldImpl(fieldName, method.getReturnType()));
            }
        }
        return fields;
    }
}
