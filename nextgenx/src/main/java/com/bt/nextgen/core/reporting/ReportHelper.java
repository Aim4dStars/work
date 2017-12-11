package com.bt.nextgen.core.reporting;

import java.util.List;
import java.util.Map;

public interface ReportHelper {
    Map<String, ReportProperties> getReportPropertiesMap(List<String> reportNames);
}
