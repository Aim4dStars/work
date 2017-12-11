package com.bt.nextgen.core.api.validation;

import java.util.List;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;

public interface ErrorMapper
{
	List <DomainApiErrorDto> map(List <ValidationError> errors);
}
