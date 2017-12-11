package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.smsf.model.AssetClassOrderedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AssetClassDtoServiceImpl implements AssetClassDtoService
{
    @Autowired
    private StaticDataDtoService staticDataDtoService;


    @Override
    public List<AssetClassDto> findAll(ServiceErrors serviceErrors)
    {
        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("asset_class_grp", ApiSearchCriteria.SearchOperation.EQUALS, "asset_class_grp", ApiSearchCriteria.OperationType.STRING);
        critiera.add(searchCriteria);

        return sortAssetTypes(staticDataDtoService.search(critiera, serviceErrors));
    }


    /**
     * Return sorted Asset Types by their designated order
     * @return sorted asset type list
     */
    private List<AssetClassDto> sortAssetTypes(List<StaticCodeDto> assetClasses)
    {
        AssetClassConverter assetClassConverter = new AssetClassConverter();
        List<AssetClassOrderedDto> assetTypesToSort = new ArrayList<>();
        AssetClassOrderedDto assetTypeDto = null;

        // Grab avaloq asset types and try to map against our AssetType enum
        for (StaticCodeDto code : assetClasses)
        {
            AssetClass assetToSort = AssetClass.getByCode(code.getIntlId());

            // AssetType enumeration found - convert to dto with specific order
            if (assetToSort != null)
            {
            assetTypeDto = assetClassConverter.toAssetClassOrderedDto(code, assetToSort);
        }
        else
        {
            assetTypeDto = assetClassConverter.toAssetClassOrderedDto(code);
        }

            assetTypesToSort.add(assetTypeDto);
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

        List<AssetClassDto> sortedAssetClasses = new ArrayList();

        // Convert back to AssetClass entities
        for (AssetClassOrderedDto assetClassOrdered : assetTypesToSort)
        {
            AssetClassDto assetClass = new AssetClassDto(assetClassOrdered.getAssetName(), assetClassOrdered.getAssetCode());
            sortedAssetClasses.add(assetClass);
        }

        return sortedAssetClasses;
    }
}
