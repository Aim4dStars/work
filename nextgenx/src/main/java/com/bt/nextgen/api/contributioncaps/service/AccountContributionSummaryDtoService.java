package com.bt.nextgen.api.contributioncaps.service;


import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.contributioncaps.model.AccountContributionSummaryDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface AccountContributionSummaryDtoService extends SearchByCriteriaDtoService<MemberContributionCapValuationDto>
{

}