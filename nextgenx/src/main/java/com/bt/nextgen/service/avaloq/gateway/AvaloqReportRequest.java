package com.bt.nextgen.service.avaloq.gateway;

import com.avaloq.abs.bb.fld_def.BaseFld;
import com.avaloq.abs.bb.fld_def.DateFld;
import com.avaloq.abs.bb.fld_def.DateTimeFld;
import com.avaloq.abs.bb.fld_def.ExtlIdVal;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.core.tracking.TrackingReference;
import com.bt.nextgen.core.tracking.TrackingReferenceLocator;
import com.bt.nextgen.core.util.SearchResultsUtil;
import com.bt.nextgen.core.web.model.Criterion;
import com.bt.nextgen.core.web.model.SearchCriteria;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.reports.service.ReportGenerationServiceImpl;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.Template;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.reportservice.reportrequest.v1_0.Exec;
import com.btfin.abs.reportservice.reportrequest.v1_0.Fmt;
import com.btfin.abs.reportservice.reportrequest.v1_0.Mode;
import com.btfin.abs.reportservice.reportrequest.v1_0.Param;
import com.btfin.abs.reportservice.reportrequest.v1_0.ParamList;
import com.btfin.abs.reportservice.reportrequest.v1_0.RepReq;
import com.btfin.abs.reportservice.reportrequest.v1_0.Res;
import com.btfin.abs.reportservice.reportrequest.v1_0.Task;
import com.btfin.abs.reportservice.reportrequest.v1_0.ValList;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Builder class implementation to help manage requests to Avaloq Eventually
 * Avaloq Utils will be replaced by this class
 *
 * @author Andrew Barker
 */
public class AvaloqReportRequest {
    private static Logger logger = LoggerFactory.getLogger(AvaloqReportRequest.class);

    private boolean applicationLevelRequest = false;
    private final RepReq requestObject;
    private final String templateName;
    private static final AvaloqOperation operation = AvaloqOperation.REP_REQ;
    private static final EventType eventType = EventType.STARTUP;

    private static com.btfin.abs.reportservice.reportrequest.v1_0.ObjectFactory reportFactory = AvaloqObjectFactory
            .getReportObjectFactory();
    private static com.btfin.abs.common.v1_0.ObjectFactory commonFactory = AvaloqObjectFactory.getCommonobjectfactory();
    private static com.avaloq.abs.bb.fld_def.ObjectFactory flddefFactory = AvaloqObjectFactory.getFlddefobjectfactory();

    public AvaloqReportRequest(String templateName) {
        this.templateName = templateName;
        this.requestObject = reportFactory.createRepReq();

        Task task = reportFactory.createTask();
        task.setTempl(templateName);
        task.setFmt(Fmt.XML_SPECIFIC);
        Hdr hdr = commonFactory.createHdr();
        final TrackingReference trackingRef = TrackingReferenceLocator.locate();
        hdr.setReqId(trackingRef.getTransactionReferenceAndIncrement());
        requestObject.setHdr(hdr);
        requestObject.setTask(task);
    }

    public AvaloqReportRequest(Template template) {
        this(template.getName());
    }

    protected ParamList getParamList() {
        if (this.getRequestObject().getTask().getParamList() == null)
            this.getRequestObject().getTask().setParamList(reportFactory.createParamList());
        return this.getRequestObject().getTask().getParamList();
    }

    protected static BaseFld createQueryFor(String valType, String value) {
        if (AvaloqUtils.VAL_TEXTVAL.equalsIgnoreCase(valType)) {
            TextFld textFld = flddefFactory.createTextFld();
            textFld.setVal(cleanString(value));
            return textFld;
        } else if (AvaloqUtils.VAL_DATEVAL.equalsIgnoreCase(valType)) {
            DateTime date = DateTime.parse(value);
            DateFld dateFld = AvaloqGatewayUtil.createDateVal(date.toDate());
            return dateFld;
        } else if (AvaloqUtils.VAL_DATETIMEVAL.equalsIgnoreCase(valType)) {
            DateTime date = DateTime.parse(value);
            DateTimeFld dateTimeFld = AvaloqGatewayUtil.createDateTimeVal(date);
            return dateTimeFld;
        } else {
            IdFld idFld = flddefFactory.createIdFld();
            idFld.setVal(cleanString(value));
            return idFld;
        }
    }

    protected static BaseFld createQueryFor(DateTime value) {
        DateFld dateFld = AvaloqGatewayUtil.createDateVal(value.toDate());
        return dateFld;
    }

    protected static ValList createQueryFor(String valType, String... values) {
        ValList valList = reportFactory.createValList();
        for (String value : values) {
            BaseFld base = null;
            if (AvaloqUtils.VAL_TEXTVAL.equalsIgnoreCase(valType)) {
                TextFld textFld = flddefFactory.createTextFld();
                textFld.setVal(cleanString(value));
                base = textFld;
            } else if (AvaloqUtils.VAL_DATEVAL.equalsIgnoreCase(valType)) {
                DateTime date = DateTime.parse(value);
                DateFld dateFld = AvaloqGatewayUtil.createDateVal(date.toDate());
                base = dateFld;
            } else {
                IdFld idFld = flddefFactory.createIdFld();
                idFld.setVal(cleanString(value));
                base = idFld;
            }

            valList.getVal().add(base);
        }
        return valList;
    }

    protected static ValList createQueryForId(String valType, String... values) {
        ValList valList = reportFactory.createValList();
        for (String value : values) {
            IdFld idFld = flddefFactory.createIdFld();
            idFld.setVal(cleanString(value));
            valList.getVal().add(idFld);
        }
        return valList;
    }

    protected static ValList createQueryForListOfValues(String valType, List<String> values) {
        ValList valList = reportFactory.createValList();

        for (String value : values) {
            logger.info("Adding Value {}", value);
            IdFld idFld = flddefFactory.createIdFld();
            ExtlIdVal val = flddefFactory.createExtlIdVal();
            val.setKey(valType);
            val.setVal(cleanString(value));
            idFld.setExtlVal(val);
            valList.getVal().add(idFld);
        }
        return valList;
    }

    protected static Param createQueryParameter(String queryParameterName) {
        Param param = reportFactory.createParam();
        param.setName(queryParameterName);
        return param;
    }

    public AvaloqReportRequest formSearchQuery(String searchParamVal) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_SEARCH_KEY);
        parameter.setVal(createQueryFor(AvaloqUtils.VAL_TEXTVAL, searchParamVal));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forCISKey(String cisKey) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_PERSON_LIST);
        parameter.setValList(createQueryForListOfValues(AvaloqUtils.PARAM_CIS_ID, Arrays.asList(cisKey)));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest formPersonID(String... personId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_PERSON);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, personId));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forIncludeAccount(String... accountId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INCLUDE_ACCOUNT_ID);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, accountId));
        this.getParamList().getParam().add(parameter);
        return this;

    }

    public AvaloqReportRequest forContId(String contId) {
        Param parameter = createQueryParameter(AvaloqUtils.CONT_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, contId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest emulatingUser(String userId) {
        // TODO:CASH05MERGE where should this be going?
        // this.getRequestObject().getTask().setEmulatedUser(userId);
        return this;
    }

    private Mode getModeForCurrentReport() {
        Mode mode = this.getRequestObject().getMode();
        if (mode == null) {
            mode = reportFactory.createMode();
            this.getRequestObject().setMode(mode);
        }
        return mode;

    }

    public AvaloqReportRequest forJMSResponse() {
        Mode mode = getModeForCurrentReport();
        mode.setRes(Res.ASYNC);

        return this;
    }

    public AvaloqReportRequest compressResult() {
        Mode mode = getModeForCurrentReport();
        mode.setCompress(true);
        return this;
    }

    public AvaloqReportRequest processAsynchronously() {
        Mode mode = getModeForCurrentReport();
        mode.setExec(Exec.ASYNC);
        return this;
    }

    public AvaloqReportRequest forChunkedResponse(Integer maxChunkSize) {
        Mode mode = getModeForCurrentReport();
        mode.setMaxChunkSz(maxChunkSize);
        return this;
    }

    public AvaloqReportRequest forAccount(String accountId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_ACCOUNT_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, accountId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forScheduledTransactionAccount(String accountId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INCLUDE_ACCOUNT_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, accountId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forBenchmark(String benchmarkId) {
        Param parameter = createQueryParameter(AvaloqUtils.BENCHMARK_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, benchmarkId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forAdviserOeId(String oeId) {
        Param parameter = createQueryParameter(AvaloqUtils.ADVISER_OE_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, oeId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forOeIds(List<String> oeIds) {
        Param parameter = createQueryParameter(AvaloqUtils.OE_LIST_ID);
        ValList valList = reportFactory.createValList();
        for (String oeId : oeIds) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, oeId));
        }

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forF1OeIds(List<String> oeIds) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_OE_LIST_FI_ID);
        ValList valList = reportFactory.createValList();
        if (oeIds.size() > 1) {
            for (String oeId : oeIds) {
                valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID_FIELD, oeId));
            }
            parameter.setValList(valList);
        } else {
            parameter.setVal(createQueryFor(AvaloqUtils.PARAM_ID_FIELD, oeIds.get(0)));
        }

        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forProfileIds(List<String> profileIds) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_JOB_PROFILE_USER);
        ValList valList = reportFactory.createValList();
        for (String profileId : profileIds) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, profileId));
        }
        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest fromCreationTimestamp(String creationTimestampFrom) {
        Param paramDate = createQueryParameter(AvaloqUtils.CREATION_TIMESTAMP_FROM);
        paramDate.setVal(createQueryFor(AvaloqUtils.VAL_DATETIMEVAL, creationTimestampFrom));
        this.getParamList().getParam().add(paramDate);

        return this;
    }

    public AvaloqReportRequest toCreationTimestamp(String creationTimestampTo) {
        Param paramDate = createQueryParameter(AvaloqUtils.CREATION_TIMESTAMP_TO);
        paramDate.setVal(createQueryFor(AvaloqUtils.VAL_DATETIMEVAL, creationTimestampTo));
        this.getParamList().getParam().add(paramDate);

        return this;
    }

    public AvaloqReportRequest forAccountList(List<String> accountIds) {
        return this.forAccountList(AvaloqUtils.PARAM_ACCOUNT_ID, accountIds);
    }

    public AvaloqReportRequest forIncludeAccountList(List<String> accountIds) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INCLUDE_ACCOUNT_ID);
        ValList valList = reportFactory.createValList();
        for (String accountId : accountIds) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, accountId));
        }
        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forAccountList(String paramName, List<String> accountIds) {
        Param parameter = createQueryParameter(paramName);
        ValList valList = reportFactory.createValList();
        for (String accountId : accountIds) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, accountId));
        }
        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forInvestmentManager(String managerId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INVESTMENT_MANAGER_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, managerId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forProductList(List<String> productIds) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_PRODUCT_LIST);
        ValList valList = reportFactory.createValList();
        for (String productId : productIds) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, productId));
        }
        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forIncludeProductList(List<String> productIds) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INCLUDE_PRODUCT_ID);
        ValList valList = reportFactory.createValList();
        for (String productId : productIds) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, productId));
        }
        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forEffectiveDate(DateTime effectiveDate) {
        Param paramDate = createQueryParameter(ReportGenerationServiceImpl.PARAM_NAME_EFFECTIVE_DATE);
        paramDate.setVal(createQueryFor(effectiveDate));
        this.getParamList().getParam().add(paramDate);
        return this;
    }

    public AvaloqReportRequest fromDate(String fromDate) {
        Param paramDate = createQueryParameter(Constants.PARAM_NAME_START_DATE);
        paramDate.setVal(createQueryFor(Constants.PARAM_TYPE_VAL_DATE, fromDate));
        this.getParamList().getParam().add(paramDate);

        return this;
    }

    public AvaloqReportRequest toDate(String toDate) {
        Param paramDate = createQueryParameter(Constants.PARAM_NAME_END_DATE);
        paramDate.setVal(createQueryFor(Constants.PARAM_TYPE_VAL_DATE, toDate));
        this.getParamList().getParam().add(paramDate);

        return this;
    }

    public AvaloqReportRequest forDateTime(String paramName, DateTime dateValue) {
        Param paramDate = createQueryParameter(paramName);
        paramDate.setVal(createQueryFor(dateValue));
        this.getParamList().getParam().add(paramDate);
        return this;
    }

    public AvaloqReportRequest forDateTimeOptional(String paramName, DateTime dateValue) {
        if (dateValue != null) {
            this.forDateTime(paramName, dateValue);
        }
        return this;
    }

    // Added for the Client Identifier in Term Deposit Maturity Status Report

    public AvaloqReportRequest forClientTermDeposit(String client) {
        Param parameter = createQueryParameter(ReportGenerationServiceImpl.PARAM_NAME_PORTFOLIO_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, client));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forTransactionCategory(String categoryId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_CATEGORY_ID);
        parameter.setValList(createQueryForId(AvaloqUtils.PARAM_ID, categoryId));
        this.getParamList().getParam().add(parameter);

        return this;
    }

    public AvaloqReportRequest asApplicationUser() {
        this.applicationLevelRequest = true;
        return this;
    }

    public AvaloqReportRequest searchingFor(List<Criterion> criteria) {
        Param paramNameMatch;

        for (Criterion criterion : criteria) {
            paramNameMatch = createQueryParameter(criterion.getName());
            if (criterion.isSingleValue()) {
                // The search param with one single value
                if (!CollectionUtils.isEmpty(criterion.getValue())) {
                    BaseFld value = createQueryFor(AvaloqUtils.PARAM_TYPE_TEXT_FIELD, criterion.getValue().get(0));
                    paramNameMatch.setVal(value);
                    this.getParamList().getParam().add(paramNameMatch);
                }

            } else if (!criterion.isSingleValue()) {
                // The search param with a list<String> as value
                ValList valList = createQueryForListOfValues(criterion.getName(), criterion.getValue());
                paramNameMatch.setValList(valList);
                this.getParamList().getParam().add(paramNameMatch);

            }
        }
        return this;
    }

    /**
     * method used when creating report request with search parameters
     *
     * @param searchParameters
     */
    public AvaloqReportRequest(SearchParameters searchParameters) {
        Param paramNameMatch = null;
        this.templateName = searchParameters.getSearchFor().getName();
        this.requestObject = reportFactory.createRepReq();
        Hdr hdr = commonFactory.createHdr();
        hdr.setReqId(UUID.randomUUID().toString());
        requestObject.setHdr(hdr);

        Task task = reportFactory.createTask();
        task.setTempl(templateName);
        task.setFmt(Fmt.XML_SPECIFIC);
        requestObject.setTask(task);
        if (searchParameters.getSearchCriterias() != null) {
            for (SearchCriteria searchCriteria : searchParameters.getSearchCriterias()) {
                if (!(searchCriteria.getSearchKey().is(SearchParams.UI_SEARCH_PARAMS))) {
                    paramNameMatch = createQueryParameter(SearchResultsUtil.getParamName(searchParameters.getSearchFor()
                            .toString(), searchCriteria.getSearchKey().toString()));
                    paramNameMatch.setVal(createQueryFor(SearchResultsUtil.getParamType(searchParameters.getSearchFor()
                            .toString(), searchCriteria.getSearchKey().toString()), searchCriteria.getSearchValue()));
                    this.getParamList().getParam().add(paramNameMatch);
                }
            }
        }
    }

    public boolean isApplicationLevelRequest() {
        return applicationLevelRequest;
    }

    public RepReq getRequestObject() {
        return requestObject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public AvaloqOperation getOperation() {
        return operation;
    }

    public AvaloqReportRequest forDocumentIdList(List<String> docIds) {
        String[] docList = docIds.toArray(new String[docIds.size()]);
        Param parameter = createQueryParameter(AvaloqUtils.DOCUMENT_ID_LIST);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, docList));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forExternalRefId(List<String> docIds) {
        String[] docList = docIds.toArray(new String[docIds.size()]);
        Param parameter = createQueryParameter(AvaloqUtils.EXTERNAL_REFERENCE_NR);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, docList));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forAssets(Collection<String> assetIds) {
        ValList valList = createQueryFor(AvaloqUtils.VAL_TEXTVAL, assetIds.toArray(new String[assetIds.size()]));
        Param paramAssetId = createQueryParameter(AvaloqUtils.PARAM_ASSET_LIST_ID);
        paramAssetId.setValList(valList);
        this.getParamList().getParam().add(paramAssetId);
        return this;
    }

    public AvaloqReportRequest forAssetIds(Collection<String> assetIds) {
        ValList valList = createQueryFor(AvaloqUtils.VAL_TEXTVAL, assetIds.toArray(new String[assetIds.size()]));
        Param paramAssetId = createQueryParameter(AvaloqUtils.PARAM_ASSET_ID);
        paramAssetId.setValList(valList);
        this.getParamList().getParam().add(paramAssetId);
        return this;
    }

    public AvaloqReportRequest forAssetsOptional(Collection<String> assetIds) {
        if (assetIds != null)
            this.forAssets(assetIds);
        return this;
    }

    // TODO : Check the paramList as in cash Param used to take Val as input but
    // it has now been moved to ValList
    public AvaloqReportRequest forAvokaAppNum(List<String> avokaApplicationNum) {
        String[] avokaList = avokaApplicationNum.toArray(new String[avokaApplicationNum.size()]);
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_AVOKA_APP_NO);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, avokaList));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forPastTransactions(String portfolioId, String dateFrom, String dateTo) {
        String[] portfolio = {portfolioId};
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_ACCOUNT_ID);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, portfolio));
        this.getParamList().getParam().add(parameter);

        if (StringUtils.isNotEmpty(dateFrom)) {
            String[] dateFromTransaction = {dateFrom};
            Param parameterDateFrom = createQueryParameter(AvaloqUtils.PARAM_VAL_DATE_FROM);
            parameterDateFrom.setValList(createQueryFor(AvaloqUtils.VAL_DATEVAL, dateFromTransaction));
            this.getParamList().getParam().add(parameterDateFrom);
        }

        if (StringUtils.isNotEmpty(dateTo)) {
            String[] dateToTransaction = {dateTo};
            Param parameterDateTo = createQueryParameter(AvaloqUtils.PARAM_VAL_DATE_TO);
            parameterDateTo.setValList(createQueryFor(AvaloqUtils.VAL_DATEVAL, dateToTransaction));
            this.getParamList().getParam().add(parameterDateTo);
        }

        return this;
    }

    public AvaloqReportRequest forBpList(List<String> bpList) {
        String[] bpArray = bpList.toArray(new String[bpList.size()]);
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_BP_LIST);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, bpArray));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forBpIdList(List<String> bpIdList) {
        String[] bpArray = bpIdList.toArray(new String[bpIdList.size()]);
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_BP_ID_LIST);
        parameter.setValList(createQueryFor(AvaloqUtils.PARAM_ID, bpArray));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forBpNrList(List<String> bpNrList) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_BP_LIST);
        parameter.setValList(createQueryForListOfValues(AvaloqUtils.PARAM_BP_NR, bpNrList));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forBpListId(List<String> bpNrList) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_ACCOUNT_ID);
        parameter.setValList(createQueryForListOfValues(AvaloqUtils.PARAM_BP_NR, bpNrList));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forCustomerList(List<String> cisKeyList) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_PERSON_LIST_ID);
        parameter.setValList(createQueryForListOfValues(AvaloqUtils.PARAM_CIS_ID, cisKeyList));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forBpNrListVal(List<String> bpNrList) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_BP_LIST);
        parameter.setVal(AvaloqGatewayUtil.createNumberVal(bpNrList.get(0)));
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forDocId(String docId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_DOC_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, docId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forRefDocListId(List<String> docIdList) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_REF_DOC_LIST_ID);
        ValList valList = reportFactory.createValList();

        for (String docId : docIdList) {
            valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, docId));
        }

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forFinancialYear(Date date) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_FINANCIAL_YEAR);
        parameter.setVal(AvaloqGatewayUtil.createDateVal(date));

        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forAccountListId(String accountId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_ACCOUNT_LIST_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, accountId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forInvestmentManagerOeId(String managerId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INVESTMENT_MANAGER_OE_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, managerId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    public AvaloqReportRequest forInvestmentPolicyStatementId(String ipsId) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_INVESTMENT_POLICY_STATEMENT_ID);
        ValList valList = reportFactory.createValList();
        valList.getVal().add(createQueryFor(AvaloqUtils.PARAM_ID, ipsId));

        parameter.setValList(valList);
        this.getParamList().getParam().add(parameter);
        return this;
    }

    private static String cleanString(String value) {
        // Jaxb does not quote ]]>, ISC want us to not die when that happens.
        if (value != null && value instanceof String) {
            return value.replaceAll("]]>", "]]&gt;");
        }
        return value;
    }

    public AvaloqReportRequest forOrderTypeList(List<String> orderTypes) {
        Param parameter = createQueryParameter(AvaloqUtils.PARAM_ORDER_TYPE_LIST_ID);
        parameter.setValList(createQueryForListOfValues("", orderTypes));
        this.getParamList().getParam().add(parameter);
        return this;
    }
}
