package com.bt.nextgen.service.integration.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.TransactionStatus;

/**
 * Interface for Payment services
 * 
 */
public interface PaymentIntegrationService {

    /**
     * Validates a payment.
     * 
     * @param payment
     *            - Holds all the details of the payment
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return PaymentDetails
     */
    PaymentDetails validatePayment(PaymentDetails payment, ServiceErrors serviceErrors);

    /**
     * Submits a payment.
     * 
     * @param payment
     *            - Holds all the details of the payment
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return PaymentDetails
     */
    PaymentDetails submitPayment(PaymentDetails payment, ServiceErrors serviceErrors);

    /**
     * Stops a payment.
     * 
     * @param payment
     *            - Holds all the details of the payment
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return TransactionStatus - status of stop transaction
     */
    TransactionStatus stopPayment(PaymentDetails payment, ServiceErrors serviceErrors);

    /**
     * End a payment.
     * 
     * @param payment
     *            - Holds all the details of the payment
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return PaymentDetails - receipt id
     */
    PaymentDetails endPayment(PaymentDetails payment, ServiceErrors serviceErrors);

    /**
     * Save's a payment.
     *
     * @param payment
     *            - Holds all the details of the payment
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return PaymentDetails
     */
    PaymentDetails savePayment(PaymentDetails payment, ServiceErrors serviceErrors);
}
