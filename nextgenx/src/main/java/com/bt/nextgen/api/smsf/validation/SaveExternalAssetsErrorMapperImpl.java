package com.bt.nextgen.api.smsf.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationFormatter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * smsf external assets save submit error mapper
 */
@Service
public class SaveExternalAssetsErrorMapperImpl implements SaveExternalAssetsErrorMapper {

    @Override
    public List <DomainApiErrorDto> map(List <ValidationError> errors)
    {
        if (errors == null)
        {
            return new ArrayList<>();
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

    @Override
    public List <ValidationError> mapWarnings(List <DomainApiErrorDto> errors)
    {
        if (errors == null)
        {
            return new ArrayList<>();
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
