package com.bt.nextgen.reports.account.transactions;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.TransactionHistoryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Report("transactionHistoryReportV2")
public class TransactionHistoryReport extends AccountReportV2 {
    private static final String DISCLAIMER_CONTENT = "DS-IP-0029";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";
    private static final String REPORT_TYPE = "Transaction history";
    private static final String ACCOUNT_ID_URI_MAPPING = "account-id";
    private static final String START_DATE_PARAMETER_MAPPING = "start-date";
    private static final String END_DATE_PARAMETER_MAPPING = "end-date";
    private static final String ASSET_CODE_PARAMETER_MAPPING = "asset-code";
    private static final String ASSET_CODE = "assetCode";
    private static final String ACCOUNT_ID = "accountId";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    @Autowired
    private TransactionHistoryDtoService transactionService;

    @Autowired
    private UserProfileService userProfileService;


    @Autowired
    private OptionsService optionsService;


    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @ReportBean("startDate")
    public String getStartDate(Map<String, Object> params) {
        String startDate = (String) params.get(START_DATE_PARAMETER_MAPPING);
        if (startDate == null) {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime());
        } else {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(startDate));
        }
    }

    @ReportBean("endDate")
    public String getEndDate(Map<String, Object> params) {
        String endDate = (String) params.get(END_DATE_PARAMETER_MAPPING);
        if (endDate == null) {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime());
        } else {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(endDate));
        }
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        List<ApiSearchCriteria> criteria = getApiSearchCriteria(params);
        String queryString = (String) params.get(BeanFilter.QUERY_PARAMETER);
        String sortString = (String) params.get(Sort.SORT_PARAMETER);
        ApiResponse response = new Sort<>(new BeanFilter(ApiVersion.CURRENT_VERSION, new SearchByCriteria<>(
                ApiVersion.CURRENT_VERSION, transactionService, criteria), queryString), sortString).performOperation();
        ResultListDto<TransactionHistoryDto> resultList = (ResultListDto<TransactionHistoryDto>) response.getData();
        List<TransactionHistoryDto> transactions = resultList.getResultList();
        List<TransactionData> transactionDatas = getTransactionData(transactions);
        return Collections.singletonList(transactionDatas);
    }

    public List<TransactionData> getTransactionData(List<TransactionHistoryDto> transactions) {
        List<TransactionData> transactionDatas = new ArrayList<>();
        List<TransactionHistoryDto> buffer = new ArrayList<>();

        for (TransactionHistoryDto transaction : transactions) {
            buffer.add(transaction);
            if (!transaction.getIsLink()) {
                if (buffer.size() == 1) {
                    transactionDatas.add(new TransactionData(transaction));
                } else {
                    transactionDatas.add(new TransactionData(buffer));
                }
                buffer = new ArrayList<>();
            }
        }
        return transactionDatas;
    }

    @ReportBean("moreInformation")
    public String getMoreInfo(Map<String, Object> params, Map<String, Object> dataCollections) {
        final UserExperience userExperience = getUserExperience(params, dataCollections);
        if (UserExperience.DIRECT.equals(userExperience)) {
            return null;
        }
        String adviserName = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            Broker adviser = getAdviser(getAccountKey(params), params, params);
            BrokerUser adviserUser = brokerIntegrationService.getAdviserBrokerUser(adviser.getKey(), new FailFastErrorsImpl());
            adviserName = adviserUser.getFullName();
        }

        return getContent(INFORMATION_CONTENT, new String[] { adviserName });
    }



    @ReportBean("disclaimer")
    public String getDisclaimer() {
        String key = DISCLAIMER_CONTENT;
        return getContent(key, null);
    }

    private List<ApiSearchCriteria> getApiSearchCriteria(Map<String, Object> params) {
        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        ApiSearchCriteria portfolioCriteria = null;
        ApiSearchCriteria assetCriteria = null;
        String assetCode = (String) params.get(ASSET_CODE_PARAMETER_MAPPING);
        portfolioCriteria = new ApiSearchCriteria(ACCOUNT_ID, SearchOperation.EQUALS,
                (String) params.get(ACCOUNT_ID_URI_MAPPING), OperationType.STRING);
        criteria.add(portfolioCriteria);

        if (assetCode != null && !assetCode.equals(Constants.EMPTY_STRING)) {
            assetCriteria = new ApiSearchCriteria(ASSET_CODE, SearchOperation.EQUALS, assetCode, OperationType.STRING);
            criteria.add(assetCriteria);
        }
        ApiSearchCriteria startDateCriteria = null;
        String startDate = (String) params.get(START_DATE_PARAMETER_MAPPING);
        startDateCriteria = new ApiSearchCriteria(START_DATE, SearchOperation.EQUALS, startDate, OperationType.DATE);
        criteria.add(startDateCriteria);
        ApiSearchCriteria endDateCriteria = null;
        String endDate = (String) params.get(END_DATE_PARAMETER_MAPPING);
        endDateCriteria = new ApiSearchCriteria(END_DATE, SearchOperation.EQUALS, endDate, OperationType.DATE);
        criteria.add(endDateCriteria);
        return criteria;
    }

    @Override
    public boolean getBsbAccount(Map<String, Object> params){
        String accountId = (String) params.get(ACCOUNT_ID_URI_MAPPING);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(accountId));
        Boolean displayBsbAccountNumber = optionsService.hasFeature(OptionKey.valueOf(OptionNames.REPORT_ACCOUNT_BANKACCOUNT),
                accountKey, serviceErrors);
        return displayBsbAccountNumber;
    }

    @ReportBean("reportTitle")
    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TYPE;
    }

}
