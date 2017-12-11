package com.bt.nextgen.reports.account.transactions;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

@Report(value = "cashTransactionsReportV2", filename = "Cash transactions")
public class CashTransactionsReport extends AccountReportV2 {
    private static final String REPORT_TYPE = "Cash statement";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0028";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";

    private static final String START_DATE = "start-date";
    private static final String END_DATE = "end-date";
    private static final String ACCOUNT_ID = "account-id";

    @Autowired
    private CashTransactionHistoryDtoService cashTransactionHistoryDtoService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @ReportBean("moreInformation")
    public String getMoreInfo(Map<String, Object> params, Map<String, Object> dataCollections) {
        final UserExperience userExperience = getUserExperience(params, dataCollections);
        if (UserExperience.DIRECT.equals(userExperience)) {
            return null;
        }
        String adviser = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            adviser = getAdviserName(getAccountKey(params), params);
        }
        return getContent(INFORMATION_CONTENT, new String[] { adviser });
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        return getContent(DISCLAIMER_CONTENT);
    }

    @Override
    @ReportBean("reportTitle")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TYPE;
    }

    @ReportBean("reportSubtitle")
    public String getReportSubtitle(Map<String, Object> params) {
        StringBuilder subtitle = new StringBuilder();
        subtitle.append(ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime((String) params.get(START_DATE))));
        subtitle.append(" to ");
        subtitle.append(ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime((String) params.get(END_DATE))));
        return subtitle.toString();
    }


    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = getAccountKey(params).getId();

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText(accountId).toString(), ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.FROM_DATE, ApiSearchCriteria.SearchOperation.EQUALS,
                (String) params.get(START_DATE), ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.TO_DATE, ApiSearchCriteria.SearchOperation.EQUALS,
                (String) params.get(END_DATE), ApiSearchCriteria.OperationType.STRING));

        List<CashTransactionHistoryDto> cashTransactionDtos = cashTransactionHistoryDtoService.search(criteria,
                new FailFastErrorsImpl());

        return Collections
                .singletonList(convert(cashTransactionDtos, new Converter<CashTransactionHistoryDto, CashTransactionsData>() {
                    public CashTransactionsData convert(CashTransactionHistoryDto dto) {
                        return new CashTransactionsData(dto);
                    }
                }));
    }

    @Override
    public boolean getBsbAccount(Map<String, Object> params){
        String accountId = (String) params.get(ACCOUNT_ID);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        Boolean displayBsbAccountNumber = optionsService.hasFeature(OptionKey.valueOf(OptionNames.REPORT_ACCOUNT_BANKACCOUNT),
                accountKey, serviceErrors);
        return displayBsbAccountNumber;
    }

    public String getAdviserName(AccountKey key, Map<String, Object> params) {
        Broker adviser = getAdviser(key, params, params);
        BrokerUser adviserUser = brokerIntegrationService.getAdviserBrokerUser(adviser.getKey(), new FailFastErrorsImpl());
        return adviserUser.getFullName();
    }
}