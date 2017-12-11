package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.KeyedDto;

public interface KeyDtoService <K, T extends KeyedDto <K>> extends DtoService <T>
{

}
