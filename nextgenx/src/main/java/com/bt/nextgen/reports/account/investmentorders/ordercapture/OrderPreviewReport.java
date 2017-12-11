package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Report("orderPreviewReport")
public class OrderPreviewReport extends AbstractOrderReport {

    @Autowired
    private OptionsService optionsService;

    private static final String REPORT_TITLE = "Your client authorisation for an investment order";
    private static final String REPORT_PORTFOLIO_FEE = " and portfolio management fee";
    private static final String REPORT_SUBTITLE = "Order instructions";
    private static final String PORTFOLIO_FEE_DISCLAIMER_CONTENT = "DS-IP-0170";
    private static final String PORTFOLIO_FEE_DECLARATION_CONTENT = "DS-IP-0171";
    private static final String DECLARATION_CONTENT = "DS-IP-0044";
    private static final String ORDER_DISCLAIMER_CONTENT = "DS-IP-0168";
    private static final String FEE_DISCLAIMER_CONTENT = "DS-IP-0085";
    private static final String PORTFOLIO_SUPER_FEE_DISCLAIMER_CONTENT = "DS-IP-0191";
    private static final String PORTFOLIO_SUPER_FEE_DECLARATION_CONTENT = "DS-IP-0192";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0194";
    private static final String CMA_DISCLAIMER_CONTENT = "DS-IP-0198";

    @ReportBean("declaration")
    public String getDeclaration(Map<String, Object> params, Map<String, Object> dataCollections) {
        StringBuilder declaration = new StringBuilder();
        OrderGroupReportData orderGroupReportData = getOrderGroupData(params, dataCollections);
        declaration = declaration.append(getContent(DECLARATION_CONTENT));
        if (orderGroupReportData.isPortfolioFeePresent()) {
            boolean isSuper = AccountStructureType.SUPER == getAccountStructureType(params, dataCollections);
            declaration.append("<br/>").append(getPortfolioFeeDeclarationContent(isSuper));
        }
        return declaration.toString();
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params, Map<String, Object> dataCollections) {
        StringBuilder disclaimer = new StringBuilder();
        OrderGroupReportData orderGroupReportData = getOrderGroupData(params, dataCollections);
        if (orderGroupReportData.isPortfolioFeePresent()) {
            boolean isSuper = AccountStructureType.SUPER == getAccountStructureType(params, dataCollections);
            disclaimer.append(getPortfolioFeeDisclaimerContent(isSuper)).append("<br/><br/>");
        }

        if (orderGroupReportData.hasShareAssets() || orderGroupReportData.hasPortfolioAssets()) {
            disclaimer.append(getContent(ORDER_DISCLAIMER_CONTENT)).append("<br/><br/>");
        }
        
        if (orderGroupReportData.hasShareAssets()) {
            disclaimer.append(getContent(FEE_DISCLAIMER_CONTENT));
        }

        boolean hasNonCashAssets = optionsService.hasFeature(OptionKey.valueOf(OptionNames.NONCASHASSETS), getAccountKey(params),
                new FailFastErrorsImpl());
        if (!hasNonCashAssets) {
            disclaimer.append(getContent(CMA_DISCLAIMER_CONTENT));
        } else {
            disclaimer.append(getContent(DISCLAIMER_CONTENT));
        }

        return disclaimer.toString();
    }

    @ReportBean("title")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        String reportTitle = REPORT_TITLE;
        OrderGroupReportData orderGroupReportData = getOrderGroupData(params, dataCollections);
        if (orderGroupReportData.isPortfolioFeePresent()) {
            reportTitle = reportTitle + REPORT_PORTFOLIO_FEE;
        }
        return reportTitle;
    }

    @ReportBean("subtitle")
    public String getReportSubTitle() {
        return REPORT_SUBTITLE;
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }

    @Override
    @ReportBean("reportFileName")
    public String getReportFileName(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        StringBuilder filename = new StringBuilder(account.getAccountNumber());
        filename.append(" - ");
        filename.append("Order Authorisation");
        return filename.toString();
    }

    protected String getPortfolioFeeDeclarationContent(boolean isSuper) {
        return isSuper ? getContent(PORTFOLIO_SUPER_FEE_DECLARATION_CONTENT) : getContent(PORTFOLIO_FEE_DECLARATION_CONTENT);
    }

    protected String getPortfolioFeeDisclaimerContent(boolean isSuper) {
        return isSuper ? getContent(PORTFOLIO_SUPER_FEE_DISCLAIMER_CONTENT) : getContent(PORTFOLIO_FEE_DISCLAIMER_CONTENT);
    }

    protected AccountStructureType getAccountStructureType(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        return account.getAccountStructureType();
    }

}
