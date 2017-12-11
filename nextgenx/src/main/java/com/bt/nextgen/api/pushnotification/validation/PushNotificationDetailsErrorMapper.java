package com.bt.nextgen.api.pushnotification.validation;

import java.util.List;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;

/**
 * Error mapper for API errors.
 */
public interface PushNotificationDetailsErrorMapper extends ErrorMapper {
    public List<ValidationError> mapWarnings(List<DomainApiErrorDto> errors);
}
