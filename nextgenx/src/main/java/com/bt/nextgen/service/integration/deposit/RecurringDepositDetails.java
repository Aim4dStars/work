/**
 * 
 */
package com.bt.nextgen.service.integration.deposit;

import com.bt.nextgen.service.integration.RecurringTransaction;

/**
 * @deprecated Use package com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails
 */
@Deprecated
public interface RecurringDepositDetails extends DepositDetails, RecurringTransaction {
    public String getPositionId();

    public void setPositionId(String posId);
}
