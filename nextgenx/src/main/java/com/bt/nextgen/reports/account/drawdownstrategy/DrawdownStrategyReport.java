package com.bt.nextgen.reports.account.drawdownstrategy;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDetailsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Report("drawdownStrategyReport")
public class DrawdownStrategyReport extends AccountReportV2 {

    private static final Logger logger = LoggerFactory.getLogger(DrawdownStrategyReport.class);

    @Autowired
    private DrawdownDetailsDtoService drawdownDetailsDtoService;

    @Autowired
    private DrawdownStrategyReportDataConverter reportDataConverter;

    @Autowired
    private JsonObjectMapper mapper;

    // Params
    private static final String ACCOUNT_ID = "account-id";
    private static final String DRAWDOWN_STRATEGY = "drawdown";
    private static final String ASSET_PRIORITY_LIST = "priority-list";

    // CMS
    private static final String DECLARATION = "DS-IP-0103";
    private static final String SUPER_DECLARATION = "DS-IP-0185";
    private static final String DRAWDOWN_STRATEGY_DESCRIPTION = "Ins-IP-0311";

    private static final String HIGH_VALUE_EXPLANATION_GRAPHIC = "drawdownExplanationHighValue";
    private static final String HIGH_VALUE_EXPLANATION_1 = "Ins-IP-0093";
    private static final String HIGH_VALUE_EXPLANATION_2 = "Ins-IP-0094";
    private static final String HIGH_VALUE_EXPLANATION_3 = "Ins-IP-0095";

    private static final String PRO_RATA_EXPLANATION_GRAPHIC = "drawdownExplanationProRata";
    private static final String PRO_RATA_EXPLANATION_1 = "Ins-IP-0096";
    private static final String PRO_RATA_EXPLANATION_2 = "Ins-IP-0097";
    private static final String PRO_RATA_EXPLANATION_3 = "Ins-IP-0098";

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String drawdownStrategy = (String) params.get(DRAWDOWN_STRATEGY);
        String assetPriorityList = (String) params.get(ASSET_PRIORITY_LIST);

        DrawdownStrategy strategy = DrawdownStrategy.forIntlId(drawdownStrategy);

        if (StringUtils.isEmpty(accountId) || strategy == null) {
            String error = "DrawdownStrategyReport: Both Account ID and selected Drawdown Strategy are required";
            logger.error(error);
            throw new IllegalArgumentException(error);
        }

        DrawdownDetailsDto detailsDto = null;
        if (DrawdownStrategy.ASSET_PRIORITY.equals(strategy)) {
            detailsDto = getAssetPriorityDetails(accountId, assetPriorityList);
        }

        DrawdownStrategyReportData reportData = reportDataConverter.toReportData(strategy, detailsDto);
        return Collections.singletonList(reportData);
    }

    private DrawdownDetailsDto getAssetPriorityDetails(String accountId, String assetPriorityList) {
        DrawdownDetailsDto detailsDto;
        // Parse list from JSON if printed from edit screen, otherwise use the list stored in Avaloq
        if (!StringUtils.isEmpty(assetPriorityList)) {
            try {
                detailsDto = mapper.readValue(assetPriorityList, DrawdownDetailsDto.class);
            } catch (IOException e) {
                String error = "DrawdownStrategyReport: Unable to map asset priority list details when generating PDF: ";
                logger.error(error, e);
                throw new IllegalArgumentException(error + e);
            }
        } else {
            detailsDto = drawdownDetailsDtoService.find(new AccountKey(accountId), new FailFastErrorsImpl());
        }

        return detailsDto;
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Client authorisation - drawdown strategy";
    }

    @ReportBean("drawdownDescription")
    public String getDescription(Map<String, String> params) {
        return getContent(DRAWDOWN_STRATEGY_DESCRIPTION, null);
    }

    @ReportBean("investorDeclaration")
    public String getDeclaration(Map<String, Object> params) {
        WrapAccountDetail account = getAccount(getAccountKey(params), params, getServiceType(params));
        Boolean isSuper = AccountStructureType.SUPER.equals(account.getAccountStructureType());

        if (isSuper) {
            return getContent(SUPER_DECLARATION, null);
        } else {
            return getContent(DECLARATION, null);
        }
    }

    @ReportBean("greenTickIcon")
    public Renderable getConfirmedIcon(Map<String, String> params) {
        String imageLocation = getContent("greenTickIcon");
        return getRasterImage(imageLocation);
    }

    @ReportBean("explanationGraphic")
    public Renderable getDrawdownExplanationGraphic(Map<String, String> params) {
        String imageLocation = selectContentForDrawdownStrategy(params.get(DRAWDOWN_STRATEGY), HIGH_VALUE_EXPLANATION_GRAPHIC,
                PRO_RATA_EXPLANATION_GRAPHIC);
        if (imageLocation != null) {
            return getRasterImage(imageLocation);
        }
        return null;
    }

    @ReportBean("explanationStep1Funds")
    public String getExplanationStepOne(Map<String, String> params) {
        return selectContentForDrawdownStrategy(params.get(DRAWDOWN_STRATEGY), HIGH_VALUE_EXPLANATION_1, PRO_RATA_EXPLANATION_1);
    }

    @ReportBean("explanationStep2Portfolios")
    public String getExplanationStepTwo(Map<String, String> params) {
        return selectContentForDrawdownStrategy(params.get(DRAWDOWN_STRATEGY), HIGH_VALUE_EXPLANATION_2, PRO_RATA_EXPLANATION_2);
    }

    @ReportBean("explanationStep3Shares")
    public String getExplanationStepThree(Map<String, String> params) {
        return selectContentForDrawdownStrategy(params.get(DRAWDOWN_STRATEGY), HIGH_VALUE_EXPLANATION_3, PRO_RATA_EXPLANATION_3);
    }

    // Select the content appropriate for HighValue or ProRata strategies. Not applicable for Asset Priority List strategy.
    private String selectContentForDrawdownStrategy(String drawdownStrategy, String highValueContent, String proRataContent) {
        DrawdownStrategy strategy = DrawdownStrategy.forIntlId(drawdownStrategy);
        String content = null;

        if (DrawdownStrategy.HIGH_PRICE.equals(strategy)) {
            content = getContent(highValueContent, null);
        } else if (DrawdownStrategy.PRORATA.equals(strategy)) {
            content = getContent(proRataContent, null);
        }

        return content;
    }

}
