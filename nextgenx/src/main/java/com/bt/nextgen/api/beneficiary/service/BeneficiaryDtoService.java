package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Service interface to view/save beneficiary details.
 * Created by M035995 on 11/07/2016.
 */
public interface BeneficiaryDtoService extends SearchByCriteriaDtoService<BeneficiaryDto> {

    /**
     * This method retrieves the beneficiary details for an account
     *
     * @param accountKey    object of {@link AccountKey}
     * @param serviceErrors object of {@link ServiceErrors}
     * @param mode          Whether the beneficiary details are from CACHE
     *
     * @return object of {@link BeneficiaryDto}
     */
    public List<BeneficiaryDto> getBeneficiaryDetails(AccountKey accountKey, ServiceErrors serviceErrors, String mode);

    /**
     * This method retrieves the beneficiary details for a list of account Ids which belong to the intermediary
     *
     * @param brokerId      broker Id of support staff
     * @param serviceErrors object of {@link ServiceErrors}
     * @param mode          Whether the beneficiary details are from CACHE
     *
     * @return List of {@link BeneficiaryDto}
     */
    public List<BeneficiaryDto> getBeneficiaryDetails(String brokerId, ServiceErrors serviceErrors, String mode);
}
