package com.bt.nextgen.api.userpreference.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;

import java.util.List;

public interface UserPreferenceDtoErrorMapper extends ErrorMapper {
    public List<ValidationError> mapWarnings(List<DomainApiErrorDto> errors);
}
