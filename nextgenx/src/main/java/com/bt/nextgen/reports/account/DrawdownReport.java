package com.bt.nextgen.reports.account;

import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Report("drawdownReport")
public class DrawdownReport extends AccountReport {
    private static final String DECLARATION = "DS-IP-0103";
    private static final String REPORT_TYPE = "Client authorisation";
    private static final String SUB_REPORT_TYPE = "Drawdown strategy";

    private static final String EXPLANATION_ONE = "Ins-IP-0093";
    private static final String EXPLANATION_TWO = "Ins-IP-0094";
    private static final String EXPLANATION_THREE = "Ins-IP-0095";
    private static final String EXPLANATION_FOUR = "Ins-IP-0096";
    private static final String EXPLANATION_FIVE = "Ins-IP-0097";
    private static final String EXPLANATION_SIX = "Ins-IP-0098";

    @Autowired
    protected ContentDtoService contentService;

    @SuppressWarnings("squid:S1172")
    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return REPORT_TYPE;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("declaration")
    public String getDisclaimer(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DECLARATION);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("subReportType")
    public String getSubReportType(Map<String, String> params) {
        return SUB_REPORT_TYPE;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("explanationOne")
    public String getExplanationOne(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(EXPLANATION_ONE);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("explanationTwo")
    public String getExplanationTwo(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(EXPLANATION_TWO);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("explanationThree")
    public String getExplanationThree(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(EXPLANATION_THREE);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("explanationFour")
    public String getExplanationFour(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(EXPLANATION_FOUR);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("explanationFive")
    public String getExplanationFive(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(EXPLANATION_FIVE);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("explanationSix")
    public String getExplanationSix(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(EXPLANATION_SIX);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

}
