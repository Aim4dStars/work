package com.bt.nextgen.api.advisermodel.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
public class AdviserModelDtoServiceImpl implements AdviserModelDtoService {

    private static final String ADVISER_MODEL_CASH_TYPE = "Generic Model Cash";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Override
    public AssetDto findOne(ServiceErrors serviceErrors) {
        // Load generic cash asset for creation of all adviser models
        Collection<AssetType> assetTypes = Collections.singletonList(AssetType.CASH);
        Map<String, Asset> assetMap = assetService.loadAssetsForCriteria(Collections.<String> emptyList(), "", assetTypes,
                serviceErrors);

        for (Map.Entry<String, Asset> entry : assetMap.entrySet()) {
            Asset asset = entry.getValue();
            if (ADVISER_MODEL_CASH_TYPE.equals(asset.getMoneyAccountType())) {
                AssetDto dto = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().name());
                dto.setAssetCode(UploadAssetCodeEnum.ADVISER_MODEL_CASH.value());
                return dto;
            }
        }

        throw new NotFoundException("Adviser model cash asset not found.");
    }
}
