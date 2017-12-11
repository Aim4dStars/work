package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PaymentIntegrationService;
import com.btfin.abs.trxservice.pay.v1_0.PayRsp;
import com.btfin.panorama.avaloq.jaxb.BaseResponse;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AvaloqPaymentIntegrationService extends AbstractAvaloqIntegrationService implements PaymentIntegrationService {

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private PaymentDetailsBuilder paymentBuilder;

    private static final Logger logger = LoggerFactory.getLogger(AvaloqPaymentIntegrationService.class);

    /******
     * This method will validate the Payment through Avaloq Service <b>PAY_REQ</b> .
     *
     * @param payment
     *
     * @return PaymentDetails
     */
    @Override
    public PaymentDetails validatePayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<PaymentDetails>("validatePayment", serviceErrors) {
            @Override
            public PaymentDetails performOperation() {
                BaseResponse response;
                if (isNonCommencedPensionPayment(payment, serviceErrors)) {
                    response = webserviceClient.sendToWebService(AvaloqBpReqBuilder.buildValidatePaymentBpRequest(payment),
                            AvaloqOperation.BP_REQ, serviceErrors);
                }
                else {
                    Date bankDate = bankDateIntegrationService.getBankDate(serviceErrors).toDate();
                    response = webserviceClient.sendToWebService(
                            AvaloqPayReqBuilder.buildValidatePaymentPayRequest(payment, bankDate), AvaloqOperation.PAY_REQ,
                            serviceErrors);
                }
                return paymentBuilder.buildPaymentDetails(response, serviceErrors);

            }

        }.run();
    }

    /******
     * This method will submit the Payment through Avaloq Service <b>PAY_REQ</b> .
     *
     * @param payment
     *
     * @return PaymentDetails
     */

    @Override
    public PaymentDetails submitPayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<PaymentDetails>("submitPayment", serviceErrors) {
            @Override
            public PaymentDetails performOperation() {
                BaseResponse response;
                if (isNonCommencedPensionPayment(payment, serviceErrors)) {
                    response = webserviceClient.sendToWebService(AvaloqBpReqBuilder.buildSubmitPaymentBpRequest(payment),
                            AvaloqOperation.BP_REQ, serviceErrors);
                }
                else {
                    Date bankDate = bankDateIntegrationService.getBankDate(serviceErrors).toDate();
                    logger.info("Bank date for payments is: {}", bankDate);
                    logger.info("Payment transaction date: {}", payment.getTransactionDate());
                    response = webserviceClient.sendToWebService(
                            AvaloqPayReqBuilder.buildSubmitPaymentPayRequest(payment, bankDate), AvaloqOperation.PAY_REQ,
                            serviceErrors);
                }
                return paymentBuilder.buildPaymentDetails(response, serviceErrors);
            }
        }.run();
    }

    private boolean isNonCommencedPensionPayment(PaymentDetails payment, ServiceErrors serviceErrors) {
        WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(payment.getAccountKey(), serviceErrors);
        return account instanceof PensionAccountDetail && ((PensionAccountDetail) account).getCommenceDate() == null;
    }

    /******
     * This method will stop the Payment through Avaloq Service <b>PAY_REQ</b> .
     *
     * @param positionId
     * @param serviceErrors
     *
     * @return TransactionInterface
     */
    @Override
    public TransactionStatus stopPayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<TransactionStatus>("stopPayment", serviceErrors) {
            @Override
            public TransactionStatus performOperation() {
                PayRsp payRsp = webserviceClient.sendToWebService(AvaloqPayReqBuilder.buildStopPaymentPayRequest(payment),
                        AvaloqOperation.PAY_REQ, serviceErrors);
                logger.debug("Payment has been stoppped for the position id: {}", payment.getPositionId());
                return paymentBuilder.toStopPaymentResponse(payRsp);
            }
        }.run();
    }

    @Override
    public PaymentDetails endPayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<PaymentDetails>("endPayment", serviceErrors) {
            @Override
            public PaymentDetails performOperation() {
                PayRsp payRsp = webserviceClient.sendToWebService(AvaloqPayReqBuilder.buildStopPaymentPayRequest(payment),
                        AvaloqOperation.PAY_REQ, serviceErrors);
                return paymentBuilder.buildPaymentDetails(payRsp, serviceErrors);
            }
        }.run();
    }

    /******
     * This method will save the Payment through Avaloq Service <b>PAY_REQ</b> .
     *
     * @param payment
     *
     * @return PaymentDetails
     */
    @Override
    public PaymentDetails savePayment(final PaymentDetails payment, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<PaymentDetails>("savePayment", serviceErrors) {
            @Override
            public PaymentDetails performOperation() {
                BaseResponse response;
                Date bankDate = bankDateIntegrationService.getBankDate(serviceErrors).toDate();
                response = webserviceClient.sendToWebService(
                        AvaloqPayReqBuilder.buildSavePaymentPayRequest(payment, bankDate), AvaloqOperation.PAY_REQ,
                        serviceErrors);
                return paymentBuilder.buildPaymentDetails(response, serviceErrors);

            }

        }.run();
    }

}
