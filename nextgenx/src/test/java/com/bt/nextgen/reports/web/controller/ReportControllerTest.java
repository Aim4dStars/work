package com.bt.nextgen.reports.web.controller;

import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.bt.nextgen.core.reporting.ReportIdentity;
import com.bt.nextgen.core.reporting.ReportTemplate;
import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.reporting.ReportBuilder;
import com.bt.nextgen.core.reporting.ReportPackService;
import com.bt.nextgen.core.reporting.datasource.ReportDatasourceFactory;
import com.bt.nextgen.core.reporting.view.ReportViewFactory;
import com.bt.nextgen.reports.web.model.ReportRequestDto;
import com.bt.nextgen.reports.web.model.ReportRequestPackDto;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {
    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportBuilder reportBuilder;

    @Mock
    private ReportDatasourceFactory datasourceFactory;

    @Mock
    private ReportViewFactory reportViewFactory;

    @Mock
    private ReportPackService reportPackService;

    @Mock
    private JsonObjectMapper objectMapper;

    @Mock
    private ObjectReader objectReader;

    @Before
    public void setup() {
        when(objectMapper.readerWithView(any(Class.class))).thenReturn(objectReader);
        when(objectReader.forType(any(Class.class))).thenReturn(objectReader);
        when(reportPackService.getReportFileName(any(AccountKey.class))).thenReturn("file-name");
    }

    @Test(expected = RuntimeException.class)
    public void testGenerateReportPdf_whenReportViewNotAvailable_thenThrowException() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(false);
        reportController.generateReportPdf("reportId", allRequestParams, response);
    }

    @Test
    public void testGenerateReportPdf_whenReportViewAvailable_thenGeneratePdfReport() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(true);

        reportController.generateReportPdf("reportId", allRequestParams, response);

        verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=\"file-name.pdf\"");
        verify(response, times(1)).setContentType("application/pdf");
    }

    @Test(expected = RuntimeException.class)
    public void testGenerateReportExcel_whenReportViewNotAvailable_thenThrowException() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(false);

        reportController.generateReportExcel("reportId", allRequestParams, response);
    }

    @Test
    public void testGenerateReportExcel_whenReportViewAvailable_thenGenerateExcelReport() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(true);

        reportController.generateReportExcel("reportId", allRequestParams, response);

        verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=\"file-name.xls\"");
        verify(response, times(1)).setContentType("application/vnd.ms-excel");
    }

    @Test(expected = RuntimeException.class)
    public void testGenerateReportCsv_whenReportViewNotAvailable_thenThrowException() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(false);

        reportController.generateReportCsv("reportId", allRequestParams, response);
    }

    @Test
    public void testGenerateReportCsv_whenReportViewAvailable_thenGenerateCsvReport() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(true);

        reportController.generateReportCsv("reportId", allRequestParams, response);

        verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=\"file-name.csv\"");
        verify(response, times(1)).setContentType("text/csv");
    }

    @Test(expected = RuntimeException.class)
    public void testGenerateReportWord_whenReportViewNotAvailable_thenThrowException() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(false);

        reportController.generateReportWord("reportId", allRequestParams, response);
    }

    @Test
    public void testGenerateReportWord_whenReportViewAvailable_thenGenerateWordReport() throws Exception {
        Map<String, Object> allRequestParams = new HashMap<>();
        HttpServletResponse response = mock(HttpServletResponse.class);

        testCommonReportLogic(true);

        reportController.generateReportWord("reportId", allRequestParams, response);

        verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=\"file-name.doc\"");
        verify(response, times(1)).setContentType("application/msword");
    }

    @Test
    public void testGenerateReportPdfPack_whenCompressionDisabled_thenHeaderAndContentTypeShouldBePdf() throws Exception {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.FALSE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getParams()).thenReturn(new HashMap<String, Object>());

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        when(objectReader.readValue(anyString())).thenReturn(reportRequestPackDto);

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        reportController.generateReportPdfPack("C27E6A0767CB90A132BE56505E1F8FD803D82897926FB812", "request", response);

        verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=\"file-name.pdf\"");
        verify(response, times(1)).setContentType("application/pdf");
    }

    @Test
    public void testGenerateReportPdfPack_whenCompressionEnabled_thenHeaderAndContentTypeShouldBeZip() throws Exception {
        ReportRequestPackDto reportRequestPackDto = mock(ReportRequestPackDto.class);
        when(reportRequestPackDto.getCompressReports()).thenReturn(Boolean.TRUE);

        ReportRequestDto reportRequestDto = mock(ReportRequestDto.class);
        when(reportRequestDto.getParams()).thenReturn(new HashMap<String, Object>());

        when(reportRequestPackDto.getReportRequestDtos()).thenReturn(Arrays.asList(reportRequestDto));

        when(objectReader.readValue(anyString())).thenReturn(reportRequestPackDto);

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        reportController.generateReportPdfPack("C27E6A0767CB90A132BE56505E1F8FD803D82897926FB812", "request", response);

        verify(response, times(1)).setHeader("Content-Disposition", "attachment; filename=\"file-name.zip\"");
        verify(response, times(1)).setContentType("application/zip");
    }

    private void testCommonReportLogic(boolean reportViewAvailable) {
        List<ReportDatasource> reportDatasourceList = createDummyReportDataSource();
        ReportView reportView = mock(ReportView.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);
        when(datasourceFactory.createDataSources(any(ReportIdentity.class), any(Map.class))).thenReturn(reportDatasourceList);
        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportTemplate.isAvailable()).thenReturn(reportViewAvailable);
        when(reportViewFactory.createReportView(any(ReportIdentity.class))).thenReturn(reportView);
    }

    private List<ReportDatasource> createDummyReportDataSource() {
        List<ReportDatasource> reportDatasourceList = new ArrayList<>();
        ReportDatasource reportDatasource = mock(ReportDatasource.class);
        Map<String, Object> params = new HashMap<>();
        params.put("reportFileName", "file-name");
        when(reportDatasource.getParameters()).thenReturn(params);
        reportDatasourceList.add(reportDatasource);
        return reportDatasourceList;
    }
}
