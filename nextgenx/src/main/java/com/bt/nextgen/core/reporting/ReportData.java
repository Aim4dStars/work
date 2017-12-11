package com.bt.nextgen.core.reporting;

import java.util.Collection;
import java.util.Map;

/**
 * Abstraction of a report
 */
public interface ReportData {

    /**
     * Return the main data to be used in the report.
     * 
     * @param Map
     *            of parameters used to retrieve data
     * @param Map
     *            of dataCollections to hold previously obtained data for re-use
     * @return Collection of data
     */
    Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections);

    /**
     * Return the page names for the report.
     * 
     * @param Collection
     *            of data as returned by getData
     * @return Collection of String page names
     */
    Collection<String> getReportPageNames(Collection<?> data);

    /**
     * Return the file name of the downloaded report file. Report template ID is used if custom name not provided.
     * 
     * @param Collection
     *            of data as returned by getData
     * @return String file name
     */
    String getReportFileName(Collection<?> data);

    /**
     * Return the maximum number of threads to use when concurrently generating report groups
     * 
     * @return int max thread count allowed
     */
    int getThreadPoolSize();

}
