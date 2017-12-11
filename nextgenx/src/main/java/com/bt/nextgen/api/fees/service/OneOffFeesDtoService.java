package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.fees.model.OneOffFeesDto;
import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.integration.account.AccountKey;

/** 
 *  OneOffAdviceFeesDtoService: Interface will have the method declaration(s) needed for
 * getting 'advice fees' from Service .
 *
 */
public interface OneOffFeesDtoService extends FindByKeyDtoService<AccountKey, OneOffFeesDto>,CreateDtoService <AccountKey, OneOffFeesDto>,
	ValidateDtoService <AccountKey, OneOffFeesDto>
{

}
