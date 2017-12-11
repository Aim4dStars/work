package com.bt.nextgen.api.draftaccount.service;


import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

public interface DirectOnboardingDtoService extends
        SubmitDtoService<ClientApplicationKey, ClientApplicationDto>{
}
