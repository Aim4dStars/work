package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.HoldingApplicationDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

public interface HoldingApplicationDtoService extends FindByKeyDtoService<AccountKey, HoldingApplicationDto> {
}
