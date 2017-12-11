package com.bt.nextgen.api.rollover.v1.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("RolloverDtoErrorMapperImplV1")
public class RolloverDtoErrorMapperImpl implements ErrorMapper {

    @Override
    public List<DomainApiErrorDto> map(List<ValidationError> errors) {
        if (errors == null) {
            return Collections.emptyList();
        }

        List<DomainApiErrorDto> apiErrors = new ArrayList<>();

        for (ValidationError error : errors) {
            apiErrors.add(new DomainApiErrorDto(error.getErrorId(), error.getField(), null, error.getMessage(),
                    error.isError() ? ErrorType.ERROR : ErrorType.WARNING));
        }

        return apiErrors;
    }
}
