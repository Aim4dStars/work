package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.PolicyDto;
import com.bt.nextgen.api.policy.model.PolicyKey;
import com.bt.nextgen.core.api.dto.*;

public interface PolicyDtoService extends SearchByKeyDtoService<PolicyKey, PolicyDto>, FindByPartialKeyDtoService<PolicyKey, PolicyDto> {
}
