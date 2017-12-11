package com.bt.nextgen.api.tracking.service;


import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.model.OnboardingClientStatusDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface OnboardingClientService extends FindByKeyDtoService<ClientApplicationKey, OnboardingClientStatusDto> {

}
