package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.model.AssetDto;
import com.bt.nextgen.service.integration.asset.Asset;
import org.apache.commons.lang.StringUtils;

/**
 * AssetDto convertor for SMSF external asset domain object
 */
public final class AssetDtoConverter {

    //private final static Logger logger = LoggerFactory.getLogger(AssetDtoConverter.class);

    private AssetDtoConverter() {

    }

    /**
     * Returns assetDto object created from domain object
     * @param asset
     * @return assetDto
     */
    public static AssetDto toDtoCodeName(Asset asset) {
        final AssetDto assetDto = new AssetDto();

        assetDto.setAssetId(asset.getAssetId());

            if (asset.getAssetClass() != null)
            {
            assetDto.setAssetClass(asset.getAssetClass().getDescription());
            }

            if (StringUtils.isNotEmpty(asset.getAssetCode()))
            {
                assetDto.setAssetCode(asset.getAssetCode().toUpperCase().trim());
            }

            assetDto.setAssetName(asset.getAssetName());

            if (asset.getAssetClassId() != null && StringUtils.isNotEmpty(asset.getAssetClassId().getCode()))
            {
                assetDto.setAssetClassId(asset.getAssetClassId().getCode());
            }

        assetDto.setAssetType(asset.getCluster().getDescription());

        return assetDto;
    }

}
