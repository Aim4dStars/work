package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.ServiceErrors;

public interface ClientListDtoService extends FindAllDtoService<ClientIdentificationDto>,
        SearchByCriteriaDtoService<ClientIdentificationDto>, FindByKeyDtoService<ClientKey, ClientIdentificationDto>,
        UpdateDtoService<ClientKey, ClientIdentificationDto>, FilterableDtoService<ClientIdentificationDto> {

    ClientDto findWithoutRelatedAccounts(ClientKey key, ServiceErrors serviceErrors);

}
