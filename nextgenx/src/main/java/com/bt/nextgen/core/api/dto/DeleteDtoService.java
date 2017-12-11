package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceErrors;

public interface DeleteDtoService <K, T extends KeyedDto <K>> extends KeyDtoService <K, T>
{
	void delete(K key, ServiceErrors serviceErrors);
}
