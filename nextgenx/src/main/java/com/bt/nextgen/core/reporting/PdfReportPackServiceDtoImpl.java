package com.bt.nextgen.core.reporting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.datasource.ReportDatasourceFactory;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewFactory;
import com.bt.nextgen.reports.web.model.ReportRequestDto;
import com.bt.nextgen.reports.web.model.ReportRequestPackDto;

import static ch.lambdaj.Lambda.convert;

/**
 * Pdf Report Pack Service.
 */
@Service("pdfReportPackService")
public class PdfReportPackServiceDtoImpl implements ReportPackService {
    private static final Logger logger = LoggerFactory.getLogger(PdfReportPackServiceDtoImpl.class);

    private static final int THREAD_POOL_SIZE = 5;

    private static final String DEFAULT_REPORT_PACK_FILE_NAME = "report-pack";

    @Autowired
    @Qualifier("concurrentJasperReportBuilder")
    private ReportBuilder concurrentReportBuilder;

    @Autowired
    private ReportDatasourceFactory reportDatasourceFactory;

    @Autowired
    private ReportViewFactory reportViewFactory;

    public void create(ReportRequestPackDto reportRequestPackDto, InputStream coverLetter, OutputStream outputStream) {
        List<ConcurrentCallable<ReportView>> reportViewCallableList = convert(reportRequestPackDto.getReportRequestDtos(),
                new Converter<ReportRequestDto, ConcurrentCallable<ReportView>>() {
                    @Override
                    public ConcurrentCallable<ReportView> convert(ReportRequestDto reportRequestDto) {
                        return createReportViewCallable(reportRequestDto);
                    }
                });

        Concurrent.when(THREAD_POOL_SIZE,
                reportViewCallableList.toArray(new ConcurrentCallable<?>[reportRequestPackDto.getReportRequestDtos().size()]))
                  .done(processReportViews(reportRequestPackDto, coverLetter, outputStream))
                  .execute();
    }

    @Override
    public String getReportFileName(AccountKey accountKey) {
        return DEFAULT_REPORT_PACK_FILE_NAME;
    }

    private ConcurrentComplete processReportViews(final ReportRequestPackDto reportRequestPackDto, final InputStream coverLetter,
                                                  final OutputStream outputStream) {
        return new AbstractConcurrentComplete() {
            @Override
            public void run() {
                List<ReportView> reportViews = new ArrayList<>();

                // Reassemble into the right order
                for (ReportRequestDto reportRequestDto : reportRequestPackDto.getReportRequestDtos()) {
                    for (ConcurrentResult<?> concurrentResult : this.getResults()) {
                        ReportView reportView = (ReportView) concurrentResult.getResult();

                        if (reportView.getReportTemplate().getId().getTemplateKey().equals(reportRequestDto.getReportId())) {
                            reportViews.add(reportView);
                            break;
                        }
                    }
                }

                createReports(reportViews, coverLetter, reportRequestPackDto.getCompressReports(), outputStream);
            }
        };

    }

    private void createReports(final List<ReportView> reportViews, final InputStream coverLetter, boolean compressReports,
                               final OutputStream outputStream) {
        if (coverLetter != null) {
            createWithCoverLetter(reportViews, coverLetter, compressReports, outputStream);
        } else {
            concurrentReportBuilder.renderToPdf(reportViews, compressReports, outputStream);
        }
    }

    private void createWithCoverLetter(List<ReportView> reportViews, InputStream coverLetter, boolean compressReports,
                                       OutputStream outputStream) {
        if (compressReports) {
            ZipOutputStream zipOutputStream = (ZipOutputStream) outputStream;

            try {
                // Add cover letter to archive - do we need to use the filename uploaded?
                zipOutputStream.putNextEntry(new ZipEntry("Cover letter.pdf"));
                IOUtils.copy(coverLetter, zipOutputStream);
            } catch (IOException e) {
                logger.error("Unable to add report to archive", e);
                throw new ReportBuilderException("Unable to add report to archive", e);
            }

            concurrentReportBuilder.renderToPdf(reportViews, compressReports, zipOutputStream);
        } else {
            // Unfortunately we have to convert the generated report into byte array for PdfCopy to work
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            concurrentReportBuilder.renderToPdf(reportViews, compressReports, byteArrayOutputStream);

            Document document = new Document(PageSize.A4);
            document.setMargins(0F, 0F, 0F, 0F);

            try {
                PdfCopy pdfCopy = new PdfCopy(document, outputStream);
                pdfCopy.setMargins(0, 0, 0, 0);
                document.open();

                PdfReader coverLetterReader = new PdfReader(coverLetter);
                pdfCopy.addDocument(coverLetterReader);
                pdfCopy.freeReader(coverLetterReader);
                coverLetterReader.close();

                if (byteArrayOutputStream.size() > 0) {
                    PdfReader reports = new PdfReader(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                    pdfCopy.addDocument(reports);
                    pdfCopy.freeReader(reports);
                    reports.close();
                }
            } catch (DocumentException | IOException e) {
                logger.error("Unable to combine reports", e);
                throw new ReportBuilderException("Unable to combine reports", e);
            } finally {
                document.close();
            }
        }
    }

    private ConcurrentCallable<ReportView> createReportViewCallable(final ReportRequestDto reportRequestDto) {
        return new ConcurrentCallable<ReportView>() {
            @Override
            public ReportView call() {
                ReportIdentity reportKey = ReportIdentity.ReportIdentityString.asIdentity(reportRequestDto.getReportId());

                List<ReportDatasource> datasource = reportDatasourceFactory.createDataSources(reportKey, reportRequestDto.getParams());

                ReportView reportView = reportViewFactory.createReportView(reportKey);

                if (!reportView.getReportTemplate().isAvailable()) {
                    logger.error("Unable to source report {} from {}", reportRequestDto.getReportId(), reportView.getReportSource());
                    throw new ReportBuilderException("Unable to find report for reportId: " + reportRequestDto.getReportId());
                }

                reportView.setReportDataSource(datasource.get(0));

                return reportView;
            }
        };
    }
}