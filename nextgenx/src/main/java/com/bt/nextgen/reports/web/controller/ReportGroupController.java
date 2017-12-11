package com.bt.nextgen.reports.web.controller;

import com.bt.nextgen.core.reporting.ReportBuilder;
import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.datasource.ReportDatasourceFactory;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewFactory;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This is the controller used to generate groups of reports
 * 
 * 
 * @see com.bt.nextgen.core.reporting.ReportBuilder
 * @see com.btfin.panorama.core.concurrent.Concurrent
 */
@Controller
@SuppressWarnings("squid:S00112")
public class ReportGroupController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private static final String REPORT_FILE_NAME_PARAM = "reportFileName";
    private static final String THREAD_POOL_SIZE_PARAM = "threadPoolSize";

    @Autowired
    @Qualifier("jasperReportBuilder")
    private ReportBuilder reportBuilder;

    @Autowired
    private ReportDatasourceFactory datasourceFactory;

    @Autowired
    private ReportViewFactory reportViewFactory;

    @RequestMapping(value = "/secure/reportgroupcsv/{reportId}", method = RequestMethod.GET)
    public void generateReportGroupCsv(@PathVariable("reportId") String reportId,
            @RequestParam final Map<String, Object> allRequestParams, HttpServletResponse response) throws Exception {
        logger.info("Requesting csv report group generation for {} ", reportId);

        ReportIdentity reportKey = ReportIdentity.ReportIdentityString.asIdentity(reportId);

        allRequestParams.put(JRParameter.IS_IGNORE_PAGINATION, true);
        List<ReportDatasource> datasources = datasourceFactory.createDataSources(reportKey, allRequestParams);
        
        String fileName = datasources.get(0).getParameters().get(REPORT_FILE_NAME_PARAM).toString();
        int threadPoolSize = (int)datasources.get(0).getParameters().get(THREAD_POOL_SIZE_PARAM);

        // Render CSV files concurrently
        List<ConcurrentCallable<?>> reportsToRender = new ArrayList<ConcurrentCallable<?>>();
        for (ReportDatasource datasource : datasources) {
            reportsToRender.add(getCsvReportToRender(reportKey, datasource));
        }

        Concurrent.when(threadPoolSize, reportsToRender.toArray(new ConcurrentCallable<?>[datasources.size()]))
                .done(createZipArchive(reportKey, fileName, response)).execute();
    }

    private ConcurrentCallable<Pair<String, byte[]>> getCsvReportToRender(final ReportIdentity reportKey,
            final ReportDatasource datasource) {

        return new ConcurrentCallable<Pair<String, byte[]>>() {

            @Override
            public Pair<String, byte[]> call() {

                ReportView reportView = reportViewFactory.createReportView(reportKey);
                reportView.setReportDataSources(Collections.singletonList(datasource));

                if (!reportView.getReportTemplate().isAvailable()) {
                    logger.error("Unable to source report {} from {}", reportKey.getTemplateKey(), reportView.getReportSource());
                    throw new RuntimeException("Unable to find report for reportId: " + reportKey.getTemplateKey());
                }

                String filename = datasource.getName() + ".csv";
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                reportBuilder.renderToCsv(reportView, Collections.singletonList(datasource), byteStream);
                return new ImmutablePair<>(filename, byteStream.toByteArray());
            }
        };
    }

    private ConcurrentComplete createZipArchive(final ReportIdentity reportKey, final String fileName,
            final HttpServletResponse response) {

        return new AbstractConcurrentComplete() {

            @Override
            public void run() {

                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".zip");
                response.setContentType("application/zip");

                writeResultsToArchive(reportKey, response, this.getResults());
            }
        };
    }

    private static void writeResultsToArchive(ReportIdentity reportKey, HttpServletResponse response,
            List<? extends ConcurrentResult<?>> results) {

        try {
            ZipOutputStream zipStream = new ZipOutputStream(response.getOutputStream());

            for (ConcurrentResult<?> result : results) {
                Pair<String, byte[]> reportDetail = (Pair<String, byte[]>) result.getResult();

                ZipEntry entry = new ZipEntry(reportDetail.getKey());
                zipStream.putNextEntry(entry);
                zipStream.write(reportDetail.getValue());
                zipStream.closeEntry();
            }

            zipStream.flush();
            zipStream.close();

        } catch (IOException io) {
            logger.error("failed writing to zip archive", io);
            throw new RuntimeException("Problem when generating group report: " + reportKey.getTemplateKey());
        }
    }
}
