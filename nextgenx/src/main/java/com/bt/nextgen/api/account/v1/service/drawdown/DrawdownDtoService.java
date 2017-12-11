package com.bt.nextgen.api.account.v1.service.drawdown;

import com.bt.nextgen.api.account.v1.model.DrawdownDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface DrawdownDtoService extends FindByKeyDtoService<AccountKey, DrawdownDto>,
        UpdateDtoService<AccountKey, DrawdownDto> {

}