package com.bt.nextgen.api.version.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.version.model.ModuleKey;
import com.bt.nextgen.api.version.service.MobileAppVersionDtoService;
import com.bt.nextgen.api.version.service.VersionDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.domain.key.StringIdKey;

/**
 * This is a global service which retrieves the version of all apis exposed by
 * the application
 */

@Controller
@RequestMapping(produces = "application/json")
public class VersionApiController {

    @Autowired
    private VersionDtoService versionService;

    @Autowired
    private MobileAppVersionDtoService mobileAppVersionDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PUBLIC_API)
    public
    @ResponseBody
    ApiResponse getApplicationVersions() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, versionService).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.MODULE_VERSION)
    public
    @ResponseBody
    ApiResponse getModuleVersion(@PathVariable(UriMappingConstants.MODULE_ID_URI_MAPPING) String moduleId) {
        ModuleKey moduleKey = new ModuleKey(moduleId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, versionService, moduleKey).performOperation();
    }

    /**
     * Returns mobile client version details based on platform client version.
     * Details: <a href="http://dwgps0026/twiki/bin/view/NextGen/RESTMobileVersionService>RESTMobileVersionService</a>
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.MOBILE_MIN_VERSION)
    public
    @ResponseBody
    ApiResponse getMobileMinVersion(@RequestParam(value = "platform", required = false, defaultValue = "common") String platform) {
        StringIdKey key = new StringIdKey(platform);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, mobileAppVersionDtoService, key).performOperation();
    }
}
