package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.FundEstablishmentDto;
import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.service.ServiceErrors;

public interface ClientApplicationDetailsDtoService extends FindOneDtoService<ClientApplicationDetailsDto> {

    ClientApplicationDetailsDto findByClientApplicationId(Long draftAccountId, ServiceErrors serviceErrors);

    ClientApplicationDetailsDto findByAccountNumber(String accountNumber, ServiceErrors serviceErrors);

    FundEstablishmentDto findFundEstablishmentStatusByClientApplicationId(Long draftAccountId, String accountNumber, ServiceErrors serviceErrors);
}
