package com.bt.nextgen.api.account.v1.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("WrapAccountDetailsDtoErrorMapperV1")
public class WrapAccountDetailsDtoErrorMapperImpl implements WrapAccountDetailsDtoErrorMapper
{

	@Override
	public List <DomainApiErrorDto> map(List <ValidationError> errors)
	{
        List<DomainApiErrorDto> apiErrors = new ArrayList<>();
		if (errors == null)
		{
            return apiErrors;
		}

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

			apiErrors.add(new DomainApiErrorDto(domain, null, error.getMessage(), error.isError()
				? ErrorType.ERROR
				: ErrorType.WARNING));
		}
		return apiErrors;
	}

}
