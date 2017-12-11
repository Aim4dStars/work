package com.bt.nextgen.core.reporting;

import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ResourceNotFoundException;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.RenderableUtil;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.renderers.BatikRenderer;
import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("squid:S1172")
public abstract class BaseReport implements ReportData {
    private static final String REPORT_BASE_DIR = "cms.basedir.report";

    @Autowired
    protected CmsService cmsService;

    @Autowired
    protected BadgingService badgingService;

    @Autowired
    private Configuration configuration;

    protected String getReportDir() {
        return configuration.getString(REPORT_BASE_DIR);
    }

    protected Renderable getVectorImage(String imageLocation) {
        try {
            InputStream in = new DefaultResourceLoader().getResource(getReportDir() + imageLocation).getInputStream();
            BatikRenderer renderer = BatikRenderer.getInstance(in);
            return renderer;
        } catch (JRException | IOException e) {
            throw new ResourceNotFoundException("Unable to find or generate report icon: " + imageLocation, e);
        }
    }

    protected Renderable getRasterImage(String imageLocation) {
        try {
            InputStream in = new DefaultResourceLoader().getResource(getReportDir() + imageLocation).getInputStream();
            RenderableUtil util = RenderableUtil.getInstance(DefaultJasperReportsContext.getInstance());
            return util.getRenderable(in, OnErrorTypeEnum.ERROR);
        } catch (JRException | IOException e) {
            throw new ResourceNotFoundException("Unable to find or generate report icon: " + imageLocation, e);
        }

    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return null;
    }

    @Override
    public Collection<String> getReportPageNames(Collection<?> data) {
        return null;
    }

    @Override
    public String getReportFileName(Collection<?> data) {
        return null;
    }

    @Override
    public int getThreadPoolSize() {
        return 1;
    }

    @ReportBean("REPORTS_BASE")
    public String getReportBaseDir(Map<String, String> params) {
        return configuration.getString(REPORT_BASE_DIR) + "/jasper";
    }

    @ReportBean("now")
    public DateTime getReportGenerationTime(Map<String, String> params) {
        return new DateTime();
    }

    @ReportBean("IMAGE_BASE")
    public String getImageSrc(Map<String, String> params) {
        String baseReport = configuration.getString(REPORT_BASE_DIR) + "images";
        return baseReport;
    }

    @ReportImage("reportLogo")
    public Renderable getReportLogo(Map<String, String> params) {
        String logoLocation = badgingService.getBadgeForCurrentUser(new FailFastErrorsImpl()).getReportLogo();
        return getVectorImage(logoLocation);
    }

    @ReportImage("reportFooterLandscapeBackground")
    public Renderable getReportFooterLandscape(Map<String, String> params) {
        String imageLocation = cmsService.getContent("reportFooterLandscapeImage");
        return getVectorImage(imageLocation);
    }

    @ReportImage("reportFooterPortraitBackground")
    public Renderable getReportFooterPortrait(Map<String, String> params) {
        String imageLocation = cmsService.getContent("reportFooterPortraitImage");
        return getVectorImage(imageLocation);
    }

    @ReportImage("reportFatFooterLandscapeBackground")
    public Renderable getReportFatFooterLandscape(Map<String, String> params) {
        String imageLocation = cmsService.getContent("reportFatFooterLandscapeImage");
        return getRasterImage(imageLocation);
    }

    @ReportImage("iconAccount")
    public Renderable getAccountIcon(Map<String, String> params) {
        String imageLocation = cmsService.getContent("iconAccount");
        return getRasterImage(imageLocation);
    }

    @ReportImage("iconContact")
    public Renderable getContactIcon(Map<String, String> params) {
        String imageLocation = cmsService.getContent("iconContact");
        return getRasterImage(imageLocation);
    }

    @ReportImage("iconAdviser")
    public Renderable getAdviserIcon(Map<String, String> params) {
        String imageLocation = cmsService.getContent("iconAdviser");
        return getRasterImage(imageLocation);
    }

    @ReportImage("reportFatFooterPortraitBackground")
    public Renderable getReportFatFooterPortrait(Map<String, String> params) {
        String imageLocation = cmsService.getContent("reportFatFooterPortraitImage");
        return getRasterImage(imageLocation);
    }

}
