/**
 * 
 */
package com.bt.nextgen.service.integration.movemoney;

/**
 * Interface for holding all the details of Deposit Transaction of Recurring Type
 */
public interface RecurringDepositDetails extends DepositDetails, RecurringTransaction {

    public String getPositionId();

}
