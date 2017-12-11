package com.bt.nextgen.api.profile.controller;

import com.bt.nextgen.api.profile.model.ProfileDetailsUpdateDto;
import com.bt.nextgen.api.profile.service.ProfileDetailsDtoService;
import com.bt.nextgen.api.profile.service.ProfileDetailsUpdateDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This service retrieves profile details for the current user's role.
 */

@Deprecated
@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileApiController {

    @Autowired
    private ProfileDetailsDtoService profileDetailsService;

    @Autowired
    private ProfileDetailsUpdateDtoService profileDetailsUpdateService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PROFILE)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse getProfileDetails() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, profileDetailsService).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.WHATS_NEW)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody KeyedApiResponse<String> updateWhatsNewStatus(
        @PathVariable(UriMappingConstants.READ_STATUS_URI_MAPPING) String readStatus)
        throws Exception {
        return new Update<>(ApiVersion.CURRENT_VERSION, profileDetailsUpdateService,
            null, new ProfileDetailsUpdateDto(readStatus)).performOperation();
    }
}
