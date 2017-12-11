package com.bt.nextgen.core.reporting;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.view.DefaultReportViewImpl;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewProcessor;
import com.bt.nextgen.core.util.Properties;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static ch.lambdaj.Lambda.sumFrom;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * This class is in charge of running the jasper engine
 */
@SuppressWarnings("squid:S00112")
@Service("jasperReportBuilder")
public class JasperReportBuilderImpl implements ReportBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JasperReportBuilderImpl.class);

    @Autowired
    private ReportViewProcessor reportViewProcessor;

    @Autowired
    private ReportHelper reportHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderToPdf(ReportView reportView, OutputStream out) {
        JasperReport jReport = compileReport(reportView);

        try {
            JasperPrint printableReport = fillReport(jReport, reportView.getReportDataSource());
            JasperExportManager.exportReportToPdfStream(printableReport, out);
        } catch (JRException e) {
            logger.error("Failure during rendering report " + reportView.getReportTemplate().getId(), e);
            throw new RuntimeException("Failure during rendering report " + reportView.getReportTemplate().getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderToPdf(List<ReportView> reportViews, boolean compress, OutputStream out) {
        if (compress) {
            ZipOutputStream zipOutputStream = (ZipOutputStream) out;

            Map<String, ReportProperties> reportPropertiesMap = reportHelper
                    .getReportPropertiesMap(extract(reportViews, on(ReportView.class).getReportTemplate().getId().getTemplateKey()));

            try {
                for (ReportView reportView : reportViews) {
                    ReportProperties reportProperties = reportPropertiesMap.get(reportView.getReportTemplate().getId().getTemplateKey());

                    zipOutputStream.putNextEntry(new ZipEntry(reportProperties.getFilename() + ".pdf"));
                    renderToPdf(reportView, zipOutputStream);
                }
            } catch (IOException e) {
                logger.error("Failure during rendering reports", e);
                throw new RuntimeException("Failure during rendering reports", e);
            }
        } else {
            List<JasperPrint> jasperPrints = new ArrayList<>();

            try {
                for (ReportView reportView : reportViews) {
                    JasperReport jReport = compileReport(reportView);
                    jasperPrints.add(fillReport(jReport, reportView.getReportDataSource()));
                }

                JRPdfExporter jrPdfExporter = new JRPdfExporter();
                jrPdfExporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
                jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                configuration.setCreatingBatchModeBookmarks(true);
                jrPdfExporter.setConfiguration(configuration);

                jrPdfExporter.exportReport();
            } catch (JRException e) {
                logger.error("Failure during rendering report", e);
                throw new RuntimeException("Failure during rendering report", e);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void renderToExcel(ReportView reportView, List<ReportDatasource> reportContents, OutputStream out) {
        JasperReport jReport = compileReport(reportView);

        try {
            List<JasperPrint> jPrintList = new ArrayList<JasperPrint>();

            for (ReportDatasource reportData : reportContents) {
                JasperPrint printableReport = fillReport(jReport, reportData);
                jPrintList.add(printableReport);
            }

            JRXlsExporter exporter = new JRXlsExporter();

            exporter.setExporterInput(SimpleExporterInput.getInstance(jPrintList));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
            configuration.setMaxRowsPerSheet(0);
            configuration.setOnePagePerSheet(false);
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(true);
            configuration.setIgnoreCellBorder(true);
            configuration.setWhitePageBackground(false);
            configuration.setRemoveEmptySpaceBetweenColumns(true);
            configuration.setRemoveEmptySpaceBetweenRows(false); // ?
            configuration.setImageBorderFixEnabled(true);
            configuration.setFontSizeFixEnabled(true);
            configuration.setIgnoreGraphics(false);
            exporter.setConfiguration(configuration);

            exporter.exportReport();
        } catch (JRException e) {
            throw new RuntimeException("Failure during rendering report " + reportView.getReportTemplate().getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderToCsv(ReportView reportView, List<ReportDatasource> reportContents, OutputStream out) {
        JasperReport jReport = compileReport(reportView);

        try {
            List<JasperPrint> jPrintList = new ArrayList<JasperPrint>();

            for (ReportDatasource reportData : reportContents) {
                JasperPrint printableReport = fillReport(jReport, reportData);
                jPrintList.add(printableReport);
            }

            JRCsvExporter exporter = new JRCsvExporter();

            exporter.setExporterInput(SimpleExporterInput.getInstance(jPrintList));

            exporter.setExporterOutput(new SimpleWriterExporterOutput(out));

            // SimpleCsvReportConfiguration configuration = new SimpleCsvReportConfiguration();

            // exporter.setConfiguration(configuration);

            exporter.exportReport();
        } catch (JRException e) {
            throw new RuntimeException("Failure during rendering report " + reportView.getReportTemplate().getId(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderToWord(ReportView reportView, List<ReportDatasource> reportContents, OutputStream out) {

        JasperReport jReport = compileReport(reportView);

        try {
            List<JasperPrint> jPrintList = new ArrayList<JasperPrint>();

            for (ReportDatasource reportData : reportContents) {
                JasperPrint printableReport = fillReport(jReport, reportData);
                jPrintList.add(printableReport);
            }

            JRDocxExporter docxExporter = new JRDocxExporter();

            docxExporter.setExporterInput(SimpleExporterInput.getInstance(jPrintList));

            docxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));

            docxExporter.exportReport();
        } catch (JRException e) {
            throw new RuntimeException("Failure during rendering report " + reportView.getReportTemplate().getId(), e);
        }

    }

    /**
     * Given a report, this function will compile the report using the jasper engine. The stored compilation is in the Report
     * object
     *
     * @param reportView the view that contains optional view configurations and the report template
     * @return the preparedReport
     * @see com.bt.nextgen.core.reporting.ReportTemplate#getCompiledVersion()
     */
    protected JasperReport compileReport(ReportView reportView) {
        // When a specific view is defined it implies that there is a need to modify the source jrxml file.
        // If a non-specific view is defined we skip view processing.
        if (!(reportView instanceof DefaultReportViewImpl)) {
            JasperDesign jasperDesign = (JasperDesign) reportViewProcessor.process(reportView);

            if (jasperDesign != null) {
                try {
                    // Must not cache compiled report
                    return JasperCompileManager.compileReport(jasperDesign);
                } catch (JRException ex) {
                    throw new RuntimeException("Problem running reporting engine", ex);
                }
            }
        }

        // Use compiled version if available
        if (reportView.getReportTemplate().getCompiledVersion() != null && Properties.getSafeBoolean("report.use.compiled")) {
            return (JasperReport) reportView.getReportTemplate().getCompiledVersion();
        }

        try {
            JasperReport jasperReport;

            switch (reportView.getReportTemplate().getType()) {
                case "application/xml":
                case "application/vnd.jasper.jrxml":
                case "application/vnd.jrxml":
                    logger.info("Found a jrxml file, will compile {}", reportView.getReportTemplate().getId());
                    jasperReport = JasperCompileManager.compileReport(reportView.getReportTemplate().getAsStream());
                    break;

                case "application/vnd.jasper":
                    logger.info("Found a precompiled report for {}", reportView.getReportTemplate().getId());
                    jasperReport = (JasperReport) JRLoader.loadObject(reportView.getReportTemplate().getAsStream());
                    break;

                default:
                    throw new RuntimeException("Unknown file type " + reportView.getReportTemplate().getType());
            }

            reportView.getReportTemplate().setCompiledVersion(jasperReport);

            return jasperReport;
        } catch (JRException | IOException ex) {
            logger.error("Failure during rendering report", ex);
            throw new RuntimeException("Problem running reporting engine", ex);
        }
    }

    protected JasperPrint fillReport(JasperReport report, ReportDatasource source) throws JRException {
        JasperPrint printableReport;
        JRDataSource dataSource;

        Collection<?> beans = source.getBeans();

        if (beans.isEmpty()) {
            dataSource = new JREmptyDataSource();
        } else {
            dataSource = new JRBeanCollectionDataSource(beans);
        }

        printableReport = JasperFillManager.fillReport(report, source.getParameters(), dataSource);
        printableReport.setName(source.getName());
        return printableReport;
    }
}
