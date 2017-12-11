package com.bt.nextgen.api.smsf.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationFormatter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class AccountingSoftwareErrorMapperImpl implements AccountingSoftwareErrorMapper {
    @Override
    public List<DomainApiErrorDto> map(List<ValidationError> errors) {
        if (errors == null) {
            return new ArrayList<>();
        }

        List<DomainApiErrorDto> apiErrors = new ArrayList<>();

        for (ValidationError error : errors) {
            apiErrors.add(new DomainApiErrorDto(error.getErrorId(), error.getField(), null, error.getMessage(), error.isError()
                    ? DomainApiErrorDto.ErrorType.ERROR
                    : DomainApiErrorDto.ErrorType.WARNING));
        }

        return apiErrors;
    }
}
