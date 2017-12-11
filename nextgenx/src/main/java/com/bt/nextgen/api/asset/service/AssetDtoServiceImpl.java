package com.bt.nextgen.api.asset.service;

import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static ch.lambdaj.Lambda.*;

@Service
// @SuppressWarnings("squid:S1200")
public class AssetDtoServiceImpl implements AssetDtoService {
    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    /**
     * Get the assets for the specified search criteria.
     *
     * @param criteriaList  the criteria to search on
     * @param serviceErrors the service errors
     */
    @Override
    public List<AssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Collection<String> assetCodes = new ArrayList<>();

        for (ApiSearchCriteria parameter : criteriaList) {
            switch (parameter.getProperty()) {
                case "assetCodes":
                    assetCodes = Arrays.asList(StringUtils.split(parameter.getValue(), ","));
                    break;
                default:
                    break;
            }
        }

        final List<Asset> assetList = assetService.loadAssetsForAssetCodes(assetCodes, serviceErrors);
        final Map<AssetKey, AssetAllocation> assetAllocations = getAssetAllocationDetails(assetList, serviceErrors);

        return assetDtoConverter.toAssetDto(assetList, null, assetAllocations);
    }

    /**
     * Gets the detailed allocation details for the Managed Funds
     *
     * @param assetList
     * @param serviceErrors
     * @return
     */
    private Map<AssetKey, AssetAllocation> getAssetAllocationDetails(List<Asset> assetList, ServiceErrors serviceErrors) {
        final List<Asset> mfAssets = select(assetList, having(on(Asset.class).getAssetType(),
                Matchers.is(AssetType.MANAGED_FUND)));

        if (CollectionUtils.isNotEmpty(mfAssets)) {
            final List<String> assetIds = collect(mfAssets, on(Asset.class).getAssetId());
            final List<AssetKey> assetKeys = convert(assetIds, new Converter<String, AssetKey>() {
                @Override
                public AssetKey convert(String assetId) {
                    return AssetKey.valueOf(assetId);
                }
            });
            return assetService.loadAssetAllocations(assetKeys, new DateTime(), serviceErrors);
        }
        return null;
    }
}
