package com.bt.nextgen.core.reporting;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.datasource.ReportDatasourceFactory;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewFactory;
import com.bt.nextgen.reports.web.model.ReportRequestDto;
import com.bt.nextgen.reports.web.model.ReportRequestPackDto;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.itextpdf.text.ExceptionConverter;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class PdfReportPackServiceImplTest {

    @InjectMocks
    private PdfReportPackServiceDtoImpl pdfReportPackServiceDto;

    @Mock
    private ReportBuilder concurrentReportBuilder;

    @Mock
    private ReportDatasourceFactory reportDatasourceFactory;

    @Mock
    private ReportViewFactory reportViewFactory;

    @Before
    public void init() {
        ReportDatasource reportDatasource = mock(ReportDatasource.class);
        when(reportDatasourceFactory.createDataSources(any(ReportIdentity.class), anyMap())).thenReturn(Arrays.asList(reportDatasource));
    }

    @Test(expected = RuntimeException.class)
    public void testCreate_whenReportTemplateNotAvailable_thenThrowRuntimeException() {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.FALSE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.FALSE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);

        OutputStream outputStream = mock(OutputStream.class);

        pdfReportPackServiceDto.create(reportRequestPackDto, null, outputStream);
    }

    @Test
    public void testCreate_whenNoCoverLetterAndNoCompression_thenCallConcurrentReportBuilderDirectly() {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.FALSE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);
        ReportIdentity reportIdentity = mock(ReportIdentity.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.TRUE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportTemplate.getId()).thenReturn(reportIdentity);
        when(reportIdentity.getTemplateKey()).thenReturn("reportId");

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);

        OutputStream outputStream = mock(OutputStream.class);

        pdfReportPackServiceDto.create(reportRequestPackDto, null, outputStream);

        verify(concurrentReportBuilder, times(1)).renderToPdf(Arrays.asList(reportView), false, outputStream);
    }

    @Test
    public void testCreate_whenHasCoverLetterAndCompressionDisabled_thenIncludeCoverLetter() {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.FALSE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);
        ReportIdentity reportIdentity = mock(ReportIdentity.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.TRUE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportTemplate.getId()).thenReturn(reportIdentity);
        when(reportIdentity.getTemplateKey()).thenReturn("reportId");

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        InputStream coverLetter = getClass().getResourceAsStream("/reports/blank.pdf");

        pdfReportPackServiceDto.create(reportRequestPackDto, coverLetter, outputStream);

        assertTrue(outputStream.size() > 0);

        byte[] data = outputStream.toByteArray();
        byte[] header = Arrays.copyOfRange(data, 0, 4);

        assertEquals("%PDF", new String(header));
    }

    @Test(expected = ExceptionConverter.class)
    public void testCreate_whenHasCoverLetterButInvalidPdf_thenThrowException() {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.FALSE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);
        ReportIdentity reportIdentity = mock(ReportIdentity.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.TRUE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportTemplate.getId()).thenReturn(reportIdentity);
        when(reportIdentity.getTemplateKey()).thenReturn("reportId");

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);

        OutputStream outputStream = mock(OutputStream.class);

        InputStream coverLetter = getClass().getResourceAsStream("/reports/helloworld.jrxml");

        pdfReportPackServiceDto.create(reportRequestPackDto, coverLetter, outputStream);
    }

    @Test
    public void testCreate_whenHasCoverLetterAndCompressionEnabled_thenGenerateZipFileWithCoverLetter() {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.TRUE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.TRUE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        ReportIdentity reportIdentity = mock(ReportIdentity.class);

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);
        when(reportTemplate.getId()).thenReturn(reportIdentity);
        when(reportIdentity.getTemplateKey()).thenReturn("reportId");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        InputStream coverLetter = getClass().getResourceAsStream("/reports/blank.pdf");

        pdfReportPackServiceDto.create(reportRequestPackDto, coverLetter, zipOutputStream);

        assertTrue(outputStream.size() > 0);

        byte[] data = outputStream.toByteArray();
        byte[] header = Arrays.copyOfRange(data, 0, 2);

        assertEquals("PK", new String(header));
    }

    @Test
    public void testCreate_whenHasCoverLetterAndCompressionEnabled_fail() throws IOException {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.TRUE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getReportId()).thenReturn("reportId");

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        when(reportTemplate.isAvailable()).thenReturn(Boolean.TRUE);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        ReportIdentity reportIdentity = mock(ReportIdentity.class);

        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);
        when(reportTemplate.getId()).thenReturn(reportIdentity);
        when(reportIdentity.getTemplateKey()).thenReturn("reportId");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        InputStream coverLetter = getClass().getResourceAsStream("/reports/blank.pdf");

        PowerMockito.mockStatic(IOUtils.class);
        when(IOUtils.copy(Mockito.any(InputStream.class), Mockito.any(OutputStream.class)))
                .thenThrow(new IOException());

        try {
            pdfReportPackServiceDto.create(reportRequestPackDto, coverLetter, zipOutputStream);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Unable to add report to archive");
        }
    }

    @Test
    public void testGetReportFileName_whenAccountKeyProvided_thenReturnFileName() {
        AccountKey accountKey = AccountKey.valueOf("123456");
        String fileName = pdfReportPackServiceDto.getReportFileName(accountKey);
        assertThat(fileName, is("report-pack"));
    }
}
