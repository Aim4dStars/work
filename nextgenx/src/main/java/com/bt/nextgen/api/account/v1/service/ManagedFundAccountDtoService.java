package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountAssetKey;
import com.bt.nextgen.api.account.v1.model.ManagedFundAccountDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;

import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface ManagedFundAccountDtoService extends UpdateDtoService<AccountAssetKey, ManagedFundAccountDto>,
        FindByKeyDtoService<AccountAssetKey, ManagedFundAccountDto>,
        SearchByKeyDtoService<AccountAssetKey, ManagedFundAccountDto>
{
    public List<DistributionMethod> getAvailableDistributionMethod(WrapAccountDetail account, Asset asset);
}
