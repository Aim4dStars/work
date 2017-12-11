package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TailorMadePortfolioDtoServiceImpl implements TailorMadePortfolioDtoService {

    private static final String TAILOR_MADE_PORTFOLIO = "Tailor Made Portfolio";
    private static final String SUPER_TAILOR_MADE_PORTFOLIO = "Super Tailor Made Portfolio";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Override
    public AssetDto findOne(ServiceErrors serviceErrors) {
        return findTmpCashAssetForModel(ModelType.INVESTMENT, serviceErrors);
    }

    @Override
    public List<AssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        // Only expecting a single criteria
        ModelType modelType = ModelType.INVESTMENT;
        ApiSearchCriteria criteria = criteriaList.get(0);
        if ("modelType".equals(criteria.getProperty())) {
            modelType = ModelType.forCode(criteria.getValue());
        }

        AssetDto dto = findTmpCashAssetForModel(modelType, serviceErrors);
        if (dto != null) {
            return Collections.singletonList(dto);
        }
        return null;
    }

    protected AssetDto findTmpCashAssetForModel(ModelType modelType, ServiceErrors serviceErrors) {

        // Retrieve Cash only asset.
        Collection<AssetType> assetTypes = Collections.singletonList(AssetType.CASH);
        Map<String, Asset> assetMap = assetService.loadAssetsForCriteria((Collection<String>) Collections.<String> emptyList(),
                Constants.EMPTY_STRING, assetTypes, serviceErrors);

        if (!assetMap.isEmpty()) {
            for (Map.Entry<String, Asset> entry : assetMap.entrySet()) {
                Asset asset = entry.getValue();
                if (ModelType.INVESTMENT == modelType && TAILOR_MADE_PORTFOLIO.equals(asset.getMoneyAccountType())) {
                    AssetDto dto = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().name());
                    dto.setAssetCode(UploadAssetCodeEnum.TMP_CASH.value());
                    return dto;
                }
                if (ModelType.SUPERANNUATION == modelType && SUPER_TAILOR_MADE_PORTFOLIO.equals(asset.getMoneyAccountType())) {
                    AssetDto dto = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().name());
                    dto.setAssetCode(UploadAssetCodeEnum.SUPER_TMP_CASH.value());
                    return dto;
                }
            }
        }
        return null;
    }
}
