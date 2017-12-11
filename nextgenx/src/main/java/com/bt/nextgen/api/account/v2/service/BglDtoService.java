package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.BglDataDto;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface BglDtoService extends FindByKeyDtoService <DateRangeAccountKey, BglDataDto>
{

}
