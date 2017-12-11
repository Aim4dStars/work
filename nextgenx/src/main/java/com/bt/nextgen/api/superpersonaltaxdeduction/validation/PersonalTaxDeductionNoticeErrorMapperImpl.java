package com.bt.nextgen.api.superpersonaltaxdeduction.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for {@link PersonalTaxDeductionNoticeErrorMapper}.
 */
@Component
public class PersonalTaxDeductionNoticeErrorMapperImpl implements PersonalTaxDeductionNoticeErrorMapper {
    @Override
    public List<DomainApiErrorDto> map(List<ValidationError> errors) {
        final List<DomainApiErrorDto> apiErrors = new ArrayList<>();

        if (errors == null) {
            return apiErrors;
        }

        for (ValidationError error : errors) {
            apiErrors.add(new DomainApiErrorDto(error.getErrorId(), error.getField(), null, error.getMessage(), error.isError()
                    ? DomainApiErrorDto.ErrorType.ERROR
                    : DomainApiErrorDto.ErrorType.WARNING));
        }

        return apiErrors;
    }
}
