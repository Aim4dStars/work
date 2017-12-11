package com.bt.nextgen.api.corporateaction.v1.service;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;

public interface CorporateActionCommonService {

    CorporateActionClientAccountDetails loadClientAccountDetails(CorporateActionContext context, List<CorporateActionAccount> accounts,
                                                                 ServiceErrors serviceErrors);

    BigDecimal getAssetPrice(Asset asset, ServiceErrors serviceErrors);

    UserProfileService getUserProfileService();
}
