package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.model.AssetTypeDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AssetTypeDtoServiceImpl implements AssetTypeDtoService
{
    @Autowired
    private StaticDataDtoService staticDataDtoService;


    @Override
    public List<AssetTypeDto> findAll(ServiceErrors serviceErrors)
    {
        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("asset_cluster", ApiSearchCriteria.SearchOperation.EQUALS, "asset_cluster", ApiSearchCriteria.OperationType.STRING);
        critiera.add(searchCriteria);

        return sortAssetTypes(staticDataDtoService.search(critiera, serviceErrors));
    }


    /**
     * Return sorted Asset Types by their designated order
     * @return sorted asset type list
     */
    private List<AssetTypeDto> sortAssetTypes(List<StaticCodeDto> assetTypes)
    {
        AssetTypeConverter assetTypeConverter = new AssetTypeConverter();
        List<AssetTypeDto> assetTypesToSort = new ArrayList<>();
        AssetTypeDto assetTypeDto = null;

        // Grab avaloq asset types and try to map against our AssetType enum
        for (StaticCodeDto code : assetTypes)
        {
            AssetType assetToSort = AssetType.getByCode(code.getIntlId());

            // AssetType enumeration found - convert to dto with specific order
            if (assetToSort != null)
            {
                assetTypeDto = assetTypeConverter.toAssetTypeDto(code, assetToSort);
            }
            else
            {
                assetTypeDto = assetTypeConverter.toAssetTypeDto(code);
            }

            assetTypesToSort.add(assetTypeDto);
        }

        // Sort the list of assetTypeDto's (now that they have defined order)
        Collections.sort(assetTypesToSort, new Comparator<AssetTypeDto>() {
            @Override
            public int compare(AssetTypeDto o1, AssetTypeDto o2) {
                if (o1.getOrder() > o2.getOrder())
                    return 1;
                else
                    return -1;
            }
        });

        return assetTypesToSort;
    }



}
