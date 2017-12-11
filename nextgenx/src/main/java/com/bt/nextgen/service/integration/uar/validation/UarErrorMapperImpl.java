package com.bt.nextgen.service.integration.uar.validation;

import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by l081361 on 22/07/2016.
 */
@Service
public class UarErrorMapperImpl implements UarErrorMapper
{
    @Override
    public List<DomainApiErrorDto> map(List <ValidationError> errors)
    {
        if (errors == null)
        {
            return null;
        }

        List <DomainApiErrorDto> apiErrors = new ArrayList<>();

        for (ValidationError error : errors)
        {
            apiErrors.add(new DomainApiErrorDto(error.getErrorId(), error.getField(), null, error.getMessage(), error.isError()
                    ? DomainApiErrorDto.ErrorType.ERROR
                    : DomainApiErrorDto.ErrorType.WARNING));
        }

        return apiErrors;
    }

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
