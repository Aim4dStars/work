package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.btfin.abs.common.v1_0.Hdr;
import com.btfin.abs.trxservice.pay.v1_0.BpayBiller;
import com.btfin.abs.trxservice.pay.v1_0.Data;
import com.btfin.abs.trxservice.pay.v1_0.PayAnyoneBenef;
import com.btfin.abs.trxservice.pay.v1_0.PayReq;
import com.btfin.abs.trxservice.pay.v1_0.Stord;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class AvaloqPayReqBuilder {

    private AvaloqPayReqBuilder() {

    }

    public static PayReq buildValidatePaymentPayRequest(PaymentDetails payment, Date bankDate) {
        PayReq payReq = buildPaymentPayRequest(payment, bankDate);
        payReq.setReq(AvaloqReqBuilder.buildDoValidationAction(payment));
        return payReq;
    }

    public static PayReq buildSubmitPaymentPayRequest(PaymentDetails payment, Date bankDate) {
        PayReq payReq = buildPaymentPayRequest(payment, bankDate);
        payReq.setReq(AvaloqReqBuilder.buildDoExecutionAction(payment));
        return payReq;
    }

    public static PayReq buildSavePaymentPayRequest(PaymentDetails payment, Date bankDate) {
        PayReq payReq = buildPaymentPayRequest(payment, bankDate);
        payReq.setReq(AvaloqReqBuilder.buildSaveExecutionAction(payment));
        return payReq;
    }

    public static PayReq buildStopPaymentPayRequest(PaymentDetails payment) {
        PayReq payReq = AvaloqObjectFactory.getPaymentObjectFactory().createPayReq();
        Hdr hdr = AvaloqGatewayUtil.createHdr();
        payReq.setHdr(hdr);
        Data data = AvaloqObjectFactory.getPaymentObjectFactory().createData();
        if (StringUtils.isNotBlank(payment.getPositionId())) {
            data.setPos(AvaloqGatewayUtil.createIdVal(payment.getPositionId()));
        }
        if (payment.getWithdrawalType() != null) {
            data.setUiPayTypeId(AvaloqGatewayUtil.createExtlIdVal(payment.getWithdrawalType().getIntlId()));
        }
        payReq.setData(data);
        payReq.setReq(AvaloqReqBuilder.buildCancelExecutionAction(payment));
        return payReq;
    }

    private static PayReq buildPaymentPayRequest(PaymentDetails payment, Date bankDate) {
        PayReq payReq = AvaloqObjectFactory.getPaymentObjectFactory().createPayReq();
        Hdr hdr = AvaloqGatewayUtil.createHdr();
        payReq.setHdr(hdr);
        Data data = AvaloqObjectFactory.getPaymentObjectFactory().createData();
        if (payment.getPositionId() != null) {
            data.setPos(AvaloqGatewayUtil.createIdVal(payment.getPositionId()));
        }
        if (payment.getAmount() != null) {
            data.setAmount(AvaloqGatewayUtil.createNumberVal(payment.getAmount()));
        }
        if (payment.getWithdrawalType() != null && (payment.getWithdrawalType() == WithdrawalType.PENSION_ONE_OFF_PAYMENT ||
                payment.getWithdrawalType() == WithdrawalType.LUMP_SUM_WITHDRAWAL)) {
            data.setTrxDate(null);
        } else {
            data.setTrxDate(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
        }
        data.setPayeeName(AvaloqGatewayUtil.createTextVal(payment.getPayeeName()));
        data.setDebMacc(AvaloqGatewayUtil.createIdVal(payment.getMoneyAccount().getMoneyAccountId()));
        data.setCurry(AvaloqGatewayUtil.createExtlIdVal(payment.getCurrencyType().getCurrency()));
        if (payment.getBenefeciaryInfo() != null) {
            data.setBenefInfo(AvaloqGatewayUtil.createTextVal(payment.getBenefeciaryInfo()));
        }
        data.setStord(buildStandingOrder(payment, bankDate));
        if (payment.getWithdrawalType() != null) {
            data.setUiPayTypeId(AvaloqGatewayUtil.createExtlIdVal(payment.getWithdrawalType().getIntlId()));
        }
        addPayeeDetails(data, payment);
        if (!StringUtils.isEmpty(payment.getBusinessChannel())) {
            data.setChannel(AvaloqGatewayUtil.createExtlIdVal(payment.getBusinessChannel()));
        }
        if (!StringUtils.isEmpty(payment.getClientIp())) {
            data.setDevId(AvaloqGatewayUtil.createTextVal(payment.getClientIp()));
        }
        payReq.setData(data);
        return payReq;
    }

    private static Stord buildStandingOrder(PaymentDetails payment, Date bankDate) {
        Stord standingOrder = AvaloqObjectFactory.getPaymentObjectFactory().createStord();

        if (payment.getRecurringFrequency() != null) {
            standingOrder.setStordPeriod(AvaloqGatewayUtil.createExtlIdVal(payment.getRecurringFrequency().getFrequency()));
            if (payment.getPensionPaymentType()!=null && payment.getTransactionDate()!=null) {
                standingOrder.setStordPeriodStart(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
            }
            if (payment.getMaxCount() != null)
                standingOrder.setMaxPeriodCnt(AvaloqGatewayUtil.createNumberVal(payment.getMaxCount()));
            if (payment.getEndDate() != null)
                standingOrder.setStordPeriodEnd(AvaloqGatewayUtil.createDateVal(payment.getEndDate()));
            setPaymentTypeAndIndexationDetails(standingOrder, payment);
        } else if (payment.getWithdrawalType() != null && (payment.getWithdrawalType() == WithdrawalType.PENSION_ONE_OFF_PAYMENT
                || payment.getWithdrawalType() == WithdrawalType.LUMP_SUM_WITHDRAWAL)) {
            return null;
        } else if (payment.getTransactionDate().after(bankDate)) {
            standingOrder.setStordPeriod(AvaloqGatewayUtil.createExtlIdVal(RecurringFrequency.Once.getFrequency()));
            standingOrder.setStordPeriodStart(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
            standingOrder.setStordPeriodEnd(AvaloqGatewayUtil.createDateVal(payment.getTransactionDate()));
        } else {
            return null;
        }
        return standingOrder;

    }

    private static void addPayeeDetails(Data data, PaymentDetails payment) {
        if (payment.getBpayBiller() != null) {
            BpayBiller bpayBiller = AvaloqObjectFactory.getPaymentObjectFactory().createBpayBiller();
            bpayBiller.setBillerCode(AvaloqGatewayUtil.createTextVal(payment.getBpayBiller().getBillerCode()));
            bpayBiller.setCrn(AvaloqGatewayUtil.createTextVal(payment.getBpayBiller().getCustomerReferenceNo()));
            data.setBpayBiller(bpayBiller);
        } else if (payment.getPayAnyoneBeneficiary() != null) {
            PayAnyoneBenef beneficiary = AvaloqObjectFactory.getPaymentObjectFactory().createPayAnyoneBenef();
            beneficiary.setBenefAcc(AvaloqGatewayUtil.createTextVal(payment.getPayAnyoneBeneficiary().getAccount()));
            // TODO: Check bsb formatting.
            beneficiary.setBsb(AvaloqGatewayUtil.createTextVal(payment.getPayAnyoneBeneficiary().getBsb()));
            data.setPayAnyoneBenef(beneficiary);
        }

    }

    private static void setPaymentTypeAndIndexationDetails(Stord standingOrder, PaymentDetails payment) {
        if (payment.getPensionPaymentType() != null && payment.getPensionPaymentType() != PensionPaymentType.SPECIFIC_AMOUNT) {
            standingOrder.setPensIdxMtdId(AvaloqGatewayUtil.createExtlIdVal(payment.getPensionPaymentType().getIntlId()));
        } else if (payment.getIndexationType() != null) {
            standingOrder.setPensIdxMtdId(AvaloqGatewayUtil.createExtlIdVal(payment.getIndexationType().getIntlId()));
            switch (payment.getIndexationType()) {
                case DOLLAR:
                    standingOrder.setPensFixedAmt(AvaloqGatewayUtil.createNumberVal(payment.getIndexationAmount()));
                    break;
                case PERCENTAGE:
                    standingOrder.setPensFixedPct(AvaloqGatewayUtil.createNumberVal(payment.getIndexationAmount()));
                    break;
                case CPI:
                case NONE:
                default:
                    break;
            }
        }
    }
}
