package com.bt.nextgen.service.avaloq.movemoney;

import java.math.BigDecimal;

import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import org.junit.Test;

import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class AvaloqBpReqBuilderTest {

    private PaymentDetailsImpl paymentDetails = MovemoneyDataUtil.paymentDetails();

    @Test
    public void testBuildValidatePaymentBpRequest_whenProvidedThePaymentDetails_thenItReturnsABpReqWithAValidAction() {
        PayAnyoneAccountDetailsImpl payAnyone = new PayAnyoneAccountDetailsImpl();
        payAnyone.setAccount("blue73");
        payAnyone.setBsb("999888");
        paymentDetails.setPayAnyoneBeneficiary(payAnyone);
        paymentDetails.setModificationSeq("1");
        BpReq bpReq = AvaloqBpReqBuilder.buildValidatePaymentBpRequest(paymentDetails);
        assertThat(bpReq.getData().getCltOnbDet().getPensPayAmt().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(bpReq.getReq().getValid(), notNullValue());
        assertThat(bpReq.getReq().getExec(), nullValue());
    }

    @Test
    public void testBuildSubmitPaymentBpRequest_whenProvidedThePaymentDetails_thenItReturnsABpReqWithAnExecAction() {
        PayAnyoneAccountDetailsImpl payAnyone = new PayAnyoneAccountDetailsImpl();
        payAnyone.setAccount("blue73");
        payAnyone.setBsb("999888");
        paymentDetails.setPayAnyoneBeneficiary(payAnyone);
        paymentDetails.setModificationSeq("1");
        BpReq bpReq = AvaloqBpReqBuilder.buildSubmitPaymentBpRequest(paymentDetails);
        assertThat(bpReq.getData().getCltOnbDet().getPensPayAmt().getVal(), equalTo(new BigDecimal("4134")));
        assertThat(bpReq.getReq().getValid(), nullValue());
        assertThat(bpReq.getReq().getExec(), notNullValue());
        assertThat(bpReq.getData().getModiSeqNr().getVal(), equalTo(BigDecimal.ONE));
    }
}
