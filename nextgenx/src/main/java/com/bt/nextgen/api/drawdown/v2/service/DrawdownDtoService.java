package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

public interface DrawdownDtoService extends FindByKeyDtoService<AccountKey, DrawdownDetailsDto>,
        UpdateDtoService<AccountKey, DrawdownDetailsDto> {

}