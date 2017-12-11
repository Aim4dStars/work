package com.bt.nextgen.api.draftaccount.service;


import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationWithdrawalDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;


public interface ClientApplicationWithdrawalDtoService extends SubmitDtoService<ClientApplicationKey, ClientApplicationWithdrawalDto> {

    @Override
    ClientApplicationWithdrawalDto submit(ClientApplicationWithdrawalDto keyedObject, ServiceErrors serviceErrors);

}
