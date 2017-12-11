package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.policy.model.AccountPolicyDto;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;

/**
 * Interface to return related accounts of the owner of the current policy
 */
public interface PolicyAccountsDtoService extends SearchByKeyDtoService<AccountKey, AccountPolicyDto> {
}
