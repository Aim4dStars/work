package com.bt.nextgen.core.api.dto;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface FindOneDtoService<T> extends DtoService <T>
{
	T findOne(ServiceErrors serviceErrors);
}
