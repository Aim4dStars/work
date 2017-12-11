package com.bt.nextgen.api.user.v1.service;

import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDto;
import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDtoKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

public interface TermsAndConditionsDtoService
        extends FindAllDtoService<TermsAndConditionsDto>,
        SubmitDtoService<TermsAndConditionsDtoKey, TermsAndConditionsDto> {

}
