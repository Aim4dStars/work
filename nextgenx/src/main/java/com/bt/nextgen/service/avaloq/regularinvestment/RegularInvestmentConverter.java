package com.bt.nextgen.service.avaloq.regularinvestment;

import java.math.BigInteger;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.order.OrderGroupConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPSchedule;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.btfin.abs.trxservice.base.v1_0.Action;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqExec;
import com.btfin.abs.trxservice.base.v1_0.ReqValid;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlReq;
import com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp;

@Service
public class RegularInvestmentConverter extends OrderGroupConverter {

    private static final Logger logger = LoggerFactory.getLogger(RegularInvestmentConverter.class);

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    protected AssetIntegrationService assetService;

    @Autowired
    private OrderItemInitialiser orderItemInit;

    /**
     * This method creates the RIP order to be sent to avaloq for validation
     * 
     * @param regularInvestment
     * @return ripReq
     */
    public TrxBdlReq toValidateRIPRequest(RegularInvestment regularInvestment, ServiceErrors serviceErrors) {
        ReqValid reqValid = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqValid();

        if (regularInvestment.getOrderGroupId() != null) {
            reqValid.setDoc(AvaloqGatewayUtil.createNumberVal(regularInvestment.getOrderGroupId()));
        }

        reqValid.setAction(createAction(regularInvestment));

        if (regularInvestment.getTransactionSeq() != null) {
            reqValid.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(regularInvestment.getTransactionSeq()));
        }
        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setValid(reqValid);
        TrxBdlReq ripReq = toGenericRIPRequest(regularInvestment, null, toGenericOrderRequest(regularInvestment, serviceErrors));
        ripReq.setReq(req);
        return ripReq;
    }

    private Action createAction(RegularInvestment regularInvestment) {
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        if (regularInvestment.getOrderGroupId() != null) {
            storeExistingRIP(regularInvestment, action);
        } else {
            action.setGenericAction(Constants.DO);
        }
        return action;
    }

    private void storeExistingRIP(RegularInvestment regularInvestment, Action action) {
        switch (regularInvestment.getRIPStatus()) {
            case ACTIVE:
                action.setWfcAction(Constants.STORE_RECURRING_TXN);
                break;
            case SUSPENDED:
                action.setWfcAction(Constants.STORE_SUSPENDED_TXN);
                break;
            case HOLD:
                action.setWfcAction(Constants.HOLD_RECUR);
                break;
            default:
                logger.warn("No Action set for validating a RIP in status: " + regularInvestment.getRIPStatus().getDisplayName());
        }
    }

    private TrxBdlReq toGenericRIPRequest(RegularInvestment regularInvestment, String linkedDDRef, TrxBdlReq ripReq) {
        if (regularInvestment.getRIPSchedule() != null) {
            addRIPScheduleToRequest(regularInvestment, ripReq);
        }
        if (linkedDDRef != null) {
            ripReq.getData().setRefDocId(AvaloqGatewayUtil.createIdVal(linkedDDRef));
        } else if (regularInvestment.getDirectDebitDetails() != null
                && regularInvestment.getDirectDebitDetails().getDepositAmount() != null) {

            // QC11818: Set min-bal field when DD exists. This is to avoid
            // warning message when the RIP is initially created (DD has NOT
            // been created yet).
            ripReq.getData().setMinBal(AvaloqGatewayUtil.createNumberVal(regularInvestment.getDirectDebitDetails().getDepositAmount()));
        }
        ripReq.getData().setMediumId(AvaloqGatewayUtil.createExtlIdVal(Constants.AVALOQ_MEDIUM_CODE_RIP));
        ripReq.getData().setOrderType(AvaloqGatewayUtil.createExtlIdVal(Constants.RIP_ORDER_TYPE));
        return ripReq;
    }

    private void addRIPScheduleToRequest(RegularInvestment regularInvestment, TrxBdlReq ripReq) {
        RIPSchedule ripSchedule = regularInvestment.getRIPSchedule();
        if (ripSchedule.getFirstExecDate() != null) {
            ripReq.getData().setFirstExec(AvaloqGatewayUtil.createDateVal(ripSchedule.getFirstExecDate().toDate()));
        }
        if (ripSchedule.getLastExecDate() != null) {
            ripReq.getData().setLastExec(AvaloqGatewayUtil.createDateVal(ripSchedule.getLastExecDate().toDate()));
        }
        if (ripSchedule.getRecurringFrequency() != null) {
            ripReq.getData().setRedoPeriodId(AvaloqGatewayUtil.createExtlIdVal(ripSchedule.getRecurringFrequency().getFrequency()));

        }

    }

    public TrxBdlReq toSubmitRIPRequest(RegularInvestment regularInvestment, String linkedDDRef, ServiceErrors serviceErrors) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        if (regularInvestment.getOrderGroupId() != null) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(regularInvestment.getOrderGroupId()));
        }

        reqExec.setAction(createAction(regularInvestment));
        reqExec.setOvrList(toOvrList(regularInvestment));

        if (regularInvestment.getTransactionSeq() != null) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(regularInvestment.getTransactionSeq()));
        }

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        TrxBdlReq ripReq = toGenericRIPRequest(regularInvestment, linkedDDRef,
                toGenericOrderRequest(regularInvestment, serviceErrors));
        ripReq.setReq(req);
        return ripReq;
    }

    public TrxBdlReq toSuspendRIPRequest(RegularInvestment regularInvestment, ServiceErrors serviceErrors) {
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(Constants.SUSPEND_RECURRING_TXN);

        return toUpdateRIPRequest(regularInvestment, action, serviceErrors);
    }

    public TrxBdlReq toCancelRIPRequest(RegularInvestment regularInvestment, ServiceErrors serviceErrors) {

        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        switch (regularInvestment.getRIPStatus()) {
            case ACTIVE:
                action.setWfcAction(Constants.DISCARD_RECURRING_TXN);
                break;
            case SUSPENDED:
                action.setWfcAction(Constants.DISCARD_SUSPENDED_TXN);
                break;
            default:
                logger.warn("No Action set for cancelling a RIP in status: " + regularInvestment.getRIPStatus().getDisplayName());
        }

        return toUpdateRIPRequest(regularInvestment, action, serviceErrors);
    }

    public TrxBdlReq toResumeRIPRequest(RegularInvestment regularInvestment, ServiceErrors serviceErrors) {
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        action.setWfcAction(Constants.RESUME_RECURRING_TXN);

        return toUpdateRIPRequest(regularInvestment, action, serviceErrors);
    }

    public TrxBdlReq toLoadRIPRequest(String ripId, ServiceErrors serviceErrors) {
        return toOrderLoadRequest(ripId, serviceErrors);
    }

    public RegularInvestmentImpl toLoadRIPResponse(String ripId, BigInteger transactionSeq, AccountKey accountKey,
            TrxBdlRsp ripRsp, ServiceErrors serviceErrors) {

        return toGenericRIPResponse(ripId, transactionSeq, accountKey, ripRsp, serviceErrors);
    }

    protected TrxBdlReq toUpdateRIPRequest(RegularInvestment regularInvestment, Action action, ServiceErrors serviceErrors) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        reqExec.setAction(action);
        if (regularInvestment.getOrderGroupId() != null) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(regularInvestment.getOrderGroupId()));
        }

        if (regularInvestment.getTransactionSeq() != null) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(regularInvestment.getTransactionSeq()));
        }

        String linkedDDRef = null;
        if (regularInvestment.getDirectDebitDetails() != null) {
            linkedDDRef = regularInvestment.getDirectDebitDetails().getReceiptNumber();
        }

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        orderItemInit.initOrderItem(regularInvestment, serviceErrors);

        TrxBdlReq updateReq = toGenericRIPRequest(regularInvestment, linkedDDRef,
                toGenericOrderRequest(regularInvestment, serviceErrors));
        updateReq.setReq(req);
        return updateReq;
    }

    protected TrxBdlReq toGenericOrderRequest(RegularInvestment regInvestment, ServiceErrors serviceErrors) {
        TrxBdlReq orderReq = super.toGenericOrderRequest(regInvestment, serviceErrors);
        String fundSourceId = regInvestment.getFundSourceId();
        if (fundSourceId != null && regInvestment.getOrderGroupId() != null && !regInvestment.getOrderGroupId().isEmpty()) {
            // Fund source has been identified.
            orderReq.getData().setTrxContId(AvaloqGatewayUtil.createIdVal(fundSourceId));
        }

        return orderReq;
    }

    protected RegularInvestmentImpl toGenericRIPResponse(String ripId, BigInteger transactionSeq, AccountKey accountKey,
            TrxBdlRsp ripRsp, ServiceErrors serviceErrors) {
        RegularInvestmentImpl ripResponse = new RegularInvestmentImpl(toGenericOrderResponse(ripId, transactionSeq, accountKey,
                ripRsp, null, serviceErrors));

        // Set RIPSchedule
        String frequency = AvaloqGatewayUtil.asString(ripRsp.getData().getRedoPeriodId());
        RIPScheduleImpl schedule = new RIPScheduleImpl(AvaloqGatewayUtil.asDateTime(ripRsp.getData().getFirstExec()),
                AvaloqGatewayUtil.asDateTime(ripRsp.getData().getLastExec()),
                RIPRecurringFrequency.getRecurringFrequency(staticIntegrationService.loadCode(CodeCategory.TRX_BDL_PERIOD,
                        frequency, serviceErrors).getIntlId()));
        ripResponse.setRIPSchedule(schedule);

        // Set fund-sournce
        ripResponse.setFundSourceId(AvaloqGatewayUtil.asString(ripRsp.getData().getTrxContId()));

        String ripStatus=null;
        if(ripRsp.getData().getUiWfStatus()!=null) {
            ripStatus = staticIntegrationService.loadCode(CodeCategory.ORDER_STATUS,
                    AvaloqGatewayUtil.asString(ripRsp.getData().getUiWfStatus()), serviceErrors).getIntlId();
        }

        if (ripStatus != null) {
            ripResponse.setRIPStatus(RIPStatus.getRIPStatus(ripStatus));
        }

        String refDocId = null;
        if (ripRsp.getData().getRefDocId() != null) {
            refDocId = AvaloqGatewayUtil.asString(ripRsp.getData().getRefDocId());
            if (refDocId != null && !refDocId.isEmpty()) {
                RecurringDepositDetailsImpl directDebitDetails = new RecurringDepositDetailsImpl();
                directDebitDetails.setReceiptNumber(refDocId);

                // Next execution date of the direct-debit.
                directDebitDetails.setNextTransactionDate(AvaloqGatewayUtil.asDateTime(ripRsp.getData().getRefDocNextExec()));

                ripResponse.setDirectDebitDetails(directDebitDetails);
            }
        }

        return ripResponse;
    }

    public TrxBdlReq toSaveRIPRequest(RegularInvestment regularInvestment, String linkedDDRef, ServiceErrors serviceErrors) {
        ReqExec reqExec = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqExec();
        Action action = AvaloqObjectFactory.getTransactionBaseObjectFactory().createAction();
        if (regularInvestment.getOrderGroupId() != null) {
            reqExec.setDoc(AvaloqGatewayUtil.createNumberVal(regularInvestment.getOrderGroupId()));
            action.setWfcAction(Constants.EXISTING_SAVE_RIP);
        }

        else{
            action.setWfcAction(Constants.SAVE_RIP);
        }
        reqExec.setOvrList(toOvrList(regularInvestment));
        reqExec.setAction(action);
        if (regularInvestment.getTransactionSeq() != null) {
            reqExec.setTransSeqNr(AvaloqGatewayUtil.createNumberVal(regularInvestment.getTransactionSeq()));
        }

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setExec(reqExec);
        TrxBdlReq ripReq = toGenericRIPRequest(regularInvestment, linkedDDRef,
                toGenericOrderRequest(regularInvestment, serviceErrors));
        ripReq.setReq(req);
        return ripReq;
    }

}
