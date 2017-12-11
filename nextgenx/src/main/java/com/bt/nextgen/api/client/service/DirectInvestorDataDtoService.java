package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * Interface for service to retrieve direct investor details from GCM
 * It takes a list of string delimited data types
 */
public interface DirectInvestorDataDtoService extends FindByKeyDtoService<ClientUpdateKey, CustomerDataDto> {
}
