package com.bt.nextgen.api.smsf.controller;

import com.bt.nextgen.api.smsf.service.AssetClassMappingService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.SearchByKey;
import com.bt.nextgen.api.smsf.model.AssetClassDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AssetClassMappingApiController
{
    @Autowired
    private AssetClassMappingService assetClassMappingService;

    @RequestMapping(method = RequestMethod.GET, value="/asset/{assetTypeCode}/classes")
    public @ResponseBody
    ApiResponse getAssetClassesForAssetType(@PathVariable String assetTypeCode)
    {
        return new SearchByKey<String, AssetClassDto>(ApiVersion.CURRENT_VERSION, assetClassMappingService, assetTypeCode).performOperation();
    }
}
