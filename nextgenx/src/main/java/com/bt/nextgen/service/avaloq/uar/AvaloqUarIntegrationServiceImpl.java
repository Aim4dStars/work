package com.bt.nextgen.service.avaloq.uar;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.uar.UarAction;
import com.bt.nextgen.service.integration.uar.UarDoc;
import com.bt.nextgen.service.integration.uar.UarDocImpl;
import com.bt.nextgen.service.integration.uar.UarIntegrationService;
import com.bt.nextgen.service.integration.uar.UarRecords;
import com.bt.nextgen.service.integration.uar.UarRequest;
import com.bt.nextgen.service.integration.uar.UarResponse;
import com.bt.nextgen.service.integration.uar.UarResponseImpl;
import com.btfin.abs.trxservice.uar.v1_0.Data;
import com.btfin.abs.trxservice.uar.v1_0.Err;
import com.btfin.abs.trxservice.uar.v1_0.Uar;
import com.btfin.abs.trxservice.uar.v1_0.UarRec;
import com.btfin.abs.trxservice.uar.v1_0.UarReq;
import com.btfin.abs.trxservice.uar.v1_0.UarRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


@Service("avaloqUarIntegrationService")
public class AvaloqUarIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements UarIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqUarIntegrationServiceImpl.class);

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private AvaloqExecute avaloqExecute;

    @Override
    public UarDoc getUarOrderId(List<String> keys, ServiceErrors serviceErrors) {

        UarDoc uarDoc = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(Template.UAR_DOC).forF1OeIds(keys)
                , UarDocImpl.class
                , serviceErrors);
        return uarDoc;
    }

    @Override
    public UarResponse getUarAccounts(final UarRequest uarRequest, final ServiceErrors serviceErrors) {
        logger.info("AvaloqUarIntegrationServiceImpl.getUarAccounts()");
        final UarResponse uarResponse = (UarResponse) new UarResponseImpl();
        return new IntegrationSingleOperation <UarResponse>("getUarRecords", serviceErrors) {
            @Override
            public UarResponse performOperation() {

                UarReq uarReq = AvaloqUtils.makeUarRequest(UarAction.GET_UAR_LIST, uarRequest);
                UarRsp uarRsp = webserviceClient.sendToWebService(uarReq, AvaloqOperation.UAR_REQ, serviceErrors);
                Data data = uarRsp.getData();
                if (data != null) {
                    List <UarRecords> uarRecordsList = new ArrayList<>();
                    setUarBasicDetails(data, uarResponse);
                    //Set UAR Records List Direct + Indirect
                    uarRecordsList = setUarRecordList(data.getUARLists().getDirJobList(), "DIR");
                    uarRecordsList.addAll(setUarRecordList(data.getUARLists().getIndirJobList(), "INDIR"));
                    uarResponse.setUarRecords(uarRecordsList);
                }
                return uarResponse;
            }
        }.run();
    }

    private void setUarBasicDetails(Data data, UarResponse uarResponse) {
        uarResponse.setDocId(data.getDoc().getVal());
        uarResponse.setWfcStatus(data.getWfcStatus().getVal());
        uarResponse.setWfcDisplayText(data.getWfcStatus().getAnnot().getDisplText());
        uarResponse.setBrokerId(data.getUARLists().getOeId().getVal());
        uarResponse.setUarDate(null!=data.getUARLists().getUarDate() ? AvaloqGatewayUtil.asDateTime(data.getUARLists().getUarDate()) : null);
    }

    private List<UarRecords> setUarRecordList(UarRec data, String recordType) {
        List<UarRecords> uarRecordsList = new ArrayList<>();
        if (null!=data) {
            for(Uar uarDetail : data.getUarRec()){
                UarRecords uarRecord = new UarRecords();
                uarRecord.setRecordIndex(null != uarDetail.getIdx() ? uarDetail.getIdx().getVal() : null);
                uarRecord.setBrokerId(null!=uarDetail.getOeId() ? uarDetail.getOeId().getVal() : null);
                uarRecord.setBrokerName(null != uarDetail.getOeId().getAnnot() ? uarDetail.getOeId().getAnnot().getDisplText() : null);
                uarRecord.setJobId(null != uarDetail.getJobId() ? uarDetail.getJobId().getVal() : null);
                uarRecord.setPersonId(null != uarDetail.getPersonId() ? uarDetail.getPersonId().getVal() : null);
                uarRecord.setPersonName(null != uarDetail.getPersonId().getAnnot() ? uarDetail.getPersonId().getAnnot().getDisplText() : null);
                uarRecord.setPermissionId(null != uarDetail.getJobOeAuthRoleId() ? uarDetail.getJobOeAuthRoleId().getVal() : null);
                uarRecord.setPermissionName(null != uarDetail.getJobOeAuthRoleId().getAnnot() ? uarDetail.getJobOeAuthRoleId().getAnnot().getDisplText() : null);
                uarRecord.setCurrPermissionId(null != uarDetail.getCurrJobOeAuthRoleId() ? uarDetail.getCurrJobOeAuthRoleId().getVal() : null);
                uarRecord.setCurrPermissionName(null != uarDetail.getCurrJobOeAuthRoleId().getAnnot() ? uarDetail.getCurrJobOeAuthRoleId().getAnnot().getDisplText() : null);
                uarRecord.setLastUarDate(null != uarDetail.getLastUarDate() ? AvaloqGatewayUtil.asDateTime(uarDetail.getLastUarDate()) : null);
                uarRecord.setLastUarOrderId(null != uarDetail.getLastUarDocId() ? uarDetail.getLastUarDocId().getVal() : null);
                uarRecord.setDecisionId(null != uarDetail.getDecsnId() ? uarDetail.getDecsnId().getVal() : null);
                uarRecord.setDecisionDocId(null != uarDetail.getDecsnDocId() ? uarDetail.getDecsnDocId().getVal() : null);
                uarRecord.setApproverUserId(null != uarDetail.getUserId() ? uarDetail.getUserId().getVal() : null);
                uarRecord.setUarDoneDate(null != uarDetail.getTimestamp() ? AvaloqGatewayUtil.asDate(uarDetail.getTimestamp()) : null);
                uarRecord.setIsFrozen(uarDetail.getIsFrozen().isVal());
                uarRecord.setIsInvalid(uarDetail.getIsInvalid().isVal());
                uarRecord.setRecordType(recordType);
                if (null!=uarDetail.getErrList() &&  !uarDetail.getErrList().getErrRec().isEmpty())
                    setErrorList(uarDetail, uarRecord);

                uarRecordsList.add(uarRecord);
            }
        }
        return uarRecordsList;
    }

    private void setErrorList(Uar uarDetail, UarRecords uarRecord) {
        ListIterator<Err> errListIterator = uarDetail.getErrList().getErrRec().listIterator();
        List<ServiceError> error = new ArrayList<>();

        while (errListIterator.hasNext()) {
            ServiceError serror = new ServiceErrorImpl();
            Err err = errListIterator.next();
            serror.setErrorCode(err.getErrId().getVal().toString());
            serror.setReason(err.getErrMsg().getVal());
            error.add(serror);
        }
        uarRecord.setErrors(error);
    }


    public UarResponse submitUarAccounts(final UarRequest uarRequest, final ServiceErrors serviceErrors)
    {logger.info("AvaloqUarIntegrationServiceImpl.getUarAccounts()");
        final UarResponse uarResponse = (UarResponse) new UarResponseImpl();
        return new IntegrationSingleOperation <UarResponse>("getUarRecords", serviceErrors) {
            @Override
            public UarResponse performOperation() {
                UarReq uarReq = AvaloqUtils.makeUarRequest(UarAction.SUBMIT_UAR_LIST, uarRequest);
                UarRsp uarRsp = webserviceClient.sendToWebService(uarReq, AvaloqOperation.UAR_REQ, serviceErrors);
                Data data = uarRsp.getData();
                if (data != null) {
                    List <UarRecords> uarRecordsList = new ArrayList<>();
                    setUarBasicDetails(data, uarResponse);
                    //Set UAR Records List Direct + Indirect
                    uarRecordsList = setUarRecordList(data.getUARLists().getDirJobList(),"DIR");
                    uarRecordsList.addAll(setUarRecordList(data.getUARLists().getIndirJobList(),"INDIR"));
                    uarResponse.setUarRecords(uarRecordsList);
                }
                return uarResponse;
            }
        }.run();
    }




}
