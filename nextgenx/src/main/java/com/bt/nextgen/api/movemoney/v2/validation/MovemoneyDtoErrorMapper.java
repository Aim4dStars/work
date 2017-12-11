package com.bt.nextgen.api.movemoney.v2.validation;

import java.util.List;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;

public interface MovemoneyDtoErrorMapper extends ErrorMapper
{
	public List <ValidationError> mapWarnings(List <DomainApiErrorDto> errors);
}
