package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.movemoney.PaymentActionType;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.btfin.abs.trxservice.bp.v1_0.BpReq;
import com.btfin.abs.trxservice.bp.v1_0.BpRsp;
import com.btfin.abs.trxservice.pay.v1_0.PayReq;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqPaymentIntegrationServiceTest {

    @InjectMocks
    AvaloqPaymentIntegrationService service;

    @Mock
    AvaloqGatewayHelperService webServiceClient;

    @Mock
    BankDateIntegrationService bankDateIntegrationService;

    @Mock
    PaymentDetailsBuilder paymentBuilder;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    PaymentDetailsImpl paymentDetails = MovemoneyDataUtil.paymentDetails();

    PayReq payReq;
    PayRsp payRsp = MovemoneyDataUtil.payRsp();

    BpReq bpReq;
    BpRsp bpRsp = MovemoneyDataUtil.bpRsp();

    ServiceErrors serviceErrors = new ServiceErrorsImpl();

    PensionAccountDetailImpl commencedPension = new PensionAccountDetailImpl();

    PensionAccountDetailImpl nonCommencedPension = new PensionAccountDetailImpl();

    WrapAccountDetailImpl nonPension = new WrapAccountDetailImpl();

    @Before
    public void setup() {
        commencedPension.setCommenceDate(new DateTime());
        nonCommencedPension.setModificationSeq("10");
        paymentDetails.setModificationSeq("10");

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(new DateTime());
        Mockito.when(webServiceClient.sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class))).thenReturn(payRsp);
        Mockito.when(paymentBuilder.buildPaymentDetails(Mockito.any(PayRsp.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDetails);
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(commencedPension);
        Mockito.when(webServiceClient.sendToWebService(Mockito.any(BpReq.class), Mockito.eq(AvaloqOperation.BP_REQ),
                Mockito.any(ServiceErrors.class))).thenReturn(bpRsp);
        Mockito.when(paymentBuilder.buildPaymentDetails(Mockito.any(BpRsp.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(paymentDetails);
    }

    @Test
    public void testValidatePayment_whenCalledForCommencedPension_thenItSendsAPayReqeust() {
        PaymentDetails paymentDetails = service.validatePayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testValidatePayment_whenCalledForNonCommencedPension_thenItSendsABpReqeust() {
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(nonCommencedPension);
        PaymentDetails paymentDetails = service.validatePayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(BpReq.class), Mockito.eq(AvaloqOperation.BP_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testSubmitPayment_whenCalledForCommencedPension_thenItSendsAPayReqeust() {
        PaymentDetails paymentDetails = service.submitPayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testSubmitPayment_whenCalledForNonCommencedPension_thenItSendsABpReqeust() {
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(nonCommencedPension);
        PaymentDetails paymentDetails = service.submitPayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(BpReq.class), Mockito.eq(AvaloqOperation.BP_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testValidatePayment_whenCalledForNonPensionPayment_thenItSendsAPayReqeust() {
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(nonPension);
        PaymentDetails paymentDetails = service.validatePayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(webServiceClient, Mockito.never()).sendToWebService(Mockito.any(BpReq.class),
                Mockito.eq(AvaloqOperation.BP_REQ), Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testSubmitPayment_whenCalledForNonPensionPayment_thenItSendsABpReqeust() {
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(nonPension);
        PaymentDetails paymentDetails = service.validatePayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(webServiceClient, Mockito.never()).sendToWebService(Mockito.any(BpReq.class),
                Mockito.eq(AvaloqOperation.BP_REQ), Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testEndPayment() {
        List<ValidationError> warnings = new ArrayList<>();
        warnings.add(new ValidationError("btfg$dd_occurred_close", null, null, ValidationError.ErrorType.WARNING));
        this.paymentDetails.setWarnings(warnings);
        PaymentDetails paymentDetails = service.endPayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
        assertThat((String) paymentDetails.getWarnings().get(0).getErrorId(),
                equalTo(this.paymentDetails.getWarnings().get(0).getErrorId()));
    }

    @Test
    public void testSavedPayment() {
        this.paymentDetails.setPaymentAction(PaymentActionType.SAVE_REGULAR);
        PaymentDetails paymentDetails = service.savePayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

    @Test
    public void testSavedOneOffPayment() {
        this.paymentDetails.setPaymentAction(PaymentActionType.SAVE_ONEOFF);
        PaymentDetails paymentDetails = service.savePayment(this.paymentDetails, serviceErrors);
        Mockito.verify(webServiceClient).sendToWebService(Mockito.any(PayReq.class), Mockito.eq(AvaloqOperation.PAY_REQ),
                Mockito.any(ServiceErrors.class));
        assertThat((PaymentDetailsImpl) paymentDetails, equalTo(this.paymentDetails));
    }

}
