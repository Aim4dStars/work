package com.bt.nextgen.api.inspecietransfer.v2.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;

import java.util.List;

/**
 * @deprecated Use V3
 */
@Deprecated
public interface InspecieTransferDtoErrorMapper extends ErrorMapper {
    public List<ValidationError> mapWarnings(List<DomainApiErrorDto> errors);
}
