package com.bt.nextgen.api.smsf.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationFormatter;

import java.util.List;

/**
 * smsf external assets save submit error mapper
 */
public interface SaveExternalAssetsErrorMapper extends ErrorMapper {

    public List<ValidationError> mapWarnings(List <DomainApiErrorDto> errors);

}
