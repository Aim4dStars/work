package com.bt.nextgen.api.modelportfolio.v2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.smsf.model.AssetClassDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.conversion.CodeCategory;

@Service
public class ModelAssetClassDtoServiceImpl implements ModelAssetClassDtoService {

    @Autowired
    private StaticDataDtoService staticDataDtoService;

    @Autowired
    private ModelAssetClassConverter modelAssetClassConverter;

    @Override
    public List<AssetClassDto> findAll(ServiceErrors serviceErrors) {
        List<ApiSearchCriteria> critiera = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("category", ApiSearchCriteria.SearchOperation.EQUALS,
                CodeCategory.ASSET_CLASS.name(), ApiSearchCriteria.OperationType.STRING);
        critiera.add(searchCriteria);

        List<StaticCodeDto> staticCodes = staticDataDtoService.search(critiera, serviceErrors);
        return modelAssetClassConverter.convertToModelAssetClasses(staticCodes);               
        
    }

}
