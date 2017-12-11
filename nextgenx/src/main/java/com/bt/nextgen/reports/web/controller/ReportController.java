package com.bt.nextgen.reports.web.controller;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.ApiParam;
import net.sf.jasperreports.engine.JRParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.reporting.ReportBuilder;
import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.ReportPackService;
import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.datasource.ReportDatasourceFactory;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewFactory;
import com.bt.nextgen.reports.web.model.ReportRequestDto;
import com.bt.nextgen.reports.web.model.ReportRequestPackDto;

/**
 * This is the controller used to generate individual reports in various formats
 *
 * @see com.bt.nextgen.core.reporting.ReportBuilder
 */
@Controller
@SuppressWarnings("squid:S00112")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private static final String REPORT_FILE_NAME_PARAM = "reportFileName";

    @Autowired
    @Qualifier("jasperReportBuilder")
    private ReportBuilder reportBuilder;

    @Autowired
    private ReportDatasourceFactory datasourceFactory;

    @Autowired
    private ReportViewFactory reportViewFactory;

    @Autowired
    @Qualifier("accountPdfReportPackService")
    private ReportPackService reportPackService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private JsonObjectMapper objectMapper;

    @RequestMapping(value = "/secure/reportpdf/{reportId}", method = {RequestMethod.GET, RequestMethod.POST})
    public void generateReportPdf(@PathVariable("reportId") String reportId,
                                  @RequestParam final Map<String, Object> allRequestParams, HttpServletResponse response) throws Exception {
        logger.info("Requesting pdf report generation for {} ", reportId);

        ReportIdentity reportKey = ReportIdentity.ReportIdentityString.asIdentity(reportId);

        List<ReportDatasource> datasource = datasourceFactory.createDataSources(reportKey, allRequestParams);
        String fileName = datasource.get(0).getParameters().get(REPORT_FILE_NAME_PARAM).toString();

        ReportView reportView = reportViewFactory.createReportView(reportKey);

        if (!reportView.getReportTemplate().isAvailable()) {
            logger.error("Unable to source report {} from {}", reportId, reportView.getReportSource());
            throw new RuntimeException("Unable to find report for reportId: " + reportId);
        }

        reportView.setReportDataSource(datasource.get(0));

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");
        response.setContentType("application/pdf");

        reportBuilder.renderToPdf(reportView, response.getOutputStream());
    }

    @RequestMapping(value = "/secure/reportxls/{reportId}", method = RequestMethod.GET)
    public void generateReportExcel(@PathVariable("reportId") String reportId,
                                    @RequestParam final Map<String, Object> allRequestParams,
                                    HttpServletResponse response) throws Exception {
        logger.info("Requesting excel report generation for {} ", reportId);
        ReportIdentity reportKey = ReportIdentity.ReportIdentityString.asIdentity(reportId);

        allRequestParams.put(JRParameter.IS_IGNORE_PAGINATION, true);
        List<ReportDatasource> datasource = datasourceFactory.createDataSources(reportKey, allRequestParams);
        String fileName = datasource.get(0).getParameters().get(REPORT_FILE_NAME_PARAM).toString();

        ReportView reportView = reportViewFactory.createReportView(reportKey);
        reportView.setReportDataSources(datasource);

        if (!reportView.getReportTemplate().isAvailable()) {
            logger.error("Unable to source report {} from {}", reportId, reportView.getReportSource());
            throw new RuntimeException("Unable to find report for reportId: " + reportId);
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xls\"");
        response.setContentType("application/vnd.ms-excel");

        reportBuilder.renderToExcel(reportView, datasource, response.getOutputStream());
    }

    // Need to revisit the URL as per the review comment
    @RequestMapping(value = "/secure/reportcsv/{reportId}", method = RequestMethod.GET)
    public void generateReportCsv(@PathVariable("reportId") String reportId,
                                  @RequestParam final Map<String, Object> allRequestParams, HttpServletResponse response) throws Exception {
        logger.info("Requesting excel report generation for {} ", reportId);

        ReportIdentity reportKey = ReportIdentity.ReportIdentityString.asIdentity(reportId);

        allRequestParams.put(JRParameter.IS_IGNORE_PAGINATION, true);
        List<ReportDatasource> datasource = datasourceFactory.createDataSources(reportKey, allRequestParams);
        String fileName = datasource.get(0).getParameters().get(REPORT_FILE_NAME_PARAM).toString();

        ReportView reportView = reportViewFactory.createReportView(reportKey);
        reportView.setReportDataSources(datasource);

        if (!reportView.getReportTemplate().isAvailable()) {
            logger.error("Unable to source report {} from {}", reportId, reportView.getReportSource());
            throw new RuntimeException("Unable to find report for reportId: " + reportId);
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");
        response.setContentType("text/csv");

        reportBuilder.renderToCsv(reportView, datasource, response.getOutputStream());
    }

    @RequestMapping(value = "/secure/reportword/{reportId}", method = RequestMethod.GET)
    public void generateReportWord(@PathVariable("reportId") String reportId,
                                   @RequestParam final Map<String, Object> allRequestParams,
                                   HttpServletResponse response) throws Exception {
        logger.info("Requesting word report generation for {} ", reportId);

        ReportIdentity reportKey = ReportIdentity.ReportIdentityString.asIdentity(reportId);

        List<ReportDatasource> datasource = datasourceFactory.createDataSources(reportKey, allRequestParams);
        String fileName = datasource.get(0).getParameters().get(REPORT_FILE_NAME_PARAM).toString();

        ReportView reportView = reportViewFactory.createReportView(reportKey);
        reportView.setReportDataSource(datasource.get(0));

        if (!reportView.getReportTemplate().isAvailable()) {
            logger.error("Unable to source report {} from {}", reportId, reportView.getReportSource());
            throw new RuntimeException("Unable to find report for reportId: " + reportId);
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".doc\"");
        response.setContentType("application/msword");

        reportBuilder.renderToWord(reportView, datasource, response.getOutputStream());
    }

    @RequestMapping(value = "/secure/reportpdfpack/{accountId}", method = {RequestMethod.GET, RequestMethod.POST})
    public void generateReportPdfPack(@PathVariable("accountId") String accountId,
                                      @RequestParam(value = "req")
                                      @ApiParam(value = "The report pack request in JSON format", required = true) String request,
                                      HttpServletResponse response) throws Exception {
        logger.info("Requesting pdf report pack generation");

        MultipartFile coverLetter = null;

        if (!validCoverLetter(coverLetter)) {
            throw new RuntimeException("Invalid cover letter format: " + coverLetter.getContentType());
        }

        String sanitizedRequest = JsonSanitizer.sanitize(request);
        ReportRequestPackDto reportRequestPackDto =
                objectMapper.readerWithView(JsonViews.Write.class).forType(ReportRequestPackDto.class).readValue(sanitizedRequest);

        // Add common account-id and serviceType param so the front-end won't have to specify each one to reduce query string size
        for (ReportRequestDto reportRequestDto : reportRequestPackDto.getReportRequestDtos()) {
            reportRequestDto.getParams().put("account-id", accountId);
            reportRequestDto.getParams().put("serviceType", "cache");
        }

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        String fileName = reportPackService.getReportFileName(accountKey);
        if (reportRequestPackDto.getCompressReports()) {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".zip\"");
            response.setContentType("application/zip");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".pdf\"");
            response.setContentType("application/pdf");
        }

        OutputStream effectiveOutputStream =
                reportRequestPackDto.getCompressReports() ? new ZipOutputStream(response.getOutputStream()) : response.getOutputStream();

        reportPackService.create(reportRequestPackDto, null, effectiveOutputStream);

        if (reportRequestPackDto.getCompressReports()) {
            effectiveOutputStream.flush();
            effectiveOutputStream.close();
        }
    }

    // To be finalised
    private boolean validCoverLetter(MultipartFile coverLetter) {
        if (coverLetter != null) {
            return coverLetter.getContentType().toLowerCase().contains("pdf") &&
                    coverLetter.getOriginalFilename().toLowerCase().lastIndexOf(".pdf") == coverLetter.getOriginalFilename().length() - 5;
        }

        return true;
    }
}
