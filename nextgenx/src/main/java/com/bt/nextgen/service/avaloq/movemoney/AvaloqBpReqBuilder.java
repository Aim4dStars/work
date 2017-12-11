package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import com.btfin.abs.trxservice.bp.v1_0.CltOnbDet;
import com.btfin.abs.trxservice.bp.v1_0.Data;

import java.math.BigDecimal;

public class AvaloqBpReqBuilder {

    private AvaloqBpReqBuilder() {

    }

    public static BpReq buildValidatePaymentBpRequest(PaymentDetails payment) {
        BpReq bpReq = buildPaymentBpReq(payment);
        bpReq.setReq(AvaloqReqBuilder.buildDoValidationAction(payment));
        return bpReq;
    }

    public static BpReq buildSubmitPaymentBpRequest(PaymentDetails payment) {
        BpReq bpReq = buildPaymentBpReq(payment);
        bpReq.setReq(AvaloqReqBuilder.buildDoExecutionAction(payment));
        return bpReq;
    }

    private static BpReq buildPaymentBpReq(PaymentDetails payment) {
        BpReq bpReq = AvaloqObjectFactory.getBprequestfactory().createBpReq();
        Hdr hdr = AvaloqGatewayUtil.createHdr();
        bpReq.setHdr(hdr);
        Data data = AvaloqObjectFactory.getBprequestfactory().createData();
        bpReq.setData(data);
        data.setBp(AvaloqGatewayUtil.createIdVal(payment.getAccountKey().getId()));
        data.setModiSeqNr(AvaloqGatewayUtil.createNumberVal(payment.getModificationSeq()));
        CltOnbDet details = AvaloqObjectFactory.getBprequestfactory().createCltOnbDet();
        details.setPensFirstPayDt(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
        if (payment.getPensionPaymentType() != null && payment.getPensionPaymentType() != PensionPaymentType.SPECIFIC_AMOUNT) {
            details.setPensIdxMtdId(AvaloqGatewayUtil.createExtlIdVal(payment.getPensionPaymentType().getIntlId()));
        } else if (payment.getIndexationType() != null) {
            details.setPensIdxMtdId(AvaloqGatewayUtil.createExtlIdVal(payment.getIndexationType().getIntlId()));
            switch (payment.getIndexationType()) {
                case DOLLAR:
                    details.setPensIdxFixAmt(AvaloqGatewayUtil.createNumberVal(payment.getIndexationAmount()));
                    break;
                case PERCENTAGE:
                    details.setPensIdxFixPct(AvaloqGatewayUtil.createNumberVal(payment.getIndexationAmount()));
                    break;
                case CPI:
                case NONE:
                default:
                    break;
            }
        }
        if (payment.getAmount() != null) {
            details.setPensPayAmt(AvaloqGatewayUtil.createNumberVal(payment.getAmount()));
        }
        details.setPensPayFreqId(AvaloqGatewayUtil.createExtlIdVal(payment.getRecurringFrequency().getFrequency()));
        if (payment.getPayAnyoneBeneficiary() != null) {
            details.setAccNr(AvaloqGatewayUtil.createTextVal(payment.getPayAnyoneBeneficiary().getAccount()));
            details.setBsb(AvaloqGatewayUtil.createTextVal(payment.getPayAnyoneBeneficiary().getBsb()));
        }
        data.setCltOnbDet(details);
        return bpReq;

    }

}
