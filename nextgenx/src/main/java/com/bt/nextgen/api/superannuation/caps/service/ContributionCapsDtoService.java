package com.bt.nextgen.api.superannuation.caps.service;


import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.superannuation.caps.model.ContributionCapDto;
import com.bt.nextgen.api.superannuation.caps.model.SuperAccountContributionCapsDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;

public interface ContributionCapsDtoService extends SearchByKeyedCriteriaDtoService<AccountKey, SuperAccountContributionCapsDto>
{
}
