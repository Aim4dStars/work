package com.bt.nextgen.api.smsf.controller;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.api.smsf.service.AssetDetailsDtoService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

/**
 * Api controller for asset code name -smsf external assets
 */
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AssetDetailsApiController {
    @Autowired
    private AssetDetailsDtoService assetDetailsDtoService;

    /**
     * API method returns asset with assetname, assetcode and assetclass
     * @param assetTypeIntlId - asset type intl id
     * @return - returns list of assetdtos
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ASSET_DETAILS)
    @ResponseBody public
    ApiResponse getAssetTypeCodeName(@PathVariable (UriMappingConstants.ASSET_TYPE_INTLID) String assetTypeIntlId) {
        final List<ApiSearchCriteria> criteria = Collections.singletonList(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID,
                ApiSearchCriteria.SearchOperation.EQUALS,
                assetTypeIntlId,
                ApiSearchCriteria.OperationType.STRING));
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, assetDetailsDtoService, criteria).performOperation();
    }
}
