package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationApprovalDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

public interface ClientApplicationApprovalDtoService extends SubmitDtoService<OnboardingApplicationKey, ClientApplicationApprovalDto> {

    @Override
    ClientApplicationApprovalDto submit(ClientApplicationApprovalDto keyedObject, ServiceErrors serviceErrors);

}
