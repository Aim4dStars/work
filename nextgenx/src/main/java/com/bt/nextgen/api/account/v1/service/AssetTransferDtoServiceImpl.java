package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.transitions.TransitionAssetDto;
import com.bt.nextgen.api.account.v1.util.AssetTransferFilterUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.TransitionSettlementsIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by L069552 on 28/09/2015.
 */
@Deprecated
@Service("assetTransferService")
public class AssetTransferDtoServiceImpl implements AssetTransferService{

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private TransitionSettlementsIntegrationService transitionSettlementsIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;



    @Override
    public List<TransitionAssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        AssetTransferFilterUtil transitionFilterUtil = new AssetTransferFilterUtil(accountService,productIntegrationService,transitionSettlementsIntegrationService,assetIntegrationService);

        List<TransitionAssetDto> transitionAssetDtos = transitionFilterUtil.findAll(criteriaList,serviceErrors);

        return transitionAssetDtos;

    }
}
