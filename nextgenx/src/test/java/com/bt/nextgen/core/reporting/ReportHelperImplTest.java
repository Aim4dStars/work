package com.bt.nextgen.core.reporting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.bt.nextgen.core.reporting.stereotype.Report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportHelperImplTest {
    @InjectMocks
    private ReportHelperImpl reportHelper;

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void getReportPropertiesMap_whenFilenameAnnotationDefined_thenUseFilenameDefinedForAnnotation() throws Exception {
        Report reportAnnotation = mock(Report.class);
        when(reportAnnotation.filename()).thenReturn("Portfolio valuation");

        when(applicationContext.findAnnotationOnBean(anyString(), any(Class.class))).thenReturn(reportAnnotation);

        List<String> reportNames = Arrays.asList("portfolioValuationReportV2");
        Map<String, ReportProperties> reportPropertiesMap = reportHelper.getReportPropertiesMap(reportNames);

        assertNotNull(reportPropertiesMap);
        assertTrue(reportPropertiesMap.size() == 1);
        assertEquals("Portfolio valuation", reportPropertiesMap.values().iterator().next().getFilename());
    }

    @Test
    public void getReportPropertiesMap_whenFilenameAnnotationNotDefined_thenUseReportIdentityAsFilename() throws Exception {
        Report reportAnnotation = mock(Report.class);
        when(reportAnnotation.filename()).thenReturn("");

        when(applicationContext.findAnnotationOnBean(anyString(), any(Class.class))).thenReturn(reportAnnotation);

        List<String> reportNames = Arrays.asList("histCorporateActionCsvReport");
        Map<String, ReportProperties> reportPropertiesMap = reportHelper.getReportPropertiesMap(reportNames);

        assertNotNull(reportPropertiesMap);
        assertTrue(reportPropertiesMap.size() == 1);
        assertEquals("histCorporateActionCsvReport", reportPropertiesMap.values().iterator().next().getFilename());
    }
}
