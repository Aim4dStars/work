package com.bt.nextgen.api.client.v2.service;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface ClientSearchDtoService extends SearchByCriteriaDtoService<ClientIdentificationDto>, FilterableDtoService<ClientIdentificationDto> {

}
