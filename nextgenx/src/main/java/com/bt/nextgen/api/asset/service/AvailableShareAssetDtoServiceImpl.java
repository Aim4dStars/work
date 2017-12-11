package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvailableShareAssetDtoServiceImpl implements AvailableShareAssetDtoService {
    private static final Logger logger = LoggerFactory.getLogger(AvailableShareAssetDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Override
    public List<AssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<AssetDto> assetDtoList = new ArrayList<>();
        String query = "";
        String assetType = null;

        for (ApiSearchCriteria apiSearchCriteria : criteriaList) {
            if ("query".equals(apiSearchCriteria.getProperty())) {
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append(apiSearchCriteria.getValue());
                query = query.concat(queryBuilder.toString());
                logger.info("Query String is : {}", query);
            } else {
                assetType = apiSearchCriteria.getValue();
            }
        }
        Collection<AssetType> assetTypes = resolveAssetType(assetType);
        Map<String, Asset> shareMap = assetService.loadShareAssetsForCriteria(query, assetTypes, serviceErrors);

        assetDtoList.addAll(filterAssets(shareMap));
        logger.info("Share list size is  : {}; filtered share list size is  : {}", shareMap.size(), assetDtoList.size());
        return assetDtoList;
    }

    public Set<AssetDto> filterAssets(final Map<String, Asset> shareMap) {
        Set<AssetDto> filteredAssets = new HashSet<>();
        for (Asset asset : shareMap.values()) {
            if (asset.getIsin() != null && AssetStatus.TERMINATED != asset.getStatus()
                    && AssetStatus.DELISTED != asset.getStatus()
                    && AssetStatus.SUSPENDED != asset.getStatus()) {
                AssetDto assetDto = new AssetDto(asset, asset.getAssetName(), asset.getAssetType().toString());
                assetDto.setAssetCode(asset.getAssetCode());
                filteredAssets.add(assetDto);
            }
        }

        return filteredAssets;
    }

    /**
     * Method Can be expanded if we want to search based on other asset types as well.
     * 
     * @param assetType
     * @return
     */
    private Collection<AssetType> resolveAssetType(String assetType) {
        switch (assetType.toUpperCase()) {
            case "SHARE":
                Collection<AssetType> assetTypes = new ArrayList<>();
                assetTypes.add(AssetType.SHARE);
                return assetTypes;
            default:
                return Collections.singleton(AssetType.OTHER);

        }
    }
}
