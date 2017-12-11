package com.bt.nextgen.reports.account;

import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.engine.Renderable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public abstract class AbstractOrderReport extends AccountReport {

    private static final String ICON_BUY = "iconBuy";
    private static final String ICON_SELL = "iconSell";

    protected static final String ORDER_ID_PARAM = "order-id";
    protected static final String ORDER_PARAM = "order";

    private  String reportType = null;
    private  String subReportType = null;
    private  String declaration = null;

    @Autowired
    protected ContentDtoService contentService;


    public AbstractOrderReport(String reportType, String subReportType, String declaration) {
        super();
        this.reportType = reportType;
        this.subReportType = subReportType;
        this.declaration = declaration;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("reportType")
    public String getReportType(Map<String, String> params) {
        return reportType;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("subReportType")
    public String getSubReportType(Map<String, String> params) {
        return subReportType;
    }

    /**
     * Gets the declaration.
     *
     * @param params
     *            the params
     * @return the declaration
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("declaration")
    public String getDeclaration(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(declaration);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    /**
     * Gets the icon buy.
     *
     * @param params
     *            the params
     * @return the icon buy
     */
    @SuppressWarnings("squid:S1172")
    @ReportImage(ICON_BUY)
    public Renderable getIconBuy(Map<String, String> params) {
        return generateIconRenderer(ICON_BUY);
    }

    /**
     * Gets the icon sell.
     *
     * @param params
     *            the params
     * @return the icon sell
     */
    @SuppressWarnings("squid:S1172")
    @ReportImage(ICON_SELL)
    public Renderable getIconSell(Map<String, String> params) {
        return generateIconRenderer(ICON_SELL);
    }

    /**
     * Generate the renderable image for the given icon name.
     *
     * @param iconName
     *            the icon name
     * @return the renderable
     */
    private Renderable generateIconRenderer(final String iconName) {
        String imageLocation = cmsService.getContent(iconName);
        return getVectorImage(imageLocation);
    }

}
