package com.bt.nextgen.service.integration.regularinvestment;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;

public interface RegularInvestmentIntegrationService {

    public RegularInvestment loadRegularInvestment(AccountKey accountKey, String ripId, ServiceErrors serviceErrors);

    public RegularInvestment validateRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrors);

    public RegularInvestment submitRegularInvestment(RegularInvestment regularInvestment, String linkedDDRef,
            ServiceErrors serviceErrors);

    public RegularInvestment suspendRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrors);

    public RegularInvestment resumeRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrors);

    /**
     * Cancel an existing regular investment. The status of the existing investment can either be in SUSPENDED or ACTIVE.
     * Depending on which, the underlying action to execute will be different. Note: This does not handle the underlying linked
     * account if any.
     * 
     * @param regularInvestment
     * @param serviceErrors
     */
    public RegularInvestment cancelRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrors);

    public RecurringDepositDetails loadRecurringDeposit(final String linkedDDRef, final ServiceErrors serviceErrors);

    public RegularInvestment saveRegularInvestment(RegularInvestment regularInvestment, String linkedDDRef,
                                                   ServiceErrors serviceErrors);

}
