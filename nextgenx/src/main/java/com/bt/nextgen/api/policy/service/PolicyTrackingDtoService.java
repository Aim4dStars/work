package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.model.CustomerKey;
import com.bt.nextgen.api.policy.model.PolicyTrackingIdentifier;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;

public interface PolicyTrackingDtoService extends SearchByCriteriaDtoService<PolicyTrackingIdentifier>,
        SearchByKeyedCriteriaDtoService<CustomerKey, PolicyTrackingIdentifier> {
}
