package com.bt.nextgen.api.supermatch.v1.service;

import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.core.api.dto.FindByPartialKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Service for Super match
 */
public interface SuperMatchDtoService extends FindByPartialKeyDtoService<SuperMatchDtoKey, SuperMatchDto>, UpdateDtoService<SuperMatchDtoKey, SuperMatchDto> {

    /**
     * Triggers a request to send the SG(Super Guarantee) letter to the user
     *
     * @param accountId    - Encoded account identifier
     * @param emailAddress - email address to send the SG letter to
     */
    boolean notifyCustomer(String accountId, String emailAddress, ServiceErrors serviceErrors);
}
