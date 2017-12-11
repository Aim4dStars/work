package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ANZSICCodeDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.service.ServiceErrors;

import java.io.IOException;
import java.util.List;

public interface ANZSICCodeDtoService extends FindAllDtoService<ANZSICCodeDto> {

    List<ANZSICCodeDto> findAll(ServiceErrors serviceErrors);

}
