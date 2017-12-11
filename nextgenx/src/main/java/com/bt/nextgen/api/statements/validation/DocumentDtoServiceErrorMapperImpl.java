package com.bt.nextgen.api.statements.validation;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentDtoServiceErrorMapperImpl implements DocumentDtoServiceErrorMapper {

    @Override
    public List<DomainApiErrorDto> map(List<ValidationError> errors) {
        if (errors == null){
            return new ArrayList<>();
        }
        List <DomainApiErrorDto> apiErrors = new ArrayList<>();
        for (ValidationError error : errors){
            String domain = null;
            if (error.getField() != null){
                //TODO: Need to update once error codes and types will be defined
            }
            else{
                // should be a page level error
                domain = error.getField();
            }
            apiErrors.add(new DomainApiErrorDto(domain, null, error.getMessage(), error.isError()
                    ? DomainApiErrorDto.ErrorType.ERROR
                    : DomainApiErrorDto.ErrorType.WARNING));
        }
        return apiErrors;
    }
}