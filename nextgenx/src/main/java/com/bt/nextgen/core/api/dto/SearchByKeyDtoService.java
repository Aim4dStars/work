package com.bt.nextgen.core.api.dto;

import java.util.List;

import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceErrors;

public interface SearchByKeyDtoService <K, T extends KeyedDto <K>> extends FindByKeyDtoService <K, T>
{
	List <T> search(K Key, ServiceErrors serviceErrors);
}
