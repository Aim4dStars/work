package com.bt.nextgen.reports.corporateaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportInitializer;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.AccountReport;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

/**
 * This report is not currently used and may be removed in the near future
 */
@Report("corporateActionRecommendationReport")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@PreAuthorize("isAuthenticated() and (hasPermission(null, 'View_security_events') OR hasPermission(null, 'View_intermediary_reports')"
        + " OR hasPermission(null, 'View_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
public class CorporateActionRecommendationReport extends AccountReport {

    private static final String DECLARATION = "DS-IP-0069";

    @Autowired
    private CorporateActionDetailsDtoService corporateActionDetailsDtoService;

    private ServiceErrors serviceErrors = new FailFastErrorsImpl();

    private CorporateActionDetailsBaseDto corporateActionDetailsBaseDto;

    @ReportInitializer
    public void init(Map<String, String> params) {
        String corporateActionIdString = params.get(UriMappingConstants.CORPORATE_ACTION_ID_URI_MAPPING);
        final CorporateActionDtoKey key = new CorporateActionDtoKey(EncodedString.toPlainText(corporateActionIdString));
        corporateActionDetailsBaseDto = corporateActionDetailsDtoService.find(key, serviceErrors);
    }

    @ReportBean("declaration")
    @SuppressWarnings("squid:S1172")
    public String getDescription(Map<String, String> params) {
        return cmsService.getContent(DECLARATION);
    }

    @ReportBean("reportType")
    @SuppressWarnings("squid:S1172")
    public String getReportName(Map<String, String> params) {
        return "Client authorisation";
    }

    @ReportBean("subReportType")
    @SuppressWarnings("squid:S1172")
    public String getSubReportName(Map<String, String> params) {
        return "Corporate action recommendation";
    }

    @ReportBean("corporateActionDetails")
    @SuppressWarnings("squid:S1172")
    public CorporateActionDetailsBaseDto getCorporateActionDetail(Map<String, String> params) {
        return corporateActionDetailsBaseDto;
    }

    @ReportBean("corporateActionAccountDetails")
    public CorporateActionAccountDetailsDto getCorporateActionAccountDetails(Map<String, String> params) {
        final String accountIdString = EncodedString.toPlainText(params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING));

        for (CorporateActionAccountDetailsDto account : ((CorporateActionDetailsDto) corporateActionDetailsBaseDto).getAccounts()) {
            if (accountIdString.equals(EncodedString.toPlainText(account.getAccountKey()))) {
                return account;
            }
        }

        return null;
    }

    @ReportBean("corporateActionSummary")
    @SuppressWarnings("squid:S1172")
    public List<String> getCorporateActionSummary(Map<String, String> params) {
        return corporateActionDetailsBaseDto.getSummary();
    }

    @ReportBean("corporateActionOptions")
    @SuppressWarnings("squid:S1172")
    public List<CorporateActionOptionDto> getCorporateActionOption(Map<String, String> params) {
        return corporateActionDetailsBaseDto.getOptions();
    }

    @ReportBean("corporateActionSelectedOptionSummary")
    @SuppressWarnings("squid:S1172")
    public List<String> getCorporateActionRecommendedSummary(Map<String, String> params) {
        List<String> optionDisplayString = new ArrayList<>();
        String summaryString;

        if (corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.BUY_BACK.name())) {
            String summary = params.get(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING);
            String units = params.get(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING);
            String optionIds = params.get(UriMappingConstants.CORPORATE_ACTION_ID_URI_MAPPING);
            optionDisplayString = getBuyBacksSummary(summary, units, optionIds);
        } else if (corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.PRO_RATA_PRIORITY_OFFER.name()) ||
                corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.EXERCISE_RIGHTS.name()) ||
                corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.EXERCISE_CALL_OPTION.name()) ||
                corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.EXERCISE_RIGHTS_WITH_OPT.name())) {
            int optionId = Integer.parseInt(params.get(UriMappingConstants.CORPORATE_ACTION_OPTION_URI_MAPPING));
            String units = params.get(UriMappingConstants.CORPORATE_ACTION_OPTION_UNITS_MAPPING);
            String summary = params.get(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING);
            String oversubscribe = params.get(UriMappingConstants.CORPORATE_ACTION_OPTION_OVERSUBSCRIBE_MAPPING);
            summaryString = getExerciseRightsSummary(optionId, summary, units, oversubscribe);
            optionDisplayString.add(summaryString);
        } else {
            summaryString = params.get(UriMappingConstants.CORPORATE_ACTION_SUMMARY_URI_MAPPING);
            optionDisplayString.add(summaryString);
        }

        return optionDisplayString;
    }

    @ReportBean("corporateActionSelectedOptionTitle")
    @SuppressWarnings("squid:S1172")
    public List<String> getCorporateActionRecommendedTitle(Map<String, String> params) {
        List<String> titleList = new ArrayList<>();

        if (CorporateActionType.BUY_BACK.name().equals(corporateActionDetailsBaseDto.getCorporateActionType())) {
            String titles = params.get(UriMappingConstants.CORPORATE_ACTION_TITLE_URI_MAPPING);

            for (String title : titles.split(",")) {
                titleList.add(title.trim());
            }
        } else {
            titleList.add(params.get(UriMappingConstants.CORPORATE_ACTION_TITLE_URI_MAPPING));
        }

        return titleList;
    }

    @ReportBean("corporateActionMinimumPrice")
    @SuppressWarnings("squid:S1172")
    public String getCorporateActionMinimumPrice(Map<String, String> params) {
        String minimumPrice = "";
        if (corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.BUY_BACK.name())) {
            minimumPrice = params.get(UriMappingConstants.CORPORATE_ACTION_OPTION_MINIMUM_PRICE);
        }
        return minimumPrice;
    }

    private List<String> getBuyBacksSummary(String summary, String units, String optionIds) {
        List<String> optionDisplayString = new ArrayList<>();
        List<String> summaryList = new ArrayList<>();
        List<String> unitsList = new ArrayList<>();
        List<String> optionIdsArray = new ArrayList<>();

        for (String optionId : optionIds.split(",")) {
            optionIdsArray.add(optionId);
        }

        for (String optionSummary : summary.split(",")) {
            summaryList.add(optionSummary);
        }

        for (String optionSummary : units.split(",")) {
            unitsList.add(optionSummary);
        }

        for (int i = 0; i < optionIdsArray.size(); i++) {
            optionDisplayString.add(summaryList.get(i) + " - " + unitsList.get(i) + " units ");
        }

        return optionDisplayString;
    }

    private String getExerciseRightsSummary(int optionId, String summary, String units, String oversubscribe) {
        String summaryString;
        BigDecimal caPrice = corporateActionDetailsBaseDto.getCorporateActionPrice() != null ? corporateActionDetailsBaseDto
                .getCorporateActionPrice() : BigDecimal.ZERO;

        if (optionId == CorporateActionExerciseRightsType.FULL.getId()) {
            if (!"0".equals(oversubscribe)) {
                BigDecimal totalUnits = new BigDecimal(units).add(new BigDecimal(oversubscribe));
                BigDecimal totalPrice = caPrice.multiply(totalUnits);
                summaryString = summary + " - " + totalUnits + " unit(s) @ $" + caPrice + " = $" + totalPrice + "  Additional new shares"
                        + " " + oversubscribe + " unit(s)";
            } else {
                BigDecimal totalUnits = new BigDecimal(units);
                BigDecimal totalPrice = caPrice.multiply(totalUnits);
                summaryString = summary + " - " + units + " unit(s) @ $" + caPrice + " = $" + totalPrice;

                if (corporateActionDetailsBaseDto.getOversubscribe().equals(Boolean.TRUE)) {
                    summaryString += "  Additional new shares 0 unit";
                }
            }
        } else if (optionId == CorporateActionExerciseRightsType.PARTIAL.getId()) {
            BigDecimal totalUnits = new BigDecimal(units);
            BigDecimal totalPrice = caPrice.multiply(totalUnits);
            summaryString = summary + " - " + units + " unit(s) @ $" + caPrice + " = $" + totalPrice;
        } else {
            summaryString = summary;
        }

        return summaryString;
    }
}
