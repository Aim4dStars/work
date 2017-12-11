package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.rollover.RolloverDetails;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.rlovin.v1_0.Data;
import com.btfin.abs.trxservice.rlovin.v1_0.RlovInReq;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RolloverConverter {

    private static final String WFC_ACTION_SAVE_NEW = "opn_hold";
    private static final String WFC_ACTION_SAVE_EXISTING = "hold_store";
    private static final String WFC_ACTION_SUBMIT_EXISTING = "hold_prc";
    private static final String WFC_ACTION_DISCARD_EXISTING = "hold_discd";

    protected RlovInReq toLoadRolloverRequest(String rolloverId) {
        ReqGet reqGet = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqGet();
        reqGet.setDoc(AvaloqGatewayUtil.createIdVal(rolloverId));

        Req get = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        get.setGet(reqGet);

        RlovInReq req = AvaloqObjectFactory.getRolloverInObjectFactory().createRlovInReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());
        req.setReq(get);

        return req;
    }

    protected RlovInReq toDiscardRolloverRequest(String rolloverId) {
        Action execAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        execAction.setWfcAction(WFC_ACTION_DISCARD_EXISTING);

        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(execAction);
        reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(rolloverId));

        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setExec(reqExec);

        RlovInReq req = AvaloqObjectFactory.getRolloverInObjectFactory().createRlovInReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());
        req.setReq(txReq);

        return req;
    }

    protected RlovInReq toSaveRolloverRequest(RolloverDetails rolloverDetails) {

        Action execAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();

        if (rolloverDetails.getRolloverId() != null) {
            execAction.setWfcAction(WFC_ACTION_SAVE_EXISTING);
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(rolloverDetails.getRolloverId()));
        } else {
            execAction.setWfcAction(WFC_ACTION_SAVE_NEW);
        }
        reqExec.setAction(execAction);

        if (rolloverDetails.getLastTransSeqId() != null) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(rolloverDetails.getLastTransSeqId()));
        }

        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setExec(reqExec);

        com.btfin.abs.trxservice.rlovin.v1_0.RlovInReq req = createRolloverInRequest(rolloverDetails);
        req.setReq(txReq);

        return req;
    }

    protected RlovInReq toRolloverInRequest(RolloverDetails rolloverDetails) {

        Action execAction = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();

        if (rolloverDetails.getRolloverId() != null) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(rolloverDetails.getRolloverId()));
            execAction.setWfcAction(WFC_ACTION_SUBMIT_EXISTING);
        } else {
            execAction.setGenericAction(Constants.DO);
        }
        reqExec.setAction(execAction);

        if (rolloverDetails.getLastTransSeqId() != null) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(rolloverDetails.getLastTransSeqId()));
        }

        Req txReq = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        txReq.setExec(reqExec);

        com.btfin.abs.trxservice.rlovin.v1_0.RlovInReq req = createRolloverInRequest(rolloverDetails);
        req.setReq(txReq);

        return req;
    }

    protected RlovInReq createRolloverInRequest(RolloverDetails rolloverDetails) {
        RlovInReq req = AvaloqObjectFactory.getRolloverInObjectFactory().createRlovInReq();
        req.setHdr(AvaloqGatewayUtil.createHdr());

        Data data = AvaloqObjectFactory.getRolloverInObjectFactory().createData();
        data.setBpId(AvaloqGatewayUtil.createIdVal(rolloverDetails.getAccountKey().getId()));
        if (StringUtils.isNotEmpty(rolloverDetails.getFundId())) {
            data.setFundId(AvaloqGatewayUtil.createExtlIdVal(rolloverDetails.getFundId()));
        }
        data.setFundName(AvaloqGatewayUtil.createTextVal(rolloverDetails.getFundName()));
        data.setFundAbn(AvaloqGatewayUtil.createTextVal(rolloverDetails.getFundAbn()));
        data.setFundUsi(AvaloqGatewayUtil.createTextVal(rolloverDetails.getFundUsi()));
        if (rolloverDetails.getAmount() != null) {
            data.setFundEstimAmt(AvaloqGatewayUtil.createNumberVal(rolloverDetails.getAmount()));
        }
        data.setSsRlovIn(AvaloqUtils.createBoolVal(rolloverDetails.getPanInitiated()));
        if (rolloverDetails.getRequestDate() != null) {
            data.setSsReqDt(AvaloqGatewayUtil.createDateVal(rolloverDetails.getRequestDate().toDate()));
        }
        data.setMbrAccNr(AvaloqGatewayUtil.createTextVal(rolloverDetails.getAccountNumber()));
        data.setRlovOptId(AvaloqGatewayUtil.createExtlIdVal(rolloverDetails.getRolloverOption().getCode()));
        data.setRlovTypeId(AvaloqGatewayUtil.createExtlIdVal(rolloverDetails.getRolloverType().getCode()));
        data.setInclInsur(AvaloqUtils.createBoolVal(rolloverDetails.getIncludeInsurance()));

        req.setData(data);
        return req;
    }
}
