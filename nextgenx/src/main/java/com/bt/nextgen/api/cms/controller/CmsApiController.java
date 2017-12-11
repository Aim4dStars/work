package com.bt.nextgen.api.cms.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.cms.model.CmsDtoKey;
import com.bt.nextgen.api.cms.service.CmsFileMetaDataDtoService;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;

/**
 * This API retrieves the JSON string response from AEM
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class CmsApiController {
    @Autowired
    private CmsFileMetaDataDtoService cmsFileMetaDataDtoService;

    /**
     * Method to retrieve the JSON string from AEM.
     *
     * @param fileKey
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CMS)
    @ResponseBody
    public ApiResponse getCmsResource(@RequestParam(required = true, value = ApiConstants.KEY) String fileKey,
                                      @RequestParam(required = false, value = ApiConstants.QUERY) String query) {
        CmsDtoKey dtoKey = new CmsDtoKey();
        dtoKey.setKey(fileKey);
        dtoKey.setQuery(StringUtils.isNotBlank(query) ? query : "");
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, cmsFileMetaDataDtoService, dtoKey).performOperation();
    }
}
