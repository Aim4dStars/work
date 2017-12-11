package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.service.integration.asset.ModelAssetClass;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component("ModelAssetClassConverter")
public class ModelAssetClassConverter {

    public List<AssetClassDto> convertToModelAssetClasses(List<StaticCodeDto> assetStaticCodes) {
        List<AssetClassDto> modelAssetClasses = new ArrayList<>();
        for (StaticCodeDto staticCode : assetStaticCodes) {
            ModelAssetClass modelAssetClass = ModelAssetClass.forIntlId(staticCode.getIntlId());
            AssetClassDto assetClassDto = new AssetClassDto(modelAssetClass.getDescription(), staticCode.getIntlId());
            modelAssetClasses.add(assetClassDto);
        }
        sortByCustomOrder(modelAssetClasses);
        return modelAssetClasses;
    }

    private void sortByCustomOrder(List<AssetClassDto> ipsAssetClasses) {
        Collections.sort(ipsAssetClasses, new Comparator<AssetClassDto>() {
            @Override
            public int compare(AssetClassDto o1, AssetClassDto o2) {
                Integer o1SortOrder = ModelAssetClass.forIntlId(o1.getAssetCode()).getOrder();
                Integer o2SortOrder = ModelAssetClass.forIntlId(o2.getAssetCode()).getOrder();
                return o1SortOrder.compareTo(o2SortOrder);
            }
        });
    }

}
