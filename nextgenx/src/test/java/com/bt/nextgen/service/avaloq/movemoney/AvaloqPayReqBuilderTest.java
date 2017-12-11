package com.bt.nextgen.service.avaloq.movemoney;

import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentActionType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.btfin.abs.trxservice.pay.v1_0.PayReq;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AvaloqPayReqBuilderTest {

    private PaymentDetailsImpl paymentDetails = MovemoneyDataUtil.paymentDetails();
    private PaymentDetailsImpl lumpSumPaymentDetails = MovemoneyDataUtil.paymentLumpSumDetails();
    private PaymentDetailsImpl pensionOneOffPaymentDetails = MovemoneyDataUtil.paymentPensionOneOffDetails();

    @Test
    public void testBuildValidatePaymentRequest_whenProvidedThePaymentDetails_thenItReturnsAPayReqWithAValidAction() {
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(paymentDetails, new Date());
        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getData().getTrxDate(), notNullValue());
        assertThat(payReq.getReq().getValid(), notNullValue());
        assertThat(payReq.getReq().getExec(), nullValue());
        assertThat(payReq.getReq().getValid().getAction().getWfcAction(), nullValue());
        assertThat(payReq.getReq().getValid().getAction().getGenericAction(), equalTo(Constants.DO));
    }

    @Test
    public void testBuildValidatePaymentRequest_whenLumpSumPayment_thenTransactionDateIsNull() {
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(lumpSumPaymentDetails, new Date());
        assertThat(payReq.getData().getTrxDate(), nullValue());
    }

    @Test
    public void testBuildSubmitPaymentRequest_whenLumpSumPayment_thenTransactionDateIsNull() {
        PayReq payReq = AvaloqPayReqBuilder.buildSubmitPaymentPayRequest(lumpSumPaymentDetails, new Date());
        assertThat(payReq.getData().getTrxDate(), nullValue());
    }

    @Test
    public void testBuildValidatePaymentRequest_whenOneOffPayment_thenTransactionDateIsNull() {
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(pensionOneOffPaymentDetails, new Date());
        assertThat(payReq.getData().getTrxDate(), nullValue());
    }

    @Test
    public void testBuildSubmitPaymentRequest_whenOneOffPayment_thenTransactionDateIsNull() {
        PayReq payReq = AvaloqPayReqBuilder.buildSubmitPaymentPayRequest(pensionOneOffPaymentDetails, new Date());
        assertThat(payReq.getData().getTrxDate(), nullValue());
    }

    @Test
    public void testBuildValidatePaymentRequest_whenProvidedThePaymentDetailsWithABPayBiller_thenItReturnsAPayReqWithABPayBiller() {
        BpayBillerImpl biller = new BpayBillerImpl();
        biller.setBillerCode("bpay_code");
        biller.setCustomerReferenceNo("1234ref");
        paymentDetails.setBpayBiller(biller);
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(paymentDetails, new Date());
        assertThat(payReq.getData().getBpayBiller(), notNullValue());
        assertThat(payReq.getData().getBpayBiller().getBillerCode().getVal(), equalTo("bpay_code"));
        assertThat(payReq.getData().getBpayBiller().getCrn().getVal(), equalTo("1234ref"));
        assertThat(payReq.getData().getPayAnyoneBenef(), nullValue());
        assertThat(payReq.getData().getTrxDate(), notNullValue());
    }

    @Test
    public void testBuildValidatePaymentRequest_whenProvidedThePaymentDetailsWithAPayAnyone_thenItReturnsAPayReqWithAPayAnyone() {
        PayAnyoneAccountDetailsImpl payAnyone = new PayAnyoneAccountDetailsImpl();
        payAnyone.setAccount("blue73");
        payAnyone.setBsb("999888");
        paymentDetails.setPayAnyoneBeneficiary(payAnyone);
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(paymentDetails, new Date());
        assertThat(payReq.getData().getPayAnyoneBenef(), notNullValue());
        assertThat(payReq.getData().getPayAnyoneBenef().getBenefAcc().getVal(), equalTo("blue73"));
        assertThat(payReq.getData().getPayAnyoneBenef().getBsb().getVal(), equalTo("999888"));
        assertThat(payReq.getData().getBpayBiller(), nullValue());
        assertThat(payReq.getData().getTrxDate(), notNullValue());
    }

    @Test
    public void testBuildValidatePaymentRequest_whenProvidedThePaymentDetailsWithADollarIndexation_thenItReturnsAPayReqWithAFixedAmount() {
        paymentDetails.setIndexationType(IndexationType.DOLLAR);
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(paymentDetails, new Date());
        assertThat(payReq.getData().getStord().getPensFixedAmt().getVal(), equalTo(new BigDecimal("10")));
        assertThat(payReq.getData().getStord().getPensFixedPct(), nullValue());
        assertThat(payReq.getData().getTrxDate(), notNullValue());
    }

    @Test
    public void testBuildValidatePaymentRequest_whenProvidedThePaymentDetailsWithADollarIndexation_thenItReturnsAPayReqWithAFixedPercent() {
        paymentDetails.setIndexationType(IndexationType.PERCENTAGE);
        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(paymentDetails, new Date());
        assertThat(payReq.getData().getStord().getPensFixedAmt(), nullValue());
        assertThat(payReq.getData().getStord().getPensFixedPct().getVal(), equalTo(new BigDecimal("10")));
        assertThat(payReq.getData().getTrxDate(), notNullValue());
    }

    @Test
    public void testBuildSubmitPaymentRequest_whenProvidedThePaymentDetails_thenItReturnsAPayReqWithAnExecAction() {
        paymentDetails.setClientIp("10.0.0.1");
        paymentDetails.setBusinessChannel("1");
        PayReq payReq = AvaloqPayReqBuilder.buildSubmitPaymentPayRequest(paymentDetails, new Date());
        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getData().getTrxDate(), notNullValue());
        assertThat(payReq.getData().getDevId().getVal(), equalTo("10.0.0.1"));
        assertThat(payReq.getData().getChannel(), notNullValue());
    }

    @Test
    public void testBuildStopPaymentRequest_whenProvidedThePaymentDetails_thenItReturnsAPayReqWithACancelExecAction() {
        paymentDetails.setPositionId("564789");
        PayReq payReq = AvaloqPayReqBuilder.buildStopPaymentPayRequest(paymentDetails);
        assertThat(payReq.getReq().getValid(), nullValue());
        assertNotNull(payReq.getData().getPos());
        assertNull(payReq.getData().getStord());
        assertThat(payReq.getData().getPos().getVal(), equalTo("564789"));
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), equalTo(Constants.CANCEL));
    }

    //Saved payments domain object
    private static PaymentDetailsImpl paymentDetailsSaveAction() {
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
        paymentDetails.setAccountKey(AccountKey.valueOf("1234555"));
        return paymentDetails;
    }

    //Regular pension payment test cases

    //Saved new regular pension payment
    @Test
    public void testBuildSavePaymentRequest_NewRegular() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SAVE_NEW_REGULAR);
        savePaymentDetails.setPensionPaymentType(PensionPaymentType.MINIMUM_AMOUNT);

        PayReq payReq = AvaloqPayReqBuilder.buildSavePaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertNotNull(payReq.getData().getStord().getStordPeriodStart().getVal());
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("stord_opn_stord_hold"));
        assertThat(payReq.getReq().getExec().getDoc(), nullValue());
        assertThat(payReq.getReq().getExec().getTransSeqNr(), nullValue());

    }

    //Saved new regular pension payment editing existing active
    @Test
    public void testBuildSavePaymentRequest_NewRegularEditingActive() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SAVE_NEW_REGULAR);
        savePaymentDetails.setPositionId("123485");

        PayReq payReq = AvaloqPayReqBuilder.buildSavePaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getPos().getVal(), equalTo("123485"));
        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("stord_opn_stord_hold"));
        assertThat(payReq.getReq().getExec().getDoc(), nullValue());
        assertThat(payReq.getReq().getExec().getTransSeqNr(), nullValue());
    }

    //Edit existing Saved regular pension payment
    @Test
    public void testBuildSavePaymentRequest_EditSavedRegular() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SAVE_REGULAR);
        savePaymentDetails.setPositionId("123485");
        savePaymentDetails.setDocId("6500515");
        savePaymentDetails.setTransactionSeqNo("3");

        PayReq payReq = AvaloqPayReqBuilder.buildSavePaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getPos().getVal(), equalTo("123485"));
        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("stord_veri_stord_hold"));
        assertThat(payReq.getReq().getExec().getDoc().getVal(), equalTo(new BigDecimal("6500515")));
        assertThat(payReq.getReq().getExec().getTransSeqNr().getVal(), equalTo(new BigDecimal("3")));

    }

    //Validate existing Saved regular pension payment
    @Test
    public void testBuildSavePaymentRequest_ValidSavedRegular() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SUBMIT_REGULAR);
        savePaymentDetails.setPositionId("123485");
        savePaymentDetails.setDocId("650519");
        savePaymentDetails.setTransactionSeqNo("1");

        PayReq payReq = AvaloqPayReqBuilder.buildValidatePaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getPos().getVal(), equalTo("123485"));
        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getExec(), nullValue());
        assertThat(payReq.getReq().getValid(), notNullValue());
        assertThat(payReq.getReq().getValid().getAction(), notNullValue());
        assertThat(payReq.getReq().getValid().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getValid().getAction().getWfcAction(), equalTo("stord_hold_stord_veri"));
        assertThat(payReq.getReq().getValid().getDoc().getVal(), equalTo(new BigDecimal("650519")));
        assertThat(payReq.getReq().getValid().getTransSeqNr().getVal(), equalTo(new BigDecimal("1")));

    }

    //Submit existing Saved regular pension payment
    @Test
    public void testBuildSavePaymentRequest_SubmitSavedRegular() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SUBMIT_REGULAR);
        savePaymentDetails.setPositionId("123485");
        savePaymentDetails.setDocId("650519");
        savePaymentDetails.setTransactionSeqNo("1");

        PayReq payReq = AvaloqPayReqBuilder.buildSubmitPaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getPos().getVal(), equalTo("123485"));
        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("stord_hold_stord_veri"));
        assertThat(payReq.getReq().getExec().getDoc().getVal(), equalTo(new BigDecimal("650519")));
        assertThat(payReq.getReq().getExec().getTransSeqNr().getVal(), equalTo(new BigDecimal("1")));

    }

    //Cancel existing Saved regular pension payment
    @Test
    public void testBuildStopSavedPaymentRequest() {
        PaymentDetailsImpl savePaymentDetaits = paymentDetailsSaveAction();
        savePaymentDetaits.setPaymentAction(PaymentActionType.CANCEL_REGULAR);
        savePaymentDetaits.setDocId("650515");
        savePaymentDetaits.setTransactionSeqNo("2");
        savePaymentDetaits.setWithdrawalType(WithdrawalType.REGULAR_PENSION_PAYMENT);

        PayReq payReq = AvaloqPayReqBuilder.buildStopPaymentPayRequest(savePaymentDetaits);
        assertNull(payReq.getData().getPos());
        assertNotNull(payReq.getData().getUiPayTypeId().getExtlVal());
        assertThat(payReq.getData().getUiPayTypeId().getExtlVal().getVal(), equalTo("reg"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("stord_veri_discd"));
        assertThat(payReq.getReq().getExec().getDoc().getVal(), equalTo(new BigDecimal("650515")));
        assertThat(payReq.getReq().getExec().getTransSeqNr().getVal(), equalTo(new BigDecimal("2")));
    }

    //ONEOFF pension payment test cases

    //Save new oneoff pension payment
    @Test
    public void testBuildSavePaymentRequest_NewOneoff() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SAVE_ONEOFF);

        PayReq payReq = AvaloqPayReqBuilder.buildSavePaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("opn_hold_pay"));
        assertThat(payReq.getReq().getExec().getDoc(), nullValue());
        assertThat(payReq.getReq().getExec().getTransSeqNr(), nullValue());
    }

    //Edit existing Saved oneoff pension payment
    @Test
    public void testBuildSavePaymentRequest_EditOneoff() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.MODIFY_ONEOFF);
        savePaymentDetails.setDocId("550515");
        savePaymentDetails.setTransactionSeqNo("1");

        PayReq payReq = AvaloqPayReqBuilder.buildSavePaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("hold_pay_hold_pay"));
        assertThat(payReq.getReq().getExec().getDoc().getVal(), equalTo(new BigDecimal("550515")));
        assertThat(payReq.getReq().getExec().getTransSeqNr().getVal(), equalTo(new BigDecimal("1")));
    }


    //Submit existing Saved oneoff pension payment
    @Test
    public void testBuildSavePaymentRequest_SubmitOneoff() {
        PaymentDetailsImpl savePaymentDetails = paymentDetailsSaveAction();
        savePaymentDetails.setWithdrawalType(WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        savePaymentDetails.setPaymentAction(PaymentActionType.SUBMIT_ONEOFF);
        savePaymentDetails.setDocId("5505153");
        savePaymentDetails.setTransactionSeqNo("3");

        PayReq payReq = AvaloqPayReqBuilder.buildSubmitPaymentPayRequest(savePaymentDetails, new Date());

        assertThat(payReq.getData().getDebMacc().getVal(), equalTo("red45"));
        assertThat(payReq.getData().getAmount().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(payReq.getData().getBenefInfo().getVal(), equalTo("here is cash"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("hold_pay_prcd"));
        assertThat(payReq.getReq().getExec().getDoc().getVal(), equalTo(new BigDecimal("5505153")));
        assertThat(payReq.getReq().getExec().getTransSeqNr().getVal(), equalTo(new BigDecimal("3")));
    }

    //Cancel existing Saved oneoff pension payment
    @Test
    public void testBuildStopSavedPaymentRequest_CancelOneoff() {
        PaymentDetailsImpl savePaymentDetaits = paymentDetailsSaveAction();
        savePaymentDetaits.setPaymentAction(PaymentActionType.CANCEL_ONEOFF);
        savePaymentDetaits.setDocId("650517");
        savePaymentDetaits.setTransactionSeqNo("4");
        savePaymentDetaits.setWithdrawalType(WithdrawalType.PENSION_ONE_OFF_PAYMENT);
        PayReq payReq = AvaloqPayReqBuilder.buildStopPaymentPayRequest(savePaymentDetaits);
        assertNull(payReq.getData().getPos());
        assertNotNull(payReq.getData().getUiPayTypeId().getExtlVal());
        assertThat(payReq.getData().getUiPayTypeId().getExtlVal().getVal(), equalTo("one_off"));
        assertThat(payReq.getReq().getValid(), nullValue());
        assertThat(payReq.getReq().getExec(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction(), notNullValue());
        assertThat(payReq.getReq().getExec().getAction().getGenericAction(), nullValue());
        assertThat(payReq.getReq().getExec().getAction().getWfcAction(), equalTo("hold_pay_discd"));
        assertThat(payReq.getReq().getExec().getDoc().getVal(), equalTo(new BigDecimal("650517")));
        assertThat(payReq.getReq().getExec().getTransSeqNr().getVal(), equalTo(new BigDecimal("4")));
    }
}
