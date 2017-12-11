package com.bt.nextgen.api.draftaccount.service;


import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationSubmitDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;


public interface ClientApplicationSubmitDtoService extends SubmitDtoService<ClientApplicationKey, ClientApplicationSubmitDto> {

    @Override
    ClientApplicationSubmitDto submit(ClientApplicationSubmitDto keyedObject, ServiceErrors serviceErrors);

}
