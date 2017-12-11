package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.btfin.abs.trxservice.base.v1_0.Rsp;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import com.btfin.abs.trxservice.bp.v1_0.CltOnbDet;
import com.btfin.abs.trxservice.pay.v1_0.Data;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.abs.trxservice.pay.v1_0.Stord;
import com.btfin.panorama.service.integration.RecurringFrequency;

import java.math.BigDecimal;
import java.util.Date;

public class MovemoneyDataUtil {

    public static PaymentDetailsImpl paymentDetails() {
        PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
        paymentDetails.setTransactionDate(new Date());
        paymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        paymentDetails.setAmount(new BigDecimal("4134"));
        paymentDetails.setBenefeciaryInfo("here is cash");
        paymentDetails.setIndexationType(IndexationType.DOLLAR);
        paymentDetails.setIndexationAmount(new BigDecimal("10"));
        paymentDetails.setRecurringFrequency(RecurringFrequency.Monthly);
        MoneyAccountIdentifierImpl moneyAccount = new MoneyAccountIdentifierImpl();
        moneyAccount.setMoneyAccountId("red45");
        paymentDetails.setMoneyAccount(moneyAccount);
        paymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        paymentDetails.setAccountKey(AccountKey.valueOf("1234555"));
        return paymentDetails;
    }

    public static PaymentDetailsImpl paymentLumpSumDetails() {
        PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
        paymentDetails.setTransactionDate(new Date());
        paymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        paymentDetails.setAmount(new BigDecimal("4134"));
        paymentDetails.setBenefeciaryInfo("here is cash");
        paymentDetails.setIndexationType(IndexationType.DOLLAR);
        paymentDetails.setIndexationAmount(new BigDecimal("10"));
        paymentDetails.setRecurringFrequency(RecurringFrequency.Monthly);
        MoneyAccountIdentifierImpl moneyAccount = new MoneyAccountIdentifierImpl();
        moneyAccount.setMoneyAccountId("red45");
        paymentDetails.setMoneyAccount(moneyAccount);
        paymentDetails.setWithdrawalType(WithdrawalType.LUMP_SUM_WITHDRAWAL);
        paymentDetails.setAccountKey(AccountKey.valueOf("1234555"));
        return paymentDetails;
    }

    public static PaymentDetailsImpl paymentPensionOneOffDetails() {
        PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
        paymentDetails.setTransactionDate(new Date());
        paymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        paymentDetails.setAmount(new BigDecimal("4134"));
        paymentDetails.setBenefeciaryInfo("here is cash");
        paymentDetails.setIndexationType(IndexationType.DOLLAR);
        paymentDetails.setIndexationAmount(new BigDecimal("10"));
        paymentDetails.setRecurringFrequency(RecurringFrequency.Monthly);
        MoneyAccountIdentifierImpl moneyAccount = new MoneyAccountIdentifierImpl();
        moneyAccount.setMoneyAccountId("red45");
        paymentDetails.setMoneyAccount(moneyAccount);
        paymentDetails.setWithdrawalType(WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        paymentDetails.setAccountKey(AccountKey.valueOf("1234555"));
        return paymentDetails;
    }

    public static PayRsp payRsp() {
        PayRsp payRsp = new PayRsp();
        Data data = new Data();
        payRsp.setData(data);
        data.setAmount(AvaloqGatewayUtil.createNumberVal(new BigDecimal("4134")));
        Stord stord = new Stord();
        data.setStord(stord);
        stord.setStordPeriod(AvaloqGatewayUtil.createIdVal(RecurringFrequency.Monthly.getFrequency()));
        data.setDoc(AvaloqGatewayUtil.createNumberVal(new BigDecimal("999991")));
        payRsp.setRsp(new Rsp());
        return payRsp;
    }

    public static BpRsp bpRsp() {
        BpRsp bpRsp = new BpRsp();
        com.btfin.abs.trxservice.bp.v1_0.Data data = new com.btfin.abs.trxservice.bp.v1_0.Data();
        bpRsp.setData(data);
        CltOnbDet cltOnbDet = new CltOnbDet();
        data.setCltOnbDet(cltOnbDet);
        data.setDoc(AvaloqGatewayUtil.createNumberVal(new BigDecimal("999992")));
        bpRsp.setRsp(new Rsp());
        return bpRsp;
    }

}
