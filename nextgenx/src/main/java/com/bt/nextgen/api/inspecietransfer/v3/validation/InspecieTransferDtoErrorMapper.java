package com.bt.nextgen.api.inspecietransfer.v3.validation;

import java.util.List;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;

public interface InspecieTransferDtoErrorMapper extends ErrorMapper {
    public List<ValidationError> mapWarnings(List<DomainApiErrorDto> errors);
}
