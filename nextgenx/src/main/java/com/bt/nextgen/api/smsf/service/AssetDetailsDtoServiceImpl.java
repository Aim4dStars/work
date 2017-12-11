package com.bt.nextgen.api.smsf.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.api.smsf.model.AssetDto;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Dto service for asset code name retrieval
 */
@Service
public class AssetDetailsDtoServiceImpl implements AssetDetailsDtoService {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Override
    /**
     * Searches assets based on the criteria list
     * @param criteriaList - criterialist to filter the assettype, should be the intl Id
     * @param serviceErrors - serviceErrors to hold errors
     * @return - returns list of assetDto
     */
    public List<AssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        AssetType assetType = null;

        if (!criteriaList.isEmpty()) {
            for (ApiSearchCriteria parameter : criteriaList) {
                if (Attribute.ASSETTYPEINTLID.equals(parameter.getProperty())) {
                    assetType = AssetType.getByCode(parameter.getValue());
                }
                else {
                    throw new IllegalArgumentException("Unsupported search");
                }
            }

        }

        final Map<String, Asset> results = assetIntegrationService.loadExternalAssets(serviceErrors);

        final List<Asset> assetsFilteredByType = getAssetTypeCodeName(assetType, results.values());

        final List<Asset> filteredAssets = filterAssets(assetsFilteredByType);

        return getAssetDto(filteredAssets);

    }

    /**
     * filter assets on the assetType
     * @param assetType - assettype on which the assets have to be filtered
     * @param assets - collection of assets from which assets have to be filtered
     * @return - list of filtered asset
     */
    private List<Asset>  getAssetTypeCodeName(AssetType assetType, Collection<Asset> assets) {
        return Lambda.filter(
                Lambda.having(Lambda.on(Asset.class).getCluster(), Matchers.is(assetType)),
                assets);
    }

    /**
     * filter out specific assets with specific asset codes
     * e.g. Stapled securities (listed securities) are not displayed
     * @param assets List of asset details to filter on
     * @return
     */
    private List<Asset> filterAssets(Collection<Asset> assets)
    {
        return Lambda.filter(
                Lambda.having(Lambda.on(Asset.class).getAssetCode(), IsNot.not(Matchers.containsString(".STPLD"))),
                assets);
    }


    /**
     * converts domain asset to assetDto
     * @param filteredAssets - list of domain asset to be converted to asset DTO
     * @return - list of asset dto
     */
    protected List<AssetDto> getAssetDto(List<Asset> filteredAssets) {
        return Lambda.convert(filteredAssets, new Converter<Asset, AssetDto>() {
            @Override
            public AssetDto convert(Asset asset) {
                return AssetDtoConverter.toDtoCodeName(asset);
            }
        });
    }


}
