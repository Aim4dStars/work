package com.bt.nextgen.core.reporting;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.view.DefaultReportViewImpl;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewImpl;
import com.bt.nextgen.core.reporting.view.ReportViewProcessor;

import static com.bt.nextgen.core.reporting.ReportIdentity.ReportIdentityString.asIdentity;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JasperReportBuilderImplTest {
    @InjectMocks
    private JasperReportBuilderImpl jasperReportBuilder;

    @Mock
    private ReportViewProcessor reportViewProcessor;

    @Mock
    private ReportHelper reportHelper;

    @Test
    public void testRenderToPdf_realContent() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(DefaultReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);

        jasperReportBuilder.renderToPdf(reportView, out);

        verify(dataSource, times(1)).getBeans();
        verify(dataSource, times(1)).getParameters();
    }

    @Test
    public void testRenderToPdf_realContent_usingViewProcessor() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(ReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);

        jasperReportBuilder.renderToPdf(reportView, out);
    }

    @Test
    public void testRenderToCsv_realContent() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(DefaultReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);
        List<ReportDatasource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);

        jasperReportBuilder.renderToCsv(reportView, dataSources, out);

        verify(dataSource, times(1)).getBeans();
        verify(dataSource, times(1)).getParameters();
    }

    @Test
    public void testRenderToCsv_realContent_usingViewProcessor() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(ReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);
        List<ReportDatasource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);

        jasperReportBuilder.renderToCsv(reportView, dataSources, out);
    }

    @Test
    public void testRenderToExcel_realContent() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(DefaultReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);
        List<ReportDatasource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);

        jasperReportBuilder.renderToExcel(reportView, dataSources, out);

        verify(dataSource, times(1)).getBeans();
        verify(dataSource, times(1)).getParameters();
    }

    @Test
    public void testRenderToExcel_realContent_usingViewProcessor() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(ReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);
        List<ReportDatasource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);

        jasperReportBuilder.renderToExcel(reportView, dataSources, out);
    }

    @Test
    public void testRenderToWord_realContent() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(DefaultReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);
        List<ReportDatasource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);

        jasperReportBuilder.renderToWord(reportView, dataSources, out);

        verify(dataSource, times(1)).getBeans();
        verify(dataSource, times(1)).getParameters();
    }

    @Test
    public void testRenderToWord_realContent_usingViewProcessor() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(ReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        OutputStream out = mock(OutputStream.class);
        List<ReportDatasource> dataSources = new ArrayList<>();
        dataSources.add(dataSource);


        jasperReportBuilder.renderToWord(reportView, dataSources, out);
    }

    @Test
    public void testRenderToPdf_whenCompressionDisabled_thenGeneratePdfFile() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(ReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        List<ReportView> reportViews = new ArrayList<>();
        reportViews.add(reportView);

        jasperReportBuilder.renderToPdf(Arrays.asList(reportView), false, out);

        assertTrue(out.size() > 0);

        byte[] data = out.toByteArray();
        byte[] header = Arrays.copyOfRange(data, 0, 4);

        assertEquals("%PDF", new String(header));
    }

    @Test
    public void testRenderToPdf_whenCompressionEnabled_thenGenerateZipFile() throws Exception {
        ReportSource reportSource = mock(ReportSource.class);
        ReportView reportView = mock(ReportViewImpl.class);
        ReportTemplate reportTemplate = mock(ReportTemplate.class);

        ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(out);

        ReportProperties reportProperties = mock(ReportProperties.class);
        when(reportProperties.getFilename()).thenReturn("Filename");

        Map<String, ReportProperties> propertiesMap = new HashMap<>();
        propertiesMap.put("anything", reportProperties);

        when(reportHelper.getReportPropertiesMap(anyListOf(String.class))).thenReturn(propertiesMap);

        List<ReportView> reportViews = new ArrayList<>();
        reportViews.add(reportView);

        jasperReportBuilder.renderToPdf(Arrays.asList(reportView), true, zipOutputStream);

        assertTrue(out.size() > 0);

        byte[] data = out.toByteArray();
        byte[] header = Arrays.copyOfRange(data, 0, 2);

        assertEquals("PK", new String(header));
    }
}
