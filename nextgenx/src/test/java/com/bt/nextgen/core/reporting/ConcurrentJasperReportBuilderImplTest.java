package com.bt.nextgen.core.reporting;

import com.bt.nextgen.core.reporting.datasource.ReportDatasource;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewImpl;
import com.bt.nextgen.core.reporting.view.ReportViewProcessor;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import static com.bt.nextgen.core.reporting.ReportIdentity.ReportIdentityString.asIdentity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JasperFillManager.class)
public class ConcurrentJasperReportBuilderImplTest {
    @InjectMocks
    private ConcurrentJasperReportBuilderImpl concurrentJasperReportBuilder;

    @Mock
    private ReportViewProcessor reportViewProcessor;

    @Mock
    private ReportHelper reportHelper;

    private Logger logger;

    @Before
    public void setUp() throws Exception {
        logger = mock(Logger.class);
        ReflectionTestUtils.setField(concurrentJasperReportBuilder, "logger", logger);
    }

    @Test
    public void testRenderToPdf_whenCompressionDisabled_thenGenerateSinglePdf() throws Exception {
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

        concurrentJasperReportBuilder.renderToPdf(Arrays.asList(reportView), false, out);

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

        List<ReportView> reportViews = new ArrayList<>();
        reportViews.add(reportView);

        ReportProperties reportProperties = mock(ReportProperties.class);
        when(reportProperties.getFilename()).thenReturn("Filename");

        Map<String, ReportProperties> propertiesMap = new HashMap<>();
        propertiesMap.put("anything", reportProperties);

        when(reportHelper.getReportPropertiesMap(anyListOf(String.class))).thenReturn(propertiesMap);

        concurrentJasperReportBuilder.renderToPdf(Arrays.asList(reportView), true, zipOutputStream);

        assertTrue(out.size() > 0);

        byte[] data = out.toByteArray();
        byte[] header = Arrays.copyOfRange(data, 0, 2);

        assertEquals("PK", new String(header));
    }

    @Test
    public void testRenderToPdf_whenReportRenderingFails() throws IOException, JRException {
        final ReportSource reportSource = mock(ReportSource.class);
        final ReportView reportView = mock(ReportViewImpl.class);
        final ReportTemplate reportTemplate = mock(ReportTemplate.class);
        final ReportDatasource dataSource = mock(ReportDatasource.class);

        when(reportView.getReportTemplate()).thenReturn(reportTemplate);
        when(reportView.getReportSource()).thenReturn(reportSource);
        when(reportView.getReportDataSource()).thenReturn(dataSource);
        when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
        when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
        when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<ReportView> reportViews = new ArrayList<>();
        reportViews.add(reportView);

        PowerMockito.mockStatic(JasperFillManager.class);
        when(JasperFillManager.fillReport(Mockito.any(JasperReport.class), Mockito.any(Map.class), Mockito.any(JRDataSource.class)))
                .thenThrow(new JRException("Error"));
        try {
            concurrentJasperReportBuilder.renderToPdf(reportViews, false, out);
        } catch (Exception e) {
            verify(logger, times(1)).error(anyString(), Mockito.any(JRException.class));
        }
    }
}
