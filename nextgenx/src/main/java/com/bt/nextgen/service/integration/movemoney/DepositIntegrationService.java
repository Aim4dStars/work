package com.bt.nextgen.service.integration.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import java.util.List;

/**
 * Interface for Deposit Transaction
 */
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

    /**
     * * Method to return created deposit response for a Current Day Deposit And Scheduled Deposit request
     * 
     * @param deposit
     *            - Holds all the details of the deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return DepositDetails
     */
    DepositDetails createDeposit(DepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return created deposit response for a Recurring deposit request
     * 
     * @param deposit
     *            - Holds all the details of the Recurring deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return RecurringDepositDetails
     */
    RecurringDepositDetails createDeposit(RecurringDepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return updated deposit response for a Current Day Deposit And Scheduled Deposit request
     * 
     * @param deposit
     *            - Holds all the details of the deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return DepositDetails
     */
    DepositDetails updateDeposit(DepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to return updated deposit response for a Recurring deposit request
     * 
     * @param deposit
     *            - Holds all the details of the Recurring deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the valuation from the end point.
     * @return RecurringDepositDetails
     */
    RecurringDepositDetails updateDeposit(RecurringDepositDetails deposit, ServiceErrors serviceErrors);

    /**
     * Method to delete a saved deposit
     * 
     * @param depositId
     *            - The unique identifier of the deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during deleting of the deposit.
     */
    void deleteDeposit(String depositId, ServiceErrors serviceErrors);

    /**
     * Method to delete a saved recurring deposit
     * 
     * @param depositId
     *            - The unique identifier of the deposit
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during deleting of the deposit.
     */
    void deleteRecurringDeposit(String depositId, ServiceErrors serviceErrors);

    /**
     * Loads the saved deposits for a single account id.
     * 
     * @param accountId
     *            - plain text avaloq id of the account to load deposits for.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the saved deposits from the end point.
     * @return The collection of saved deposits for an account, if no saved deposits for the account are found then an empty list
     *         is returned.
     */
    List<DepositDetails> loadSavedDeposits(WrapAccountIdentifier identifier, ServiceErrors serviceErrors);

    /**
     * Loads the saved deposit for a single deposit id.
     * 
     * @param depositId
     *            - plain text avaloq id of the deposit id to load deposit for.
     * @param serviceErrors
     *            - Output parameter, Stores all errors encountered during loading of the saved deposit from the end point.
     * @return The collection of saved deposits, if no saved deposits are found then an empty list is returned.
     */
    DepositDetails loadSavedDeposit(String depositId, ServiceErrors serviceErrors);
}
