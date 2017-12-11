package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceErrors;

public interface FindByPartialKeyDtoService <K, T extends KeyedDto <K>> extends KeyDtoService <K, T>
{
	T find(K key, ServiceErrors serviceErrors);
}
