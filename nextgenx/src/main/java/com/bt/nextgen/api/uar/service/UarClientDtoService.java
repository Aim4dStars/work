package com.bt.nextgen.api.uar.service;

import com.bt.nextgen.api.uar.model.UarDetailsDto;
import com.bt.nextgen.api.uar.model.UarDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.service.integration.user.UserKey;

/**
 * Created by L081012 on 16/09/2016.
 */
public interface UarClientDtoService extends SearchByCriteriaDtoService<UarDetailsDto>, SubmitDtoService<UserKey, UarDetailsDto> {

}
