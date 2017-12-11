package com.bt.nextgen.core.reporting;

import static com.bt.nextgen.core.reporting.ReportIdentity.ReportIdentityString.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.bt.nextgen.core.reporting.view.DefaultReportViewImpl;
import com.bt.nextgen.core.reporting.view.ReportView;
import com.bt.nextgen.core.reporting.view.ReportViewProcessor;
import com.bt.nextgen.core.reporting.view.ReportViewProcessorJasperImpl;

public class ReportViewProcessorJasperImplTest
{
	@Test
	public void testViewProcessor_realContent() throws Exception
	{
		ReportView reportView = mock(DefaultReportViewImpl.class);
		ReportTemplate reportTemplate = mock(ReportTemplate.class);

		when(reportView.getReportTemplate()).thenReturn(reportTemplate);
		when(reportTemplate.getId()).thenReturn(asIdentity("anything"));
		when(reportTemplate.getType()).thenReturn("application/vnd.jrxml");
		when(reportTemplate.getAsStream()).thenReturn(getClass().getResourceAsStream("/reports/helloworld.jrxml"));

		ReportViewProcessor processor = new ReportViewProcessorJasperImpl();

		processor.process(reportView);
	}

	@Test
	public void testViewProcessor_nonJasperXmlFileReturnNull() throws Exception
	{
		ReportView reportView = mock(DefaultReportViewImpl.class);
		ReportTemplate reportTemplate = mock(ReportTemplate.class);

		when(reportView.getReportTemplate()).thenReturn(reportTemplate);
		when(reportTemplate.getType()).thenReturn("application/vnd.jasper");

		ReportViewProcessor processor = new ReportViewProcessorJasperImpl();

		assertTrue(processor.process(reportView) == null);
	}
}
