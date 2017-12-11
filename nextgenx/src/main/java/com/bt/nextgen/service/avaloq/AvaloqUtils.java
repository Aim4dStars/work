package com.bt.nextgen.service.avaloq;

import com.avaloq.abs.bb.fld_def.BoolFld;
import com.avaloq.abs.bb.fld_def.DateTimeFld;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.InitialInvestmentRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.bt.nextgen.service.integration.account.PayeeRequest;
import com.bt.nextgen.service.integration.account.SubscriptionRequest;
import com.bt.nextgen.service.integration.account.UpdatePaymentLimitRequest;
import com.bt.nextgen.service.integration.account.UpdatePrimContactRequest;
import com.bt.nextgen.service.integration.account.UpdateTaxPrefRequest;
import com.bt.nextgen.service.integration.termdeposit.TermDepositAction;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequest;
import com.bt.nextgen.service.integration.uar.UarAction;
import com.bt.nextgen.service.integration.uar.UarRecords;
import com.bt.nextgen.service.integration.uar.UarRequest;
import com.btfin.abs.common.v1_0.Exec;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.common.v1_0.Mode;
import com.btfin.abs.common.v1_0.Res;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.bp.v1_0.AllInitInvstAsset;
import com.btfin.abs.trxservice.bp.v1_0.AllLinkedAcc;
import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import com.btfin.abs.trxservice.bp.v1_0.DelLinkedAcc;
import com.btfin.abs.trxservice.bp.v1_0.SubscrReqElem;
import com.btfin.abs.trxservice.bp.v1_0.TrxLimitElem;
import com.btfin.abs.trxservice.datavalid.v1_0.DataValidReq;
import com.btfin.abs.trxservice.fidd.v1_0.FiddReq;
import com.btfin.abs.trxservice.ntfcn.v1_0.AddNtfcn;
import com.btfin.abs.trxservice.ntfcn.v1_0.NtfcnReq;
import com.btfin.abs.trxservice.reguser.v1_0.ActionType;
import com.btfin.abs.trxservice.reguser.v1_0.RegUserReq;
import com.btfin.abs.trxservice.uar.v1_0.Uar;
import com.btfin.abs.trxservice.uar.v1_0.UarList;
import com.btfin.abs.trxservice.uar.v1_0.UarRec;
import com.btfin.abs.trxservice.uar.v1_0.UarReq;
import com.btfin.abs.trxservice.user.v1_0.UserReq;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.integration.messages.NotificationAddRequest;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.List;

import static com.bt.nextgen.service.AvaloqGatewayUtil.createExtlId;
import static com.bt.nextgen.service.AvaloqGatewayUtil.createExtlIdVal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.createHdr;
import static com.bt.nextgen.service.AvaloqGatewayUtil.createIdVal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.createNumberVal;
import static com.bt.nextgen.service.AvaloqGatewayUtil.createTextVal;
import static com.btfin.panorama.core.util.StringUtil.concatStrings;

public class AvaloqUtils {
    public static final String PARAM_INCLUDE_ACCOUNT_ID = "bp_incl_id"; // For an  collection of accounts
    public static final String PARAM_ACCOUNT_ID = "bp_id"; // For an individual instance of a account
    public static final String PARAM_PERSON_ID = "obj_incl_id";
    public static final String PARAM_DOC_ID = "doc_id";
    public static final String PARAM_INVESTMENT_MANAGER_ID = "im_id";
    public static final String PARAM_PRODUCT_LIST = "ips_list";
    public static final String PARAM_INCLUDE_PRODUCT_ID = "ips_incl_id";
    public static final String PARAM_ID_FIELD = "fldb:IdFld";
    public static final String PARAM_ID = "rep:idVal";
    public static final String PARAM_PERSON_LIST = "person_list";
    public static final String PARAM_PERSON_LIST_ID = "person_list_id";
    public static final String PARAM_AUTH_KEY = "auth_key";
    public static final String PARAM_PERSON_AUTH_KEY = "person_auth_key";
    public static final String PARAM_USER_ID = "btfg$person_gcm_id";
    public static final String PARAM_CIS_ID = "btfg$person_cis_id";
    public static final String PARAM_ASSET_CODE = "btfg$asset_key";
    public static final String PARAM_FIRST_NAME = "first_name";
    public static final String PARAM_TYPE_TEXT_FIELD = "rep:textField";
    public static final String VAL_TEXTVAL = "rep:textVal";
    public static final String VAL_DATEVAL = "rep:dateVal";
    public static final String VAL_DATETIMEVAL = "rep:dateTimeVal";
    public static final String PARAM_ASSET_LIST_ID = "asset_list_id";
    public static final String PARAM_ASSET_ID = "asset_id";
    public static final String PARAM_ASSET_TYPE = "asset_type";
    public static final String PARAM_INVESTMENT_ID = "InvestmentId";
    public static final String UNREAD = "4";
    public static final String PARAM_NOTIFICATION_STATUS = "ntfcn_status_list_id";
    public static final String PARAM_JOB_PROFILE_USER = "resp_sec_user_list_id";
    public static final String CREATION_TIMESTAMP_FROM = "creation_timestamp_from";
    public static final String CREATION_TIMESTAMP_TO = "creation_timestamp_to";
    public static final String DOCUMENT_ID_LIST = "doc_list_id";
    public static final String EXTERNAL_REFERENCE_NR = "extl_ref_nr";
    public static final String ADVISER_OE_ID = "avsr_oe_id";
    public static final String OE_LIST_ID = "oe_list_id";
    public static final String PARAM_PERSON = "person_id";
    public static final String PARAM_AVOKA_APP_NO = "doc_list_id";
    public static final String PARAM_BP_ID_LIST = "bp_list";
    public static final String PARAM_BP_LIST = "bp_list_id";
    public static final String BENCHMARK_ID = "bmrk_id";
    public static final String CONT_ID = "cont_id";
    public static final String PARAM_VAL_DATE_FROM = "val_date_from";
    public static final String PARAM_VAL_DATE_TO = "val_date_to";
    public static final String PARAM_BP_NR = "bp_nr";
    public static final String PARAM_SEARCH_KEY = "srch_key";
    public static final String PARAM_REF_DOC_LIST_ID = "ref_doc_list_id";
    public static final String PARAM_FINANCIAL_YEAR = "fy_date";
    public static final String PARAM_ACCOUNT_LIST_ID = "bp_list_id";
    public static final String PARAM_CATEGORY_ID = "cash_cat_type_id";
    public static final String PARAM_INVESTMENT_MANAGER_OE_ID = "im_oe_id";
    public static final String PARAM_INVESTMENT_POLICY_STATEMENT_ID = "ips_incl_id";
    public static final String PARAM_PRODUCT_SYMBOL = "prod_sym";
    public static final String PARAM_OE_LIST_FI_ID = "incl_f1_list_id";
    public static final String PARAM_ORDER_TYPE_LIST_ID = "order_type_list_id";

    private static final Logger logger = LoggerFactory.getLogger(AvaloqUtils.class);

    public static UarReq makeUarRequest(UarAction uarAction, UarRequest request) {
        UarReq uarReq = AvaloqObjectFactory.getUarObjectFactory().createUarReq();
        uarReq.setHdr(createHdr());
        Req req = null;
        ReqGet reqGet = new ReqGet();
        switch (uarAction) {
            case GET_UAR_LIST:
                req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
                reqGet.setDoc(createIdVal(request.getDocId()));
                req.setGet(reqGet);
                uarReq.setReq(req);
                break;
            case SUBMIT_UAR_LIST:
                req = createTransactionServiceExecuteReq();
                req.getExec().setDoc(createNumberVal(request.getDocId()));
                com.btfin.abs.trxservice.uar.v1_0.Data requestData =  getUarSubmitData(request);
                uarReq.setData(requestData);
                break;
            default:
                break;
        }
        uarReq.setReq(req);
        return uarReq;
    }

    private static com.btfin.abs.trxservice.uar.v1_0.Data getUarSubmitData(UarRequest request){
        com.btfin.abs.trxservice.uar.v1_0.Data requestData = AvaloqObjectFactory.getUarObjectFactory().createData();
        UarList uarList = new UarList();
        UarRec uarDirRec = new UarRec();
        UarRec uarInDirRec = new UarRec();
        List<Uar> dirUars =  uarDirRec.getUarRec();
        List<Uar> inDirUars =  uarInDirRec.getUarRec();
        uarList.setOeId(createIdVal(request.getBrokerId()));
        for(UarRecords uarRecord : request.getUarRecords()){
            Uar uar = new Uar();
            uar.setJobId(createIdVal(uarRecord.getJobId()));
            uar.setOeId(createIdVal(uarRecord.getBrokerId()));
            uar.setPersonId(createIdVal(uarRecord.getPersonId()));
            uar.setIdx(createNumberVal(uarRecord.getRecordIndex()));
            if(uarRecord.getPermissionId()!=null) {
                uar.setJobOeAuthRoleId(createIdVal(uarRecord.getPermissionId()));
            }
            uar.setDecsnId(createIdVal(uarRecord.getDecisionId()));
            if(uarRecord.getRecordType().equals("DIR"))
                dirUars.add(uar);
            else
                inDirUars.add(uar);
        }
        if(!uarDirRec.getUarRec().isEmpty())
            uarList.setDirJobList(uarDirRec);
        if(!uarInDirRec.getUarRec().isEmpty())
            uarList.setDirJobList(uarInDirRec);
        requestData.setUARLists(uarList);
        return requestData;
    }


    public static FiddReq makeFiddRequest(TermDepositAction termDepositAction, TermDepositTrxRequest request) {
        FiddReq fiddReq = AvaloqObjectFactory.getTermDepositObjectFactory().createFiddReq();
        fiddReq.setHdr(createHdr());
        com.btfin.abs.trxservice.fidd.v1_0.Data data = AvaloqObjectFactory.getTermDepositObjectFactory().createData();
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        com.btfin.abs.trxservice.base.v1_0.Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        ReqExec reqExec = new ReqExec();
        ReqValid reqValid = new ReqValid();
        switch (termDepositAction) {
            case VALIDATE_ADD_TERM_DEPOSIT:
                setAddTdValues(data, request);
                action.setGenericAction(Constants.DO);
                reqValid.setAction(action);
                req.setValid(reqValid);
                break;
            case ADD_TERM_DEPOSIT:
                setAddTdValues(data, request);
                action.setGenericAction(Constants.DO);
                reqExec.setAction(action);
                req.setExec(reqExec);
                break;
            case UPDATE_TERM_DEPOSIT:
                data.setPos(createIdVal(request.getAsset()));
                data.setRenwMode(createIdVal(request.getRenewMode()));
                if (!org.h2.util.StringUtils.isNullOrEmpty(request.getPortfolio())) {
                    data.setContrprty(createIdVal(request.getPortfolio()));
                }
                if (!org.h2.util.StringUtils.isNullOrEmpty(request.getRenewAmount())) {
                    data.setRenwAmount(createNumberVal(new BigDecimal(request.getRenewAmount())));
                }
                if (!org.h2.util.StringUtils.isNullOrEmpty(request.getRenewCount())) {
                    data.setRenwNof(createNumberVal(new BigDecimal(request.getRenewCount())));
                }
                action.setGenericAction(Constants.DO);
                reqExec.setAction(action);
                req.setExec(reqExec);
                break;
            case BREAK_TERM_DEPOSIT:
                setBreakTdValues(data, request);
                action.setGenericAction(Constants.CANCEL);
                reqExec.setAction(action);
                req.setExec(reqExec);
                break;
            case VALIDATE_BREAK_TERM_DEPOSIT:
                setBreakTdValues(data, request);
                action.setGenericAction(Constants.CANCEL);
                reqValid.setAction(action);
                req.setValid(reqValid);
                break;
        }
        fiddReq.setData(data);
        fiddReq.setReq(req);
        return fiddReq;
    }

    private static void setAddTdValues(com.btfin.abs.trxservice.fidd.v1_0.Data data, TermDepositTrxRequest request) {
        data.setAsset(createIdVal(request.getAsset()));
        data.setQty(createNumberVal(new BigDecimal(request.getAmount())));
        data.setContrprty(createIdVal(request.getPortfolio()));
        data.setCurry(createIdVal(request.getCurrencyCode()));
    }

    private static void setBreakTdValues(com.btfin.abs.trxservice.fidd.v1_0.Data data, TermDepositTrxRequest request) {
        data.setPos(createIdVal(request.getAsset()));
        data.setContrprty(createIdVal(request.getPortfolio()));
    }

    public static FiddReq makeUpdateFiddRequest(String portfolioId, String tdAccountId, String renewMode) {
        FiddReq fiddReq = AvaloqObjectFactory.getTermDepositObjectFactory().createFiddReq();
        fiddReq.setHdr(createHdr());
        com.btfin.abs.trxservice.fidd.v1_0.Data data = AvaloqObjectFactory.getTermDepositObjectFactory().createData();
        data.setPos(createIdVal(tdAccountId));
        data.setContrprty(createIdVal(portfolioId));
        data.setRenwMode(createIdVal(renewMode));
        fiddReq.setData(data);
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = new ReqExec();
        com.btfin.abs.trxservice.base.v1_0.Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);
        req.setExec(reqExec);
        fiddReq.setReq(req);
        return fiddReq;
    }

    /**
     * Formatter method to format term-deposit rate.
     *
     * @param rate Non-Formatted term-deposit rate. Example: 0.309764532
     * @return Formatted TD rate. Example: 3.09%
     */
    public static String asRate(BigDecimal rate) {
        String strRate;
        try {
            strRate = rate.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_FLOOR).toString();
            return concatStrings(strRate, "%");
        } catch (Exception e) {
            return null;
        }
    }

    public static String deformatRate(String rate) {
        if (rate != null)
            return rate.replaceAll("%", "");
        else
            return "0.0";
    }

    /**
     * Formatting the rate returned from Avaloq
     *
     * @param rate
     * @return
     */
    public static String asAvaloqRate(BigDecimal rate) {
        String strRate;
        try {
            strRate = rate.setScale(2, BigDecimal.ROUND_FLOOR).toString();
            return concatStrings(strRate, "%");
        } catch (Exception e) {
            return null;
        }
    }

    public static String asRate(com.avaloq.abs.bb.fld_def.NrFld rate) {
        String strRate;
        try {
            strRate = rate.getVal().setScale(2, BigDecimal.ROUND_FLOOR).toString();
            return concatStrings(strRate, "%");
        } catch (Exception e) {
            return null;
        }
    }

    public static DateTimeFld createDateTimeVal(DateTime dateTime) {
        DateTimeFld dateTimeVal = AvaloqObjectFactory.getFlddefobjectfactory().createDateTimeFld();
        try {
            XMLGregorianCalendar xmlCalVal = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(dateTime.toGregorianCalendar());
            xmlCalVal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            dateTimeVal.setVal(xmlCalVal);
            return dateTimeVal;
        } catch (DatatypeConfigurationException e) {
            logger.warn("Exception in createDateTimeVal method of AvaloqUtils.Jaxb config is incorrect, can not create DatatypeFactory", e);
            return null;
        }

    }

    public static BoolFld createBoolVal(Boolean bool) {
        if (bool != null) {
            BoolFld boolVal = AvaloqObjectFactory.getFlddefobjectfactory().createBoolFld();
            boolVal.setVal(bool);
            return boolVal;
        }
        return null;
    }

    public static com.btfin.abs.srchservice.v1_0.SrchReq makeUserSearchRequest(String searchtoken, String roleType, String codeId) {
        com.btfin.abs.srchservice.v1_0.SrchReq searchReq = AvaloqObjectFactory.getUserserachobjectfactory().createSrchReq();
        com.btfin.abs.srchservice.v1_0.Data requestData = AvaloqObjectFactory.getUserserachobjectfactory().createData();

        searchReq.setHdr(createHdr());
        requestData.setSrchToken(createTextVal(searchtoken));
        if (roleType.equalsIgnoreCase(Roles.ROLE_INVESTOR.name())) {
            requestData.setUserRole(createExtlIdVal(JobRole.INVESTOR.toString()));
        } else if (roleType.equalsIgnoreCase(Roles.ROLE_ADVISER.name())) {
            requestData.setUserRole(createExtlIdVal(JobRole.ADVISER.toString()));
        }

        if (StringUtils.isNotEmpty(codeId)) {
            requestData.setPersonType(createIdVal(codeId));
        }

        searchReq.setData(requestData);
        return searchReq;
    }

    public static String asAvaloqId(com.avaloq.abs.bb.fld_def.TextFld textField) {
        if (textField == null) {
            return "";
        }
        return textField.getAnnot().getCtx().getId();
    }

    public static String asAvaloqType(com.avaloq.abs.bb.fld_def.TextFld textField) {
        if (textField == null) {
            return "";
        }
        return textField.getAnnot().getCtx().getId();
    }

    public static NtfcnReq makeUpdateMultipleNotificationRequestFor(List<NotificationUpdateRequest> notificationUpdateRequestList) {

        NtfcnReq ntfcnReq = AvaloqObjectFactory.getNotificationobjectfactory().createNtfcnReq();
        ntfcnReq.setHdr(createHdr());
        com.btfin.abs.trxservice.ntfcn.v1_0.Action action = AvaloqObjectFactory.getNotificationobjectfactory().createAction();
        List<com.btfin.abs.trxservice.ntfcn.v1_0.UpdateNtfcn> updateNotificationList = action.getUpdate();
        for (NotificationUpdateRequest notificationUpdateRequest : notificationUpdateRequestList) {
            if (null != notificationUpdateRequest.getNotificationId() && null != notificationUpdateRequest.getStatus()) {
                com.btfin.abs.trxservice.ntfcn.v1_0.UpdateNtfcn updateNotification = AvaloqObjectFactory.getNotificationobjectfactory()
                        .createUpdateNtfcn();
                updateNotification.setNtfcnId(createNumberVal(new BigDecimal(notificationUpdateRequest.getNotificationId().getNotificationId())));
                updateNotification.setNtfcnStatusId(createExtlIdVal(notificationUpdateRequest.getStatus().getStatusValue()));
                updateNotificationList.add(updateNotification);
            }
        }
        ntfcnReq.setAction(action);
        return ntfcnReq;
    }

    public static NtfcnReq makeAddNotificationRequestFor(List<NotificationAddRequest> notificationAddRequestList) {

        NtfcnReq ntfcnReq = AvaloqObjectFactory.getNotificationobjectfactory().createNtfcnReq();
        ntfcnReq.setHdr(createHdr());
        com.btfin.abs.trxservice.ntfcn.v1_0.Action action = AvaloqObjectFactory.getNotificationobjectfactory().createAction();
        List<AddNtfcn> addNotificationList = action.getAdd();
        for (NotificationAddRequest notificationAddRequest : notificationAddRequestList) {
            AddNtfcn addNtfcn = AvaloqObjectFactory.getNotificationobjectfactory().createAddNtfcn();
            addNtfcn.setNtfcnEvtTypeId(createExtlIdVal(notificationAddRequest.getNotificationEventType().getId()));
            addNtfcn.setRespResvMtdId(createExtlIdVal(notificationAddRequest.getNotificationResolutionBaseKey().getResolutionGroup().getUserId()));
            addNtfcn.setRespBaseItem(createNumberVal(notificationAddRequest.getNotificationResolutionBaseKey().getKey()));
            addNtfcn.setTrigObjId(createIdVal(notificationAddRequest.getTriggeringObjectKey().getId()));
            addNtfcn.setCtxList(createTextVal(notificationAddRequest.getMessageContext()));
            addNotificationList.add(addNtfcn);
        }
        ntfcnReq.setAction(action);
        return ntfcnReq;
    }

    public static UserReq makeNotifyPasswordInformationUserRequest(String secUserId) {
        UserReq userReq = AvaloqObjectFactory.getPswChangeInfoUserObjectFactory().createUserReq();
        userReq.setHdr(createHdr());
        com.btfin.abs.trxservice.user.v1_0.Data data = AvaloqObjectFactory.getPswChangeInfoUserObjectFactory().createData();
        data.setSecUser(createExtlIdVal(secUserId));
        userReq.setData(data);
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = new ReqExec();
        com.btfin.abs.trxservice.base.v1_0.Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);
        req.setExec(reqExec);
        userReq.setReq(req);
        return userReq;
    }

    public static DataValidReq makeDataValidationRequest(String tfnNumber, String personId){
        DataValidReq dataValidReq = AvaloqObjectFactory.getDataValidObjectFactory().createDataValidReq();
        Hdr hdr = createHdr();
        Mode mode = new Mode();
        mode.setRes(Res.ASYNC);
        mode.setExec(Exec.ASYNC);
        hdr.setMode(mode);
        dataValidReq.setHdr(hdr);
        com.btfin.abs.trxservice.datavalid.v1_0.Data data = AvaloqObjectFactory.getDataValidObjectFactory().createData();
        data.setPersonId(createNumberVal(personId));
        data.setPersonTfnExist(createBoolVal(false));
        data.setTfn(createNumberVal(tfnNumber));
        dataValidReq.setData(data);
        return dataValidReq;
    }

    public static BpReq makeUpdateTaxPrefRequest(UpdateTaxPrefRequest request, String cgtlMethodId) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();

        bpReq.setHdr(createHdr());

        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        data.setCgtLkmTax(createExtlIdVal(cgtlMethodId));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeUpdatePrimaryContactRequest(UpdatePrimContactRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        data.setPriCtactPerson(createIdVal(request.getPrimaryContactPersonId().getId()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeAddLinkedAccountRequest(LinkedAccRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action addlinkedAccountAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<AllLinkedAcc> linkedAccountlist = addlinkedAccountAction.getAddLinkedAcc();
        AllLinkedAcc linkedAccount = AvaloqObjectFactory.getBprequestfactory().createAllLinkedAcc();
        linkedAccount.setAccName(createTextVal(request.getLinkedAccount().getName()));
        linkedAccount.setAccNr(createTextVal(request.getLinkedAccount().getAccountNumber()));
        linkedAccount.setBsb(createTextVal(request.getLinkedAccount().getBsb()));
        if (null != request.getLinkedAccount().getNickName()) {
            linkedAccount.setAccNickName(createTextVal(request.getLinkedAccount().getNickName()));
        }
        linkedAccount.setIsPri(createBoolVal(request.getLinkedAccount().isPrimary()));
        // linkedAccount.setCurry(createIdVal(request.getLinkedAccount().getCurrencyId()));
        linkedAccount.setCurry(createExtlIdVal(request.getLinkedAccount().getCurrency().getCurrency()));
        // Add Link account to the Linked Account list for addition
        linkedAccountlist.add(linkedAccount);
        data.setAction(addlinkedAccountAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeUpdateLinkedAccountRequest(LinkedAccRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action updatelinkedAccountAction = AvaloqObjectFactory.getBprequestfactory()
                .createAction();
        List<AllLinkedAcc> linkedAccountlist = updatelinkedAccountAction.getUpdLinkedAcc();
        AllLinkedAcc linkedAccount = AvaloqObjectFactory.getBprequestfactory().createAllLinkedAcc();
        linkedAccount.setAccName(createTextVal(request.getLinkedAccount().getName()));
        linkedAccount.setAccNr(createTextVal(request.getLinkedAccount().getAccountNumber()));
        linkedAccount.setBsb(createTextVal(request.getLinkedAccount().getBsb()));
        if (null != request.getLinkedAccount().getNickName()) {
            linkedAccount.setAccNickName(createTextVal(request.getLinkedAccount().getNickName()));
        }
        linkedAccount.setIsPri(createBoolVal(request.getLinkedAccount().isPrimary()));
        // linkedAccount.setCurry(createIdVal(request.getLinkedAccount().getCurrencyId()));
        linkedAccount.setCurry(createExtlIdVal(request.getLinkedAccount().getCurrency().getCurrency()));
        // Add Link account to the Linked Account list for Updation.
        linkedAccountlist.add(linkedAccount);
        data.setAction(updatelinkedAccountAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeDeleteLinkedAccountRequest(DeleteLinkedAccRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action deletelinkedAccountAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<DelLinkedAcc> linkedAccountlist = deletelinkedAccountAction.getDelLinkedAcc();
        DelLinkedAcc linkedAccount = AvaloqObjectFactory.getBprequestfactory().createDelLinkedAcc();
        linkedAccount.setAccNr(createTextVal(request.getBankAccount().getAccountNumber()));
        linkedAccount.setBsb(createTextVal(request.getBankAccount().getBsb()));
        // Add Link account to the Linked Account list for Deletion
        linkedAccountlist.add(linkedAccount);
        data.setAction(deletelinkedAccountAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeAddPayeeAccountRequest(PayeeRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action addPayeeAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<com.btfin.abs.trxservice.bp.v1_0.AllRegAcc> addRegAccountlist = addPayeeAction.getAddRegPayee();
        com.btfin.abs.trxservice.bp.v1_0.AllRegAcc regAccount = AvaloqObjectFactory.getBprequestfactory().createAllRegAcc();
        regAccount = setPayeeDetailForBpReq(request, regAccount);
        // Add Payee account to the Payee Account list for addition
        addRegAccountlist.add(regAccount);
        data.setAction(addPayeeAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeUpdatePayeeAccountRequest(PayeeRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action updatePayeeAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<com.btfin.abs.trxservice.bp.v1_0.AllRegAcc> updateRegAccountlist = updatePayeeAction.getUpdRegPayee();
        com.btfin.abs.trxservice.bp.v1_0.AllRegAcc regAccount = AvaloqObjectFactory.getBprequestfactory().createAllRegAcc();
        regAccount = setPayeeDetailForBpReq(request, regAccount);
        // Add Payee account to the Payee Account list for updation
        updateRegAccountlist.add(regAccount);
        data.setAction(updatePayeeAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeDeletePayeeAccountRequest(PayeeRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action deletePayeeAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<com.btfin.abs.trxservice.bp.v1_0.DelRegAcc> deleteRegAccountlist = deletePayeeAction.getDelRegPayee();
        com.btfin.abs.trxservice.bp.v1_0.DelRegAcc regAccount = AvaloqObjectFactory.getBprequestfactory().createDelRegAcc();
        regAccount.setPayeeIdent(createTextVal(request.getBankAccount().getBsb()));
        regAccount.setPayeeAcc(createTextVal(request.getBankAccount().getAccountNumber()));
        // Add Payee account to the Payee Account list for Deletion
        deleteRegAccountlist.add(regAccount);
        data.setAction(deletePayeeAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeAddBillerAccountRequest(BillerRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action addBillerAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<com.btfin.abs.trxservice.bp.v1_0.AllRegAcc> addBillerAccountlist = addBillerAction.getAddRegBiller();
        com.btfin.abs.trxservice.bp.v1_0.AllRegAcc regAccount = AvaloqObjectFactory.getBprequestfactory().createAllRegAcc();
        regAccount = setBillerDetailForBpReq(request, regAccount);
        // Add Biller account to the Biller Account list for Addition
        addBillerAccountlist.add(regAccount);
        data.setAction(addBillerAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeUpdateBillerAccountRequest(BillerRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action updateBillerAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<com.btfin.abs.trxservice.bp.v1_0.AllRegAcc> updateBillerAccountlist = updateBillerAction.getUpdRegBiller();
        com.btfin.abs.trxservice.bp.v1_0.AllRegAcc regAccount = AvaloqObjectFactory.getBprequestfactory().createAllRegAcc();
        regAccount = setBillerDetailForBpReq(request, regAccount);
        // Add Biller account to the Biller Account list for updation
        updateBillerAccountlist.add(regAccount);
        data.setAction(updateBillerAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeDeleteBillerAccountRequest(BillerRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action deleteBillerAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<com.btfin.abs.trxservice.bp.v1_0.DelRegAcc> deleteBillerAccountlist = deleteBillerAction.getDelRegBiller();
        com.btfin.abs.trxservice.bp.v1_0.DelRegAcc regAccount = AvaloqObjectFactory.getBprequestfactory().createDelRegAcc();
        regAccount.setPayeeIdent(createTextVal(request.getBillerDetail().getBillerCode()));
        regAccount.setPayeeAcc(createTextVal(request.getBillerDetail().getCRN()));
        // Add Biller account to the Biller Account list for deletion.
        deleteBillerAccountlist.add(regAccount);
        data.setAction(deleteBillerAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static BpReq makeUpdatePaymentLimitRequest(UpdatePaymentLimitRequest request) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action updateTrxLimitAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        List<TrxLimitElem> updateTrxLimitElemlist = updateTrxLimitAction.getUpdTrxLimit();
        TrxLimitElem trxLimitElement = AvaloqObjectFactory.getBprequestfactory().createTrxLimitElem();
        trxLimitElement.setMetaTyp(createExtlIdVal(request.getBusinessTransactionType().PAY.name()));
        trxLimitElement.setOrderTyp(createExtlIdVal(request.getBusinessTransactionOrderType().getName()));
        trxLimitElement.setCurry(createExtlIdVal(request.getCurrency().getCurrency()));
        trxLimitElement.setAmount(createNumberVal(request.getAmount()));
        // Add Biller account to the Biller Account list for updation
        updateTrxLimitElemlist.add(trxLimitElement);
        data.setAction(updateTrxLimitAction);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        bpReq.setData(data);
        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;

    }

    public static boolean validateAvaloqResponseForBPUpdate(BpRsp bpRes) {
        boolean updatedflag = false;
        if (!(null != bpRes.getRsp().getExec().getErrList())) {
            updatedflag = true;
        }
        return updatedflag;
    }

    public static Req createTransactionServiceExecuteReq() {
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        ReqExec reqExec = new ReqExec();
        com.btfin.abs.trxservice.base.v1_0.Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(Constants.DO);
        reqExec.setAction(action);
        req.setExec(reqExec);
        return req;
    }

    public static com.btfin.abs.trxservice.bp.v1_0.AllRegAcc setBillerDetailForBpReq(BillerRequest request,
                                                                                     com.btfin.abs.trxservice.bp.v1_0.AllRegAcc regAccount) {
        regAccount.setPayeeIdent(createTextVal(request.getBillerDetail().getBillerCode()));
        regAccount.setPayeeAcc(createTextVal(request.getBillerDetail().getCRN()));
        regAccount.setAccName(createTextVal(request.getBillerDetail().getName()));
        if (null != request.getBillerDetail().getNickName()) {
            regAccount.setAccNickName(createTextVal(request.getBillerDetail().getNickName()));
        }
        return regAccount;
    }

    public static com.btfin.abs.trxservice.bp.v1_0.AllRegAcc setPayeeDetailForBpReq(PayeeRequest request,
                                                                                    com.btfin.abs.trxservice.bp.v1_0.AllRegAcc regAccount) {
        regAccount.setPayeeIdent((createTextVal(request.getBankAccount().getBsb())));
        regAccount.setPayeeAcc(createTextVal(request.getBankAccount().getAccountNumber()));
        regAccount.setAccName(createTextVal(request.getBankAccount().getName()));
        if (null != request.getBankAccount().getNickName()) {
            regAccount.setAccNickName(createTextVal(request.getBankAccount().getNickName()));
        }
        return regAccount;
    }

    public static BpReq makeAddSubscriptionRequest(SubscriptionRequest request) {

        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action subscriptionAction = AvaloqObjectFactory.getBprequestfactory().createAction();
        //Add Subscription request
        SubscrReqElem subscriptionElement = AvaloqObjectFactory.getBprequestfactory().createSubscrReqElem();
        subscriptionElement.setProdId(createExtlId(request.getProductShortName(), PARAM_PRODUCT_SYMBOL));
        subscriptionAction.getAddSubscr().add(subscriptionElement);
        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        data.setAction(subscriptionAction);
        bpReq.setData(data);

        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }


    public static BpReq makeAddInvestmentRequest(InitialInvestmentRequest request) {
        return makeInvestmentRequest(request, true);
    }

    public static BpReq makeDeleteInvestmentRequest(InitialInvestmentRequest request) {
        return makeInvestmentRequest(request, false);
    }

    public static BpReq makeInvestmentRequest(InitialInvestmentRequest request, boolean addInvestment) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        bpReq.setHdr(createHdr());
        com.btfin.abs.trxservice.bp.v1_0.Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        com.btfin.abs.trxservice.bp.v1_0.Action investmentAction = AvaloqObjectFactory.getBprequestfactory().createAction();

        if (CollectionUtils.isNotEmpty(request.getInitialInvestmentAsset())) {
            AllInitInvstAsset allInitInvstAsset;
            for (InitialInvestmentAsset investmentAsset : request.getInitialInvestmentAsset()) {
                allInitInvstAsset = AvaloqObjectFactory.getBprequestfactory().createAllInitInvstAsset();
                allInitInvstAsset.setAssetId(createIdVal(investmentAsset.getInvestmentAssetId()));
                allInitInvstAsset.setAmount(createNumberVal(investmentAsset.getInitialInvestmentAmount()));
                if (addInvestment) {
                    investmentAction.getAddInitInvstAsset().add(allInitInvstAsset);
                } else {
                    investmentAction.getDelInitInvstAsset().add(allInitInvstAsset);
                }
            }
        }

        data.setBp(createIdVal(request.getAccountKey().getId()));
        data.setModiSeqNr(createNumberVal(request.getModificationIdentifier()));
        data.setAction(investmentAction);
        bpReq.setData(data);

        Req req = createTransactionServiceExecuteReq();
        bpReq.setReq(req);
        return bpReq;
    }

    public static RegUserReq makeLogoutUserRequest() {

        RegUserReq regUserReq = AvaloqObjectFactory.getRegUserObjectFactory().createRegUserReq();
        regUserReq.setHdr(createHdr());

        com.btfin.abs.trxservice.reguser.v1_0.Data data = AvaloqObjectFactory.getRegUserObjectFactory().createData();
        data.setAction(ActionType.DEREGISTER);

        regUserReq.setData(data);

        return regUserReq;
    }

}
