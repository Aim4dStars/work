package com.bt.nextgen.api.registration.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;

import com.bt.nextgen.api.registration.model.RegistrationDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface RegistrationDtoService extends SearchByCriteriaDtoService<RegistrationDto>
{
    /**
     * Take account Id and construct wrapAccount to load account application details and return application investor registration
     *  detail as registration dto.
     * @param accountId  - input parameter account BP id
     * @param serviceErrors - Output parameter for service errors.
     * @return List <RegistrationDto>
     */
    public List<RegistrationDto> getAccountApplicationStatus(String accountId, ServiceErrors serviceErrors);

    /**
     * Will update the term & condition flag for non approver to user repository.
     * @return boolean - if updated successfully then true otherwise false.
     */
    public boolean updateTnCForNonAprrover() ;

}
