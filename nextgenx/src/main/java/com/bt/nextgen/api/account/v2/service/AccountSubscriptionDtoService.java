package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v2.model.InitialInvestmentAssetDto;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.InitialInvestmentAsset;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;

import java.util.List;
import java.util.Map;

@Deprecated
public interface AccountSubscriptionDtoService extends UpdateDtoService<AccountKey, AccountSubscriptionDto> {

    String getSubscriptionType(List<ProductSubscription> subscriptions, Map<ProductKey, Product> productsMap);

    List<InitialInvestmentAssetDto> getInitialInvestments(List<InitialInvestmentAsset> initialInvestmentAssets, ServiceErrors serviceErrors);
}
