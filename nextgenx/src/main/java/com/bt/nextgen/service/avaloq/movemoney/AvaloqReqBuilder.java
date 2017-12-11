package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Ovr;
import com.btfin.abs.trxservice.base.v1_0.OvrList;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AvaloqReqBuilder {

    private AvaloqReqBuilder() {

    }

    protected static Req buildDoValidationAction(PaymentDetails paymentDetails) {
        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();
        if (paymentDetails.getPaymentAction()!=null) { //Handle validation for saved payments
            reqValid.setAction(getWFCAction(paymentDetails.getPaymentAction().getAction()));
            if(StringUtils.isNotBlank(paymentDetails.getDocId())) {
                reqValid.setDoc(AvaloqGatewayUtil.createNumberVal(paymentDetails.getDocId()));
            }
            if (StringUtils.isNotBlank(paymentDetails.getTransactionSeqNo())) {
                reqValid.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(paymentDetails.getTransactionSeqNo()));
            }
        }else {
            reqValid.setAction(getAction(Constants.DO));
        }
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);
        return req;
    }

    protected static Req buildDoExecutionAction(PaymentDetails paymentDetails) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();

        if (paymentDetails.getPaymentAction()!=null) { //Handle submit for saved payments
            reqExec.setAction(getWFCAction(paymentDetails.getPaymentAction().getAction()));
            if(StringUtils.isNotBlank(paymentDetails.getDocId())) {
                reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(paymentDetails.getDocId()));
            }
            if (StringUtils.isNotBlank(paymentDetails.getTransactionSeqNo())) {
                reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(paymentDetails.getTransactionSeqNo()));
            }
        }else{
            reqExec.setAction(getAction(Constants.DO));
        }
        reqExec.setOvrList(toOvrList(paymentDetails));
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        return req;
    }

    protected static Req buildCancelExecutionAction(PaymentDetails paymentDetails) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        if (paymentDetails.getPaymentAction()!=null) { //Handle cancel for saved payments
            reqExec.setAction(getWFCAction(paymentDetails.getPaymentAction().getAction()));
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(paymentDetails.getDocId()));
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(paymentDetails.getTransactionSeqNo()));
        }else{
            reqExec.setAction(getAction(Constants.CANCEL));
        }
        reqExec.setOvrList(toOvrList(paymentDetails));
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        return req;
    }

    protected static Action getAction(String actionString) {
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setGenericAction(actionString);
        return action;
    }

    protected static Action getWFCAction(String actionString) {
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(actionString);
        return action;
    }


    protected static OvrList toOvrList(PaymentDetails paymentDetails) {
        List<ValidationError> warnings = paymentDetails.getWarnings();

        if (warnings == null || warnings.isEmpty()) {
            return null;
        }

        OvrList ovrList = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvrList();
        for (ValidationError warning : warnings) {
            Ovr ovr = AvaloqObjectFactory.getTransactionBaseObjectFactory().createOvr();
            ovr.setOvrId(AvaloqGatewayUtil.createExtlIdVal(warning.getErrorId()));
            ovrList.getOvr().add(ovr);
        }

        return ovrList;
    }

    protected static Req buildSaveExecutionAction(PaymentDetails paymentDetails) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(getWFCAction(paymentDetails.getPaymentAction().getAction()));
        if(StringUtils.isNotBlank(paymentDetails.getDocId())) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(paymentDetails.getDocId()));
        }
        if (StringUtils.isNotBlank(paymentDetails.getTransactionSeqNo())) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(paymentDetails.getTransactionSeqNo()));
        }
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        return req;
    }

}
