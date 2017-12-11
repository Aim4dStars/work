package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.transitions.TransitionAccountDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

/**
 * Created by L069552 on 16/09/2015.
 */
@Deprecated
public interface TransitionClientDtoService extends SearchByCriteriaDtoService<TransitionAccountDto>,FindAllDtoService<TransitionAccountDto>{

}
