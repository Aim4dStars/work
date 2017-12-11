package com.bt.nextgen.api.corporateaction.v1.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.validation.ValidationError;


@Service
public class CorporateActionAccountDetailsDtoErrorMapperImpl implements CorporateActionAccountDetailsDtoErrorMapper
{

	@Override
	public List <DomainApiErrorDto> map(List <ValidationError> errors)
	{
		if (errors == null || errors.isEmpty())
		{
			return Collections.emptyList();
		}

		List <DomainApiErrorDto> validationErrors = new ArrayList <DomainApiErrorDto>();

		for (ValidationError error : errors)
		{

			String domain = null;

			if (error.getField() != null)
			{
				//TODO: Need to update once error codes and types will be defined 
			}
			else
			{
				// should be a page level error
				domain = error.getField();
			}

			validationErrors.add(new DomainApiErrorDto(domain, null, error.getMessage(), error.isError()
				? ErrorType.ERROR
				: ErrorType.WARNING));
		}

		return validationErrors;
	}

}
