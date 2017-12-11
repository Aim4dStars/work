package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;

import java.util.List;

/**
 * This interface is a wrapper for {@link BeneficiaryDetails}
 * Created by M035995 on 8/07/2016.
 */
public interface AccountBeneficiaryDetailsResponse {

    /**
     * Retrieves the account Id of the account
     * @return
     */
    AccountKey getAccountKey();
    /**
     * This method retrieves beneficiary details for a particular account.
     *
     * @return List of {@link BeneficiaryDetails}
     */
    List<BeneficiaryDetails> getBeneficiaryDetails();

    /**
     * This method retrieves the last updated date time for beneficiaries.
     *
     * @return Last updated Date Time.
     */
    DateTime getLastUpdatedDate();

    /**
     * This method retrieves the auto-reversionary beneficiaries activation date.
     *
     * @return AutoReversionaryActivationDate.
     */
    DateTime getAutoReversionaryActivationDate();
}
