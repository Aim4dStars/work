package com.bt.nextgen.service.integration.deposit;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;

/**
 * @deprecated Use package com.bt.nextgen.service.integration.movemoney.DepositIntegrationService
 */
@Deprecated
public interface DepositIntegrationService {

    /**
     * Method to return validated deposit response for a Current Day Deposit And Scheduled Deposit request
     * 
     * @param deposit
     *            - Holds all the details of the deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return DepositDetails
     */
    DepositDetails validateDeposit(DepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return submitted deposit response for a Current Day Deposit And Scheduled Deposit request
     * 
     * @param deposit
     *            - Holds all the details of the deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return DepositDetails
     */
    DepositDetails submitDeposit(DepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return validated deposit response for a Recurring deposit request
     * 
     * @param deposit
     *            - Holds all the details of the Recurring deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return RecurringDepositDetails
     */
    RecurringDepositDetails validateDeposit(RecurringDepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return submitted deposit response for a Recurring deposit request
     * 
     * @param deposit
     *            - Holds all the details of the Recurring deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return RecurringDepositDetails
     */
    RecurringDepositDetails submitDeposit(RecurringDepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return Cancellation status for a Stop Transaction request
     * 
     * @param positionIdentifier
     *            - the position id required to cancel the deposit transaction
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return TransactionStatus - boolean within the TransactionStatus tells whether the cancel transaction was successful or
     *         not.
     */
    TransactionStatus stopDeposit(PositionIdentifier positionIdentifier, ServiceErrors serviceErrors);

}
