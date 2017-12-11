package com.bt.nextgen.core.api.dto;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;

public interface FindAllDtoService <T> extends DtoService <T>
{
	List <T> findAll(ServiceErrors serviceErrors);
}
