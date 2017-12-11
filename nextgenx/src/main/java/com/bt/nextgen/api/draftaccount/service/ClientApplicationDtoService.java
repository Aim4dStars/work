package com.bt.nextgen.api.draftaccount.service;


import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.dto.DeleteDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SimulateDraftDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface ClientApplicationDtoService extends FindAllDtoService<ClientApplicationDto>, CreateDtoService<ClientApplicationKey,
        ClientApplicationDto>,UpdateDtoService<ClientApplicationKey,ClientApplicationDto>,
        DeleteDtoService<ClientApplicationKey, ClientApplicationDto>,
        FindByKeyDtoService<ClientApplicationKey, ClientApplicationDto>,
        SimulateDraftDtoService<ClientApplicationKey, ClientApplicationDto>
{
    @Override
    ClientApplicationDto create(ClientApplicationDto dto, ServiceErrors serviceErrors);

    @Override
    List findAll(ServiceErrors serviceErrors);

    @Override
    ClientApplicationDto update(ClientApplicationDto keyedObject, ServiceErrors serviceErrors);

    @Override
    void delete(ClientApplicationKey key, ServiceErrors serviceErrors);

    @Override
    ClientApplicationDto find(ClientApplicationKey key, ServiceErrors serviceErrors);

    @Override
    ClientApplicationDto simulateDraftAccount(ClientApplicationKey key, ServiceErrors serviceErrors);

}
