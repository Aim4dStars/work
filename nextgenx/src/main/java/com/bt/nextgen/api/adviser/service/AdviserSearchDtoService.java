package com.bt.nextgen.api.adviser.service;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.model.AdviserSearchDtoKey;
import com.bt.nextgen.api.adviser.model.SingleAdviserForUserDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.service.integration.broker.BrokerKey;

public interface AdviserSearchDtoService extends SearchByKeyDtoService <AdviserSearchDtoKey, AdviserSearchDto>, SearchByCriteriaDtoService<AdviserSearchDto>
{

}
