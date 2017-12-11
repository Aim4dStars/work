package com.bt.nextgen.core.reporting;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ch.lambdaj.function.convert.Converter;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.reporting.view.ReportView;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

@Service("concurrentJasperReportBuilder")
public class ConcurrentJasperReportBuilderImpl extends JasperReportBuilderImpl {
    // Move to properties?
    private static final int THREAD_POOL_SIZE = 5;
    private static Logger logger = LoggerFactory.getLogger(ConcurrentJasperReportBuilderImpl.class);
    
    @Autowired
    private ReportHelper reportHelper;

    /**
     * Render report views to a single PDF or compress individual PDF's concurrently.
     *
     * @param reportViews  list of complete report views
     * @param compress     flag to generate a zip file with individual PDF's or combine into a single PDF
     * @param outputStream output stream
     */
    @Override
    public void renderToPdf(List<ReportView> reportViews, boolean compress, OutputStream outputStream) {
        List<ConcurrentCallable<JasperPrintResult>> reportCallableList = convert(reportViews,
                new Converter<ReportView, ConcurrentCallable<JasperPrintResult>>() {
                    @Override
                    public ConcurrentCallable<JasperPrintResult> convert(ReportView reportView) {
                        return createReportCallable(reportView);
                    }
                });

        if (compress) {
            Concurrent.when(THREAD_POOL_SIZE, reportCallableList.toArray(new ConcurrentCallable<?>[reportViews.size()]))
                      .done(compressJasperPrints(reportViews, outputStream))
                      .execute();
        } else {
            Concurrent.when(THREAD_POOL_SIZE, reportCallableList.toArray(new ConcurrentCallable<?>[reportViews.size()]))
                      .done(combineJasperPrints(reportViews, outputStream))
                      .execute();
        }
    }

    private ConcurrentComplete combineJasperPrints(final List<ReportView> reportViews, final OutputStream outputStream) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<JasperPrint> jasperPrints = new ArrayList<>();

                // Reassemble into the right order
                for (ReportView reportView : reportViews) {
                    for (ConcurrentResult<?> concurrentResult : this.getResults()) {
                        JasperPrintResult jasperPrintResult = (JasperPrintResult) concurrentResult.getResult();

                        if (reportView.getReportTemplate().getId().equals(jasperPrintResult.getReportIdentity())) {
                            jasperPrints.add(jasperPrintResult.getJasperPrint());
                            break;
                        }
                    }
                }

                combineReports(jasperPrints, outputStream);
            }
        };
    }

    private ConcurrentComplete compressJasperPrints(final List<ReportView> reportViews, final OutputStream outputStream) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<JasperPrintResult> jasperPrintResults = new ArrayList<>();

                // Reassemble into the right order
                for (ReportView reportView : reportViews) {
                    for (ConcurrentResult<?> concurrentResult : this.getResults()) {
                        JasperPrintResult jasperPrintResult = (JasperPrintResult) concurrentResult.getResult();

                        if (reportView.getReportTemplate().getId().equals(jasperPrintResult.getReportIdentity())) {
                            jasperPrintResults.add(jasperPrintResult);
                            break;
                        }
                    }
                }

                compressReports(jasperPrintResults, outputStream);
            }
        };
    }

    private void combineReports(List<JasperPrint> jasperPrints, OutputStream outputStream) {
        JRPdfExporter jrPdfExporter = new JRPdfExporter();
        jrPdfExporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
        jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setCreatingBatchModeBookmarks(true);
        jrPdfExporter.setConfiguration(configuration);

        try {
            jrPdfExporter.exportReport();
        } catch (JRException e) {
            logger.error("Failure during rendering report", e);
            throw new ReportBuilderException("Failure during rendering report", e);
        }
    }

    private void compressReports(List<JasperPrintResult> jasperPrintResults, OutputStream outputStream) {
        ZipOutputStream zipOutputStream = (ZipOutputStream) outputStream;

        Map<String, ReportProperties> reportPropertiesMap = reportHelper
                .getReportPropertiesMap(extract(jasperPrintResults, on(JasperPrintResult.class).getReportIdentity().getTemplateKey()));

        try {
            for (JasperPrintResult jasperPrintResult : jasperPrintResults) {
                ReportProperties reportProperties = reportPropertiesMap.get(jasperPrintResult.getReportIdentity().getTemplateKey());

                zipOutputStream.putNextEntry(new ZipEntry(reportProperties.getFilename() + ".pdf"));
                JasperExportManager.exportReportToPdfStream(jasperPrintResult.getJasperPrint(), zipOutputStream);
            }
        } catch (IOException | JRException e) {
            logger.error("Failure during rendering reports", e);
            throw new ReportBuilderException("Failure during rendering reports", e);
        } finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                logger.error("Failure during compressing reports", e);
            }
        }
    }

    private ConcurrentCallable<JasperPrintResult> createReportCallable(final ReportView reportView) {
        return new ConcurrentCallable<JasperPrintResult>() {
            @Override
            public JasperPrintResult call() {
                JasperReport jReport = compileReport(reportView);

                try {
                    return new JasperPrintResult(reportView.getReportTemplate().getId(),
                            fillReport(jReport, reportView.getReportDataSource()));
                } catch (JRException e) {
                    logger.error("Failure during rendering report " + reportView.getReportTemplate().getId().getTemplateKey(), e);
                    throw new ReportBuilderException(
                            "Failure during rendering report " + reportView.getReportTemplate().getId().getTemplateKey(), e);
                }
            }
        };
    }
}
