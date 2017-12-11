package com.bt.nextgen.api.smsf.controller;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.api.smsf.service.AssetTypeDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AssetTypeApiController
{
    @Autowired
    private AssetTypeDtoService assetTypeDtoService;

    @RequestMapping(method = RequestMethod.GET, value=UriMappingConstants.ASSET_TYPES)
    public @ResponseBody ApiResponse getAllAssetTypes()
    {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, assetTypeDtoService).performOperation();
    }

}
