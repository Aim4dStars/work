package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountAssetKey;
import com.bt.nextgen.api.account.v2.model.DistributionAccountDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;

import java.util.List;

@Deprecated
public interface DistributionAccountDtoService extends UpdateDtoService<AccountAssetKey, DistributionAccountDto>,
        FindByKeyDtoService<AccountAssetKey, DistributionAccountDto>,
        SearchByKeyDtoService<AccountAssetKey, DistributionAccountDto> {
    public List<DistributionMethod> getAvailableDistributionMethod(Asset asset);
}
