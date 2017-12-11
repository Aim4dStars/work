package com.bt.nextgen.reports.corporateaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBuyBackAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionExerciseRightsAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionDetailsDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportInitializer;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;

@Report("corporateActionElectionsReportCsv")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@PreAuthorize("isAuthenticated() and (hasPermission(null, 'View_security_events') OR hasPermission(null, 'View_intermediary_reports') OR " +
        "hasPermission(null, 'View_model_portfolios') OR @corporateActionPermissions.checkPermissionForUser())")
public class CorporateActionElectionsReportCsv {
    private static final String SPACE = " ";
    private static final String DASH = " - ";

    @Autowired
    private CorporateActionDetailsDtoService corporateActionDetailsDtoService;

    @Autowired
    private CorporateActionElectionCsvHelper corporateActionElectionCsvHelper;

    private ServiceErrors serviceErrors = new FailFastErrorsImpl();

    private CorporateActionDetailsBaseDto corporateActionDetailsBaseDto;

    @ReportInitializer
    public void init(Map<String, String> params) {
        String corporateActionIdString = params.get(UriMappingConstants.CORPORATE_ACTION_ID_URI_MAPPING);
        final CorporateActionDtoKey key = new CorporateActionDtoKey(EncodedString.toPlainText(corporateActionIdString));
        corporateActionDetailsBaseDto = corporateActionDetailsDtoService.find(key, serviceErrors);
    }

    @ReportBean("CorporateActionDetails")
    @SuppressWarnings("squid:S1172")
    public List<CorporateActionDetailsBaseDto> getCorporateActionDetails(Map<String, String> params) {
        List<CorporateActionDetailsBaseDto> corporateActionDetailsBaseDtoList = new ArrayList<>();
        corporateActionDetailsBaseDtoList.add(corporateActionDetailsBaseDto);
        return corporateActionDetailsBaseDtoList;
    }

    @ReportBean("CorporateActionAccountDetails")
    @SuppressWarnings("squid:S1172")
    public List<CorporateActionAccountDetailsDto> getCorporateActionAccountsList(Map<String, String> params) {
        return ((CorporateActionDetailsDto) corporateActionDetailsBaseDto).getAccounts();
    }

    @ReportBean("CorporateActionOptionMap")
    @SuppressWarnings("squid:S1172")
    public Map<CorporateActionAccountElectionsDto, String> getCorporateActionSubmittedOption(Map<String, String> params) {
        Map<CorporateActionAccountElectionsDto, String> optionMap = new LinkedHashMap<>();
        String optionDisplayString;

        for (CorporateActionAccountDetailsDto account : ((CorporateActionDetailsDto) corporateActionDetailsBaseDto).getAccounts()) {
            CorporateActionAccountElectionsDto corporateActionAccountElectionsDto = account.getSubmittedElections() != null ? account
                    .getSubmittedElections() : account.getSavedElections();

            if (corporateActionAccountElectionsDto != null) {
                if (corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.BUY_BACK.name())) {
                    optionMap.put(corporateActionAccountElectionsDto, getBuyBackTitleSummary(corporateActionAccountElectionsDto));
                } else {
                    CorporateActionOptionDto optionDto = selectFirst(corporateActionDetailsBaseDto.getOptions(),
                            having(on(CorporateActionOptionDto.class).getId(),
                                    equalTo(corporateActionAccountElectionsDto.getPrimaryAccountElection().getOptionId())));

                    if (corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.PRO_RATA_PRIORITY_OFFER.name())
                            || corporateActionDetailsBaseDto.getCorporateActionType().equals(CorporateActionType.EXERCISE_RIGHTS.name())
                            || corporateActionDetailsBaseDto.getCorporateActionType()
                                                            .equals(CorporateActionType.EXERCISE_CALL_OPTION.name())
                            || corporateActionDetailsBaseDto.getCorporateActionType()
                                                            .equals(CorporateActionType.EXERCISE_RIGHTS_WITH_OPT.name())) {
                        optionDisplayString = getRightsExerciseTitleSummary(corporateActionAccountElectionsDto, optionDto, account);
                    } else {
                        optionDisplayString = optionDto.getTitle() + SPACE + optionDto.getSummary();
                    }

                    optionMap.put(corporateActionAccountElectionsDto, optionDisplayString);
                }
            }
        }
        return optionMap;
    }

    @ReportBean("CorporateActionAdviceFeeMap")
    @SuppressWarnings("squid:S1172")
    public Map<String, BigDecimal> getOngoingAdviceFeeMap(Map<String, String> params) {
        Map<String, BigDecimal> ongoingAdviceFeeMap = new LinkedHashMap<>();
        for (CorporateActionAccountDetailsDto account : ((CorporateActionDetailsDto) corporateActionDetailsBaseDto).getAccounts()) {
            ongoingAdviceFeeMap = corporateActionElectionCsvHelper.getOngoingAdviceFee(account);
        }
        return ongoingAdviceFeeMap;
    }

    private String getRightsExerciseTitleSummary(CorporateActionAccountElectionsDto corporateActionAccountElectionsDto,
                                                 CorporateActionOptionDto optionDto, CorporateActionAccountDetailsDto account) {
        String optionDisplayString;

        BigDecimal caPrice = corporateActionDetailsBaseDto.getCorporateActionPrice() != null ? corporateActionDetailsBaseDto
                .getCorporateActionPrice() : BigDecimal.ZERO;

        CorporateActionExerciseRightsAccountElectionDtoImpl electionDto = (CorporateActionExerciseRightsAccountElectionDtoImpl)
                corporateActionAccountElectionsDto.getPrimaryAccountElection();

        if (electionDto.getOptionId().equals(CorporateActionExerciseRightsType.FULL.getId())) {
            if (electionDto.getOversubscribe() != null) {
                BigDecimal totalUnits = BigDecimal.valueOf(account.getHolding()).add(electionDto.getOversubscribe());
                BigDecimal totalPrice = caPrice.multiply(totalUnits);
                optionDisplayString = optionDto.getTitle() + SPACE + optionDto.getSummary() + DASH + totalUnits + " unit(s) @ $" +
                        caPrice + " = $" + totalPrice + (corporateActionDetailsBaseDto.getOversubscribe().equals(Boolean.TRUE) ? " " +
                        "Additional new shares " + electionDto.getOversubscribe() + " unit(s)" : "");
            } else {
                BigDecimal totalPrice = caPrice.multiply(BigDecimal.valueOf(account.getHolding()));
                optionDisplayString = optionDto.getTitle() + SPACE + optionDto.getSummary() + DASH + account.getHolding() + " unit(s)" +
                        " @ $" + caPrice + " = $" + totalPrice + (corporateActionDetailsBaseDto.getOversubscribe().equals(Boolean.TRUE) ?
                                                                  " Additional new shares 0 unit" : "");
            }
        } else if (electionDto.getOptionId().equals(CorporateActionExerciseRightsType.PARTIAL.getId()) && electionDto.getUnits() != null) {
            BigDecimal totalPrice = caPrice.multiply(electionDto.getUnits());
            optionDisplayString = optionDto.getTitle() + SPACE + optionDto.getSummary() + DASH + electionDto.getUnits() + " unit(s) @" +
                    " $" + caPrice + " = $" + totalPrice;
        } else {
            optionDisplayString = optionDto.getTitle() + SPACE + optionDto.getSummary();
        }

        return optionDisplayString;
    }

    private String getBuyBackTitleSummary(CorporateActionAccountElectionsDto corporateActionAccountElectionsDto) {
        StringBuilder optionDisplayString = new StringBuilder();

        for (CorporateActionAccountElectionDto corporateActionAccountElectionDtos : corporateActionAccountElectionsDto.getOptions()) {
            CorporateActionOptionDto optionDto = selectFirst(corporateActionDetailsBaseDto.getOptions(),
                    having(on(CorporateActionOptionDto.class).getId(), equalTo(corporateActionAccountElectionDtos.getOptionId())));

            CorporateActionBuyBackAccountElectionDtoImpl electionDto = (CorporateActionBuyBackAccountElectionDtoImpl)
                    corporateActionAccountElectionDtos;

            optionDisplayString.append(optionDto.getTitle() + SPACE + optionDto.getSummary() + (electionDto.getUnits() != null ? DASH +
                    electionDto.getUnits() + " unit(s)" : "")).append(";");
        }

        return optionDisplayString.toString();
    }
}
