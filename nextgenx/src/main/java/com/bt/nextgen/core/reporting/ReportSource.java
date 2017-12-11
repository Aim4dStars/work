package com.bt.nextgen.core.reporting;

/**
 * An ability for reports to be loaded via different mechanisms
 */
public interface ReportSource
{
	/**
	 * Load a report from this source, using the given id
	 * @param reportId
	 * @return The report
	 */
	ReportTemplate getReportTemplate(ReportIdentity reportId);
}
