package com.bt.nextgen.api.supermatch.v1.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.ServiceError;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SuperMatchErrorMapper {

    public List<DomainApiErrorDto> map(Iterable<ServiceError> errors) {
        final List<DomainApiErrorDto> domainErrors = new ArrayList<>();
        for (ServiceError error : errors) {
            domainErrors.add(new DomainApiErrorDto(error.getErrorCode(), null, error.getReason(),
                    error.getErrorMessageForScreenDisplay(), DomainApiErrorDto.ErrorType.ERROR));
        }
        return domainErrors;
    }
}
