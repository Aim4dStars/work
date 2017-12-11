package com.bt.nextgen.api.drawdown.v2.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;

import java.util.List;

public interface DrawdownErrorMapper extends ErrorMapper {

    public List<ValidationError> mapWarnings(List<DomainApiErrorDto> errors);
}
