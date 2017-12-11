package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.smsf.model.AssetClassOrderedDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetClassMappingServiceImpl implements AssetClassMappingService
{
    private static final Logger logger = LoggerFactory.getLogger(AssetClassMappingServiceImpl.class);

    @Autowired
    private StaticDataDtoService staticDataDtoService;

    @Override
    public List search(List list, ServiceErrors serviceErrors) {
        return new ArrayList();
    }

    @Override
    public List<AssetClassDto> search(String key, ServiceErrors serviceErrors)
    {
        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("asset_class_grp", ApiSearchCriteria.SearchOperation.EQUALS, "asset_class_grp", ApiSearchCriteria.OperationType.STRING);
        critiera.add(searchCriteria);

        AssetType typeToRetrieveClassFor = null;

        try
        {
            if (StringUtils.isNotEmpty(key))
            {
                typeToRetrieveClassFor = AssetType.getByCode(key);
            }
            else
            {
                throw new IllegalArgumentException("Unable to search Asset classes -- missing asset type argument");
            }
        }
        catch (IllegalArgumentException e)
        {
            logger.warn("Unable to search Asset classes -- missing asset type argument", e);
            throw new IllegalArgumentException("Unable to search Asset classes -- missing asset type argument", e);
        }

        return (List<AssetClassDto>) (List) sortAssetTypes(staticDataDtoService.search(critiera, serviceErrors), typeToRetrieveClassFor);
    }


    /**
     * Return sorted Asset Classifications by their designated order.
     * If no order is found, then it is not returned in the list of classifications.
     * TODO: Common implementation of this method across the AssetType and AssetClass
     * @return sorted asset type list
     */
    private List<AssetClassOrderedDto> sortAssetTypes(List<StaticCodeDto> assetClasses, AssetType assetType)
    {
        AssetClassConverter assetClassConverter = new AssetClassConverter();
        List<AssetClassOrderedDto> assetTypesToSort = new ArrayList<>();
        AssetClassOrderedDto assetClassOrderedDto = null;

        List<AssetClass> availableAssetClasses = new ArrayList<>(AssetClassMapping.getClassificationsForAssetType(assetType));

        // Run through all the asset classifications that are associated with the asset type.
        // Try to find a corresponding static code entry for each asset classification
        for (AssetClass assetClass : availableAssetClasses)
        {
            StaticCodeDto matchingStaticCode = null;

            // Find matching static code entry for the asset class
            for (StaticCodeDto code : assetClasses)
            {
                if (assetClass.getCode().equalsIgnoreCase(code.getIntlId()))
                {
                    matchingStaticCode = code;
                }
            }

            // If there is a matching static code, then convert that into a dto for display
            if (matchingStaticCode != null)
            {
                assetClassOrderedDto = assetClassConverter.toAssetClassOrderedDto(matchingStaticCode, assetClass);
                assetTypesToSort.add(assetClassOrderedDto);
            }
            else
            {
                logger.info("Asset Classification: {} relationship found - but no matching static code entry", assetClass.getDescription());
            }
        }

        // Sort the list of assetTypeDto's (now that they have defined order)
        Collections.sort(assetTypesToSort, new Comparator<AssetClassOrderedDto>() {
            @Override
            public int compare(AssetClassOrderedDto o1, AssetClassOrderedDto o2) {
                if (o1.getOrder() > o2.getOrder())
                    return 1;
                else
                    return -1;
            }
        });

        return assetTypesToSort;
    }



    @Override
    public AssetClassDto find(String key, ServiceErrors serviceErrors) {
        return null;
    }
}
