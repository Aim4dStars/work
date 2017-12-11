package com.bt.nextgen.api.order.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("OrderGroupDtoErrorMapperV0.1")
public class OrderGroupDtoErrorMapperImpl implements OrderGroupDtoErrorMapper
{
	@Override
	public List <DomainApiErrorDto> map(List <ValidationError> errors)
	{
		if (errors == null)
		{
			return null;
		}

		List <DomainApiErrorDto> apiErrors = new ArrayList <>();

		for (ValidationError error : errors)
		{
			apiErrors.add(new DomainApiErrorDto(error.getErrorId(), error.getField(), null, error.getMessage(), error.isError()
				? ErrorType.ERROR
				: ErrorType.WARNING));
		}

		return apiErrors;
	}

	@Override
	public List <ValidationError> mapWarnings(List <DomainApiErrorDto> errors)
	{
		if (errors == null)
		{
			return null;
		}

		List <ValidationError> validationErrors = new ArrayList <>();

		for (DomainApiErrorDto error : errors)
		{
			validationErrors.add(new ValidationError(error.getErrorId(),
				error.getDomain(),
				error.getMessage(),
				DomainApiErrorDto.ErrorType.ERROR.toString().equals(error.getErrorType())
					? ValidationError.ErrorType.ERROR
					: ValidationError.ErrorType.WARNING));
		}

		return validationErrors;
	}
}
