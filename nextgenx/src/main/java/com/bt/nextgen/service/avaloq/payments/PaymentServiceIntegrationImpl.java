package com.bt.nextgen.service.avaloq.payments;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.payments.PaymentConverter;
import com.bt.nextgen.service.integration.payments.PaymentDetails;
import com.bt.nextgen.service.integration.payments.PaymentIntegrationService;
import com.bt.nextgen.service.integration.payments.RecurringPaymentDetails;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Deprecated
@Service("DeprecatedPaymentServiceIntegrationImpl")
public class PaymentServiceIntegrationImpl extends AbstractAvaloqIntegrationService implements PaymentIntegrationService {

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceIntegrationImpl.class);

    /******
     * This method will validate the Payment through Avaloq Service <b>PAY_REQ</b> .
     * 
     * @param payment
     * @return PaymentDetails
     */

    @Override
    public PaymentDetails validatePayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<PaymentDetails>("validatePayment", serviceErrors) {
            @Override
            public PaymentDetails performOperation() {
                boolean isFuture = payment.getTransactionDate()
                        .after(bankDateIntegrationService.getBankDate(serviceErrors).toDate());
                PayRsp payRsp = webserviceClient.sendToWebService(
                        PaymentConverter.toValidatePaymentRequest(payment, isFuture, serviceErrors), AvaloqOperation.PAY_REQ,
                        serviceErrors);
                PaymentDetails paymentResponse = PaymentConverter.toValidatePaymentResponse(payment, payRsp, serviceErrors);

                logger.debug("Validated the paymentId");
                return paymentResponse;
            }
        }.run();
    }

    /******
     * This method will submit the Payment through Avaloq Service <b>PAY_REQ</b> .
     * 
     * @param payment
     * @return PaymentDetails
     */

    @Override
    public PaymentDetails submitPayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<PaymentDetails>("submitPayment", serviceErrors) {
            @Override
            public PaymentDetails performOperation() {
                boolean isFuture = payment.getTransactionDate()
                        .after(bankDateIntegrationService.getBankDate(serviceErrors).toDate());
                PayRsp payRsp = webserviceClient.sendToWebService(
                        PaymentConverter.toSubmitPaymentRequest(payment, isFuture, serviceErrors), AvaloqOperation.PAY_REQ,
                        serviceErrors);

                PaymentDetails paymentResponse = PaymentConverter.toSubmitPaymentResponse(payment, payRsp, serviceErrors);

                logger.debug("Payment Successful with paymentId: {}", paymentResponse.getReceiptNumber());
                return paymentResponse;

            }
        }.run();
    }

    /******
     * This method will validate the Recurring Payment through Avaloq Service <b>PAY_REQ</b> .
     * 
     * @param payment
     * @return PaymentDetails
     */

    @Override
    public RecurringPaymentDetails validatePayment(final RecurringPaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RecurringPaymentDetails>("validatePayment", serviceErrors) {
            @Override
            public RecurringPaymentDetails performOperation() {

                PayRsp payRsp = webserviceClient.sendToWebService(
                        PaymentConverter.toValidateRecurringPaymentRequest(payment, serviceErrors), AvaloqOperation.PAY_REQ,
                        serviceErrors);
                RecurringPaymentDetails paymentResponse = PaymentConverter.toValidateRecurringPaymentResponse(payment, payRsp,
                        serviceErrors);

                logger.debug("Validated the paymentId");
                return paymentResponse;
            }
        }.run();
    }

    /******
     * This method will submit the Recurring Payment through Avaloq Service <b>PAY_REQ</b> .
     * 
     * @param payment
     * @return PaymentDetails
     */

    @Override
    public RecurringPaymentDetails submitPayment(final RecurringPaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RecurringPaymentDetails>("submitPayment", serviceErrors) {
            @Override
            public RecurringPaymentDetails performOperation() {

                PayRsp payRsp = webserviceClient.sendToWebService(
                        PaymentConverter.toSubmitRecurringPaymentRequest(payment, serviceErrors), AvaloqOperation.PAY_REQ,
                        serviceErrors);

                RecurringPaymentDetails paymentResponse = PaymentConverter.toSubmitRecurringPaymentResponse(payment, payRsp,
                        serviceErrors);

                logger.debug("Payment Successful with paymentId: {}", paymentResponse.getReceiptNumber());
                return paymentResponse;

            }
        }.run();
    }

    /******
     * This method will stop the Payment through Avaloq Service <b>PAY_REQ</b> .
     * 
     * @param positionId
     * @param serviceErrors
     * @return TransactionInterface
     */
    @Override
    public TransactionStatus stopPayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransactionStatus>("stopPayment", serviceErrors) {
            @Override
            public TransactionStatus performOperation() {
                PayRsp payRsp = webserviceClient.sendToWebService(PaymentConverter.toStopPaymentRequest(payment, serviceErrors),
                        AvaloqOperation.PAY_REQ, serviceErrors);
                logger.debug("Payment has been stoppped for the position id: {}", payment.getPositionId());
                return PaymentConverter.toStopPaymentResponse(payRsp, serviceErrors);
            }
        }.run();
    }
}
