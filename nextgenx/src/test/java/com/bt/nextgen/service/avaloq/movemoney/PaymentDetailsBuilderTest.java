package com.bt.nextgen.service.avaloq.movemoney;

import java.math.BigDecimal;

import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import com.btfin.abs.trxservice.pay.v1_0.BpayBiller;
import com.btfin.abs.trxservice.pay.v1_0.PayAnyoneBenef;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class PaymentDetailsBuilderTest {

    @InjectMocks
    PaymentDetailsBuilder builder;

    @Mock
    StaticIntegrationService staticIntegrationService;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();

    private PayRsp payRsp = MovemoneyDataUtil.payRsp();

    private BpRsp bpRsp = MovemoneyDataUtil.bpRsp();

    @Before
    public void setup() {
        Code percentageIndexationCode = new CodeImpl();
        ((CodeImpl) percentageIndexationCode).setIntlId("fixed_pct");
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE),
                Mockito.eq("fixed_pct"), Mockito.any(ServiceErrors.class))).thenReturn(percentageIndexationCode);
        Code dollarIndexationCode = new CodeImpl();
        ((CodeImpl) dollarIndexationCode).setIntlId("fixed_amount");
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.PENSION_PAYMENT_OR_INDEXATION_TYPE),
                Mockito.eq("fixed_amount"), Mockito.any(ServiceErrors.class))).thenReturn(dollarIndexationCode);
        Code paymentFrequencyCode = new CodeImpl();
        ((CodeImpl) paymentFrequencyCode).setIntlId("rm");
        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.CODES_PAYMENT_FREQUENCIES),
                Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(paymentFrequencyCode);
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRsp_thenItReturnsPaymentDetails() {
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails, notNullValue());
        assertThat(paymentDetails.getAmount(), equalTo(new BigDecimal("4134")));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithDollarIndexation_thenItReturnsPaymentDetailsWithDollarIndexation() {
        payRsp.getData().getStord().setPensIdxMtdId(AvaloqGatewayUtil.createIdVal(IndexationType.DOLLAR.getIntlId()));
        payRsp.getData().getStord().setPensFixedAmt(AvaloqGatewayUtil.createNumberVal(new BigDecimal("10")));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails.getIndexationType(), equalTo(IndexationType.DOLLAR));
        assertThat(paymentDetails.getIndexationAmount(), equalTo(new BigDecimal("10")));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithPercentIndexation_thenItReturnsPaymentDetailsWithPercentIndexation() {
        payRsp.getData().getStord().setPensIdxMtdId(AvaloqGatewayUtil.createIdVal(IndexationType.PERCENTAGE.getIntlId()));
        payRsp.getData().getStord().setPensFixedPct(AvaloqGatewayUtil.createNumberVal(new BigDecimal("5.6")));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails.getIndexationType(), equalTo(IndexationType.PERCENTAGE));
        assertThat(paymentDetails.getIndexationAmount(), equalTo(new BigDecimal("5.6")));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithBPayBiller_thenItReturnsPaymentDetailsWithBPayBiller() {
        BpayBiller bpayBiller = new BpayBiller();
        bpayBiller.setBillerCode(AvaloqGatewayUtil.createTextVal("orange11"));
        bpayBiller.setCrn(AvaloqGatewayUtil.createTextVal("ref1112"));
        payRsp.getData().setBpayBiller(bpayBiller);
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails.getBpayBiller(), notNullValue());
        assertThat(paymentDetails.getBpayBiller().getBillerCode(), equalTo("orange11"));
        assertThat(paymentDetails.getBpayBiller().getCustomerReferenceNo(), equalTo("ref1112"));
        assertThat(paymentDetails.getPayAnyoneBeneficiary(), nullValue());
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithPayAnyone_thenItReturnsPaymentDetailsWithPayAnyone() {
        PayAnyoneBenef payAnyone = new PayAnyoneBenef();
        payAnyone.setBenefAcc(AvaloqGatewayUtil.createTextVal("orange11"));
        payAnyone.setBsb(AvaloqGatewayUtil.createTextVal("ref1112"));
        payRsp.getData().setPayAnyoneBenef(payAnyone);
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails.getBpayBiller(), nullValue());
        assertThat(paymentDetails.getPayAnyoneBeneficiary(), notNullValue());
        assertThat(paymentDetails.getPayAnyoneBeneficiary().getAccount(), equalTo("orange11"));
        assertThat(paymentDetails.getPayAnyoneBeneficiary().getBsb(), equalTo("ref1112"));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithAmount_thenItReturnsPaymentDetailsWithAmount() {
        payRsp.getData().setAmount(AvaloqGatewayUtil.createNumberVal(BigDecimal.TEN));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails.getAmount(), equalTo(BigDecimal.TEN));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithNilAmount_thenItReturnsPaymentDetailsWithoutAnAmount() {
        payRsp.getData().setAmount(AvaloqGatewayUtil.createNumberVal((BigDecimal) null));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertNull(paymentDetails.getAmount());
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithNoAmountField_thenItReturnsPaymentDetailsWithoutAnAmount() {
        payRsp.getData().setAmount(null);
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertNull(paymentDetails.getAmount());
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithAccountAndBsb_thenItReturnsPaymentDetailsWithPayAnyone() {
        bpRsp.getData().getCltOnbDet().setAccNr(AvaloqGatewayUtil.createTextVal("orange11"));
        bpRsp.getData().getCltOnbDet().setBsb(AvaloqGatewayUtil.createTextVal("ref1112"));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertThat(paymentDetails.getBpayBiller(), nullValue());
        assertThat(paymentDetails.getPayAnyoneBeneficiary(), notNullValue());
        assertThat(paymentDetails.getPayAnyoneBeneficiary().getAccount(), equalTo("orange11"));
        assertThat(paymentDetails.getPayAnyoneBeneficiary().getBsb(), equalTo("ref1112"));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithDollarIndexation_thenItReturnsPaymentDetailsWithDollarIndexation() {
        bpRsp.getData().getCltOnbDet().setPensIdxMtdId(AvaloqGatewayUtil.createIdVal(IndexationType.DOLLAR.getIntlId()));
        bpRsp.getData().getCltOnbDet().setPensIdxFixAmt(AvaloqGatewayUtil.createNumberVal(new BigDecimal("10")));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertThat(paymentDetails.getIndexationType(), equalTo(IndexationType.DOLLAR));
        assertThat(paymentDetails.getIndexationAmount(), equalTo(new BigDecimal("10")));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithPercentIndexation_thenItReturnsPaymentDetailsWithPercentIndexation() {
        bpRsp.getData().getCltOnbDet().setPensIdxMtdId(AvaloqGatewayUtil.createIdVal(IndexationType.PERCENTAGE.getIntlId()));
        bpRsp.getData().getCltOnbDet().setPensIdxFixPct(AvaloqGatewayUtil.createNumberVal(new BigDecimal("5.6")));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertThat(paymentDetails.getIndexationType(), equalTo(IndexationType.PERCENTAGE));
        assertThat(paymentDetails.getIndexationAmount(), equalTo(new BigDecimal("5.6")));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedThePayRspWithAReceiptNumber_thenItReturnsPaymentDetailsWithAReceiptNumber() {
        PaymentDetails paymentDetails = builder.buildPaymentDetails(payRsp, serviceErrors);
        assertThat(paymentDetails, notNullValue());
        assertThat(paymentDetails.getReceiptNumber(), equalTo("999991"));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithAmount_thenItReturnsPaymentDetailsWithAmount() {
        bpRsp.getData().getCltOnbDet().setPensPayAmt(AvaloqGatewayUtil.createNumberVal(BigDecimal.TEN));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertThat(paymentDetails.getAmount(), equalTo(BigDecimal.TEN));
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithNilAmount_thenItReturnsPaymentDetailsWithoutAnAmount() {
        bpRsp.getData().getCltOnbDet().setPensPayAmt(AvaloqGatewayUtil.createNumberVal((BigDecimal) null));
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertNull(paymentDetails.getAmount());
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithNoAmountField_thenItReturnsPaymentDetailsWithoutAnAmount() {
        bpRsp.getData().getCltOnbDet().setPensPayAmt(null);
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertNull(paymentDetails.getAmount());
    }

    @Test
    public void testBuildPaymentDetails_whenProvidedTheBpRspWithAReceiptNumber_thenItReturnsPaymentDetailsWithAReceiptNumber() {
        PaymentDetails paymentDetails = builder.buildPaymentDetails(bpRsp, serviceErrors);
        assertThat(paymentDetails, notNullValue());
        assertThat(paymentDetails.getReceiptNumber(), equalTo("999992"));
    }
}
