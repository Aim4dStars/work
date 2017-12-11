package com.bt.nextgen.api.client.v2.service;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

public interface ClientListDtoService extends FindAllDtoService<ClientIdentificationDto>,
		SearchByCriteriaDtoService<ClientIdentificationDto>, FindByKeyDtoService<ClientKey, ClientIdentificationDto>,
		UpdateDtoService<ClientKey, ClientIdentificationDto>, FilterableDtoService<ClientIdentificationDto> {

}
