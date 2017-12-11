package com.bt.nextgen.core.reporting;

import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ResourceNotFoundException;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
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
import java.util.Collections;
import java.util.Map;

@SuppressWarnings("squid:S1172")
public abstract class BaseReportV2 implements ReportData {
    private static final String REPORT_BASE_DIR = "cms.basedir.report";

    @Autowired
    private CmsService cmsService;

    @Autowired
    private BadgingService badgingService;

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
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getReportPageNames(Collection<?> data) {
        return Collections.emptyList();
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

    @ReportBean("IMAGE_BASE")
    public String getImageBaseDir(Map<String, String> params) {
        return configuration.getString(REPORT_BASE_DIR) + "/images";
    }

    @ReportBean("now")
    public DateTime getReportGenerationTime(Map<String, String> params) {
        return new DateTime();
    }

    protected Renderable getReportLogo(Map<String, Object> params) {
        String logoLocation = badgingService.getBadgeForCurrentUser(new FailFastErrorsImpl()).getReportLogoV2();
        return getVectorImage(logoLocation);
    }

    protected Renderable getAccountIcon(Map<String, Object> params) {
        String imageLocation = cmsService.getContent("iconAccountV2");
        return getVectorImage(imageLocation);
    }

    protected Renderable getContactIcon(Map<String, Object> params) {
        String imageLocation = cmsService.getContent("iconContactV2");
        return getVectorImage(imageLocation);
    }

    protected Renderable getAdviserIcon(Map<String, Object> params) {
        String imageLocation = cmsService.getContent("iconAdviserV2");
        return getVectorImage(imageLocation);
    }

    protected Renderable getReportFatFooterPortrait(Map<String, Object> params) {
        String imageLocation = cmsService.getContent("reportFatFooterPortraitImageV2");
        return getRasterImage(imageLocation);
    }

    protected Renderable getReportFatFooterLandscape(Map<String, Object> params) {
        String imageLocation = cmsService.getContent("reportFatFooterLandscapeImageV2");
        return getRasterImage(imageLocation);
    }

    protected String getContent(String key, String[] params) {
        if (params == null) {
            return cmsService.getContent(key);
        } else {
            return cmsService.getDynamicContent(key, params);
        }
    }

    protected String getContent(String key) {
        return getContent(key, null);
    }
}
