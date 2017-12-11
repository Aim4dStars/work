package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceErrors;

public interface UpdateDtoService <K, T extends KeyedDto <K>> extends KeyDtoService <K, T>
{
	T update(T keyedObject, ServiceErrors serviceErrors);
}
