package com.bt.nextgen.api.account.v1.util;

import com.bt.nextgen.api.account.v1.model.transitions.TransitionAssetDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.TransitionSettlementsHolder;
import com.bt.nextgen.service.integration.account.TransitionSettlementsIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by L069552 on 5/10/2015.
 */
@Deprecated
public class AssetTransferFilterUtil {

    private AccountIntegrationService accountService;

    private ProductIntegrationService productIntegrationService;

    private TransitionSettlementsIntegrationService transitionSettlementsIntegrationService;

    private AssetIntegrationService assetIntegrationService;


    /**
     *
     * @param accountIntegrationService
     * @param productIntegrationService
     */
    public AssetTransferFilterUtil(AccountIntegrationService accountIntegrationService,ProductIntegrationService
            productIntegrationService,TransitionSettlementsIntegrationService transitionSettlementsIntegrationService,AssetIntegrationService assetIntegrationService){

        this.accountService = accountIntegrationService;
        this.productIntegrationService = productIntegrationService;
        this.transitionSettlementsIntegrationService = transitionSettlementsIntegrationService;
        this.assetIntegrationService = assetIntegrationService;

    }

    /**
     *
     * @param criteriaList
     * @param serviceErrors
     * @return
     * TODO :Currently returns hard coded values for Transfer Status Parameters. Will be populated with actual service values .
     */
    public List<TransitionAssetDto>findAll(final List<ApiSearchCriteria> criteriaList,final ServiceErrors serviceErrors) {

        String accountId = criteriaList.get(0).getValue();
        WrapAccount wrapAccount = null;
        Product product = null;
        Map<String, Asset> assetMap = new HashMap<>();

        TransitionSettlementsHolder transitionSettlementsHolder = transitionSettlementsIntegrationService.getAssetTransferStatus(com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(accountId)),serviceErrors);

        wrapAccount = accountService.loadWrapAccountDetail(com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(accountId)), serviceErrors);

        product =  productIntegrationService.loadProductsMap(serviceErrors) != null ? productIntegrationService.loadProductsMap(serviceErrors).get(wrapAccount.getProductKey()) : null;


        if(transitionSettlementsHolder != null && transitionSettlementsHolder.getTransitionSettlements() != null && !transitionSettlementsHolder.getTransitionSettlements().isEmpty()){

            assetMap = assetIntegrationService.loadExternalAssets(serviceErrors);
        }

        return AssetTransferDtoConverter.fetchAssetTransferDtos(new AccountKey(accountId),wrapAccount, product,assetMap,transitionSettlementsHolder);
    }

}



