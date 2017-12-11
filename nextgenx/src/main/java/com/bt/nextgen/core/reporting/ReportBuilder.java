package com.bt.nextgen.core.reporting;

import java.io.OutputStream;
import java.util.List;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.view.ReportView;

/**
 * This builder api is used to enable the caller to create/render a report in the chosen format to a particular stream.
 */
public interface ReportBuilder {
    /**
     * Cause this builder to render a report to pdf with the given datasource and to the output stream.
     *
     * @param reportView contains optional view configurations and the template
     * @param out        where the report should be delivered to
     */
    void renderToPdf(ReportView reportView, OutputStream out);

    /**
     * Render multiple views into a single report
     *
     * @param reportViews a list of ReportView
     * @param compress    flag to compress or combine reports.  If true the generated reports will be compressed into a single zip file.
     *                    If false the reports will be combined.
     * @param out         where the report should be delivered to
     */
    void renderToPdf(List<ReportView> reportViews, boolean compress, OutputStream out);

    /**
     * Cause this builder to render a report to excel with the given datasource and to the output stream.
     *
     * @param reportView contains optional view configurations and the template
     * @param source     contains all the data for a report instance
     * @param out        where the report should be delivered to
     */
    void renderToExcel(ReportView reportView, List<ReportDatasource> source, OutputStream out);

    /**
     * Cause this builder to render a report to csv with the given datasource and to the output stream.
     *
     * @param reportView contains optional view configurations and the template
     * @param source     contains all the data for a report instance
     * @param out        where the report should be delivered to
     */
    void renderToCsv(ReportView reportView, List<ReportDatasource> source, OutputStream out);

    /**
     * Cause this builder to render a report to word with the given datasource and to the output stream.
     *
     * @param reportView contains optional view configurations and the template
     * @param source     contains all the data for a report instance
     * @param out        where the report should be delivered to
     */
    void renderToWord(ReportView reportView, List<ReportDatasource> source, OutputStream out);
}
