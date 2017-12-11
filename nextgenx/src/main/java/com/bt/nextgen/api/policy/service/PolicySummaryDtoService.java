package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.policy.model.PolicyTrackingDto;
import com.bt.nextgen.core.api.dto.*;

public interface PolicySummaryDtoService extends FindByKeyDtoService<BrokerKey, PolicyTrackingDto>,
        SearchByCriteriaDtoService<PolicyTrackingDto>, FindOneDtoService<PolicyTrackingDto> {
}
