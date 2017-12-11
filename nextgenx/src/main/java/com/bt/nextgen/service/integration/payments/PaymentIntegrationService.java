package com.bt.nextgen.service.integration.payments;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.TransactionStatus;

/**
 * Interface for Payment services
 * 
 */
@Deprecated
public interface PaymentIntegrationService
{

	/**
	 * Method to return validated payment response for a Payment request
	 * @param payment - Holds all the details of the payment
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return PaymentDetails
	 * */
	PaymentDetails validatePayment(PaymentDetails payment, ServiceErrors serviceErrors);

	/**
	 * Method to return submitted payment response for a Payment request
	 * @param payment - Holds all the details of the payment
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return PaymentDetails
	 * */
	PaymentDetails submitPayment(PaymentDetails payment, ServiceErrors serviceErrors);

	/**
	 * Method to return validated payment response for a Recurring Payment request
	 * @param payment - Holds all the details of the payment
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return PaymentDetails
	 * */
	RecurringPaymentDetails validatePayment(RecurringPaymentDetails payment, ServiceErrors serviceErrors);

	/**
	 * Method to return submitted payment response for a Recurring Payment request
	 * @param payment - Holds all the details of the payment
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return PaymentDetails
	 * */
	RecurringPaymentDetails submitPayment(RecurringPaymentDetails payment, ServiceErrors serviceErrors);

	/**
	 * Method to return stop payment response for a Payment request
	 * @param payment - Holds all the details of the payment
	 * @param serviceErrors - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
	 * @return TransactionStatus - status of stop transaction
	 * */
	TransactionStatus stopPayment(PaymentDetails payment, ServiceErrors serviceErrors);
}
