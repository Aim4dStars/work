package com.bt.nextgen.core.reporting;

import com.bt.nextgen.cms.service.CmsService;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Cms source of reports - you need to be careful caching responses from this source as a given report id, user combination could
 * have a different underlying CmsEntry. Cms content can vary for a given key depending on the user calling.
 */
@Service
public class ReportCmsSourceImpl implements ReportSource {
    private static final String REPORT_BASE_DIR = "cms.basedir.report";

    private CmsService cmsService;
    private Configuration configuration;

    @Autowired
    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @Autowired
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public ReportTemplate getReportTemplate(ReportIdentity reportId) {
        String reportBase = configuration.getString(REPORT_BASE_DIR);
        return new ReportCmsEntryImpl(cmsService.getRawContent(reportId.getTemplateKey()), reportBase, reportId);
    }
}
