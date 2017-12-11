package com.bt.nextgen.service.avaloq.beneficiary;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.broker.BrokerKey;

import java.util.List;


/**
 * This interface define methods to view/update/delete beneficiary details
 * Created by M035995 on 9/07/2016.
 */
public interface BeneficiaryDetailsIntegrationService {

    /**
     * This method retrieves beneficiary details for a particular account.
     *
     * @param accountKey    Account Id
     * @param serviceErrors Object of {@link ServiceErrors}
     *
     * @return {@link BeneficiaryDetailsResponseHolderImpl}
     */
    BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(AccountKey accountKey, ServiceErrors serviceErrors);

    /**
     * This method retrieves beneficiary details for a list of accounts
     *
     * @param accountIds    List of Account Id
     * @param serviceErrors Object of {@link ServiceErrors}
     *
     * @return {@link AccountBeneficiaryDetailsResponse}
     */
    BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(List<String> accountIds, ServiceErrors serviceErrors);

    /**
     * This method retrieves beneficiary details for a list of accounts
     *
     * @param brokerKey     BrokerKey  - Oe-id of a broker(adviser)
     * @param serviceErrors Object of {@link ServiceErrors}
     *
     * @return {@link AccountBeneficiaryDetailsResponse}
     */
    BeneficiaryDetailsResponseHolderImpl getBeneficiaryDetails(BrokerKey brokerKey, ServiceErrors serviceErrors);


    /* * Execute Save or update beneficiaries requests.
             *
             * @param benefDetails
     * @return TransactionStatus
     */
    public TransactionStatus saveOrUpdate(SaveBeneficiariesDetails benefDetails);
}
