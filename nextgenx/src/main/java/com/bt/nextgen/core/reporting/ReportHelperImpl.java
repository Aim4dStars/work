package com.bt.nextgen.core.reporting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.bt.nextgen.core.reporting.stereotype.Report;

@Component
public class ReportHelperImpl implements ReportHelper, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Return report properties map.  Currently only the report filename is intercepted
     *
     * @param reportNames list of report names
     * @return a map of report properties
     */
    public Map<String, ReportProperties> getReportPropertiesMap(List<String> reportNames) {
        Map<String, ReportProperties> propertiesMap = new HashMap<>();

        for (String reportName : reportNames) {
            Report reportAnnotation = applicationContext.findAnnotationOnBean(reportName, Report.class);

            String filename = reportAnnotation.filename();

            if (StringUtils.isEmpty(filename)) {
                filename = reportName;
            }

            propertiesMap.put(reportName, new ReportProperties(filename));
        }

        return propertiesMap;
    }
}
