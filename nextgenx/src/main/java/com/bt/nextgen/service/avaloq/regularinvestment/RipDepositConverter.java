package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.abs.trxservice.base.v1_0.Req;
import com.btfin.abs.trxservice.base.v1_0.ReqGet;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@SuppressWarnings({ "squid:MethodCyclomaticComplexity",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck" })
@Service
public class RipDepositConverter {

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    public InpayReq toLoadDDRequest(String linkedDDRef) {
        InpayReq inpayReq = AvaloqObjectFactory.getDepositObjectFactory().createInpayReq();

        // Create Request Header
        inpayReq.setHdr(AvaloqGatewayUtil.createHdr());

        ReqGet reqGet = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReqGet();
        reqGet.setDoc(AvaloqGatewayUtil.createIdVal(linkedDDRef));

        Req req = AvaloqObjectFactory.getTransactionBaseObjectFactory().createReq();
        req.setGet(reqGet);

        inpayReq.setReq(req);
        return inpayReq;
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    public RecurringDepositDetails toLoadDDResponse(String receiptNumber, InpayRsp inpayRsp, ServiceErrors serviceErrors) {
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId(
                (inpayRsp.getData().getCredMacc()) != null ? AvaloqGatewayUtil.asString(inpayRsp.getData().getCredMacc()) : null);
        PayAnyoneAccountDetails payAnyoneAccountDetails = new PayAnyoneAccountDetailsImpl();
        payAnyoneAccountDetails
                .setAccount((inpayRsp.getData().getPayerAcc()) != null ? AvaloqGatewayUtil.asString(inpayRsp.getData().getPayerAcc()) : null);
        payAnyoneAccountDetails.setBsb((inpayRsp.getData().getBsb()) != null ? AvaloqGatewayUtil.asString(inpayRsp.getData().getBsb()) : null);

        DateTime transactionDate = null;
        if (inpayRsp.getData().getTrxDate() != null && inpayRsp.getData().getTrxDate().getVal() != null) {
            transactionDate = AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getTrxDate());
        }

        // for scheduled Transaction
        if (inpayRsp.getData().getContr() != null && inpayRsp.getData().getContr().getContrPeriodStart() != null) {
            transactionDate = AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getContr().getContrPeriodStart());
        }

        DateTime depositDate = null;
        if (inpayRsp.getData().getTrxDate() != null || inpayRsp.getData().getTrxDate().getVal() != null) {
            depositDate = AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getTrxDate());
        }

        DateTime startDate = inpayRsp.getData().getContr().getContrPeriodStart() != null
                ? AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getContr().getContrPeriodStart()) : null;
        DateTime endDate = inpayRsp.getData().getContr().getContrPeriodEnd() != null
                ? AvaloqGatewayUtil.asDateTime(inpayRsp.getData().getContr().getContrPeriodEnd()) : null;
        Integer maxCount = inpayRsp.getData().getContr().getMaxPeriodCnt() != null
                ? AvaloqGatewayUtil.asInt(inpayRsp.getData().getContr().getMaxPeriodCnt()) : null;
        String positionId = AvaloqGatewayUtil.asString(inpayRsp.getData().getPos());

        BigDecimal depositAmount = inpayRsp.getData().getAmount() != null ? AvaloqGatewayUtil.asBigDecimal(inpayRsp.getData().getAmount()) : null;
        String frequency = inpayRsp.getData().getContr().getContrPeriod() != null
                ? AvaloqGatewayUtil.asString(inpayRsp.getData().getContr().getContrPeriod()) : null;
        RecurringFrequency recurringFrequency = RecurringFrequency.getRecurringFrequency(
                staticIntegrationService.loadCode(CodeCategory.DD_PERIOD, frequency, serviceErrors).getIntlId());
        String description = (inpayRsp.getData().getBenefRefNr()) != null ? AvaloqGatewayUtil.asString(inpayRsp.getData().getBenefRefNr()) : null;

        RecurringDepositDetails deposit = new RecurringDepositDetailsImpl(moneyAccountIdentifier, payAnyoneAccountDetails,
                depositAmount, CurrencyType.AustralianDollar, description, transactionDate, receiptNumber, depositDate, null,
                recurringFrequency, startDate, endDate, maxCount, positionId);

        return deposit;
    }

}
