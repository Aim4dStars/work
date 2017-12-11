package com.bt.nextgen.api.profile.v1.controller;

import com.bt.nextgen.api.profile.v1.model.ProfileDetailsUpdateDto;
import com.bt.nextgen.api.profile.v1.service.ProfileDetailsDtoService;
import com.bt.nextgen.api.profile.v1.service.ProfileDetailsUpdateDtoService;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.service.UserPreferenceDtoService;
import com.bt.nextgen.api.userpreference.validation.UserPreferenceDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This service retrieves profile details for the current user's role.
 */
@Controller("ProfileApiControllerV1")
@RequestMapping(produces = "application/json")
public class ProfileApiController {

    @Autowired
    private ProfileDetailsDtoService profileDetailsService;

    @Autowired
    private ProfileDetailsUpdateDtoService profileDetailsUpdateService;

    @Autowired
    private UserPreferenceDtoService userPreferenceDtoService;

    private UserPreferenceDtoErrorMapper errorMapper;

    @RequestMapping(method = RequestMethod.GET, value = { "${api.profile.v1.uri.profile}", "${api.onboard.v1.uri.profile}" })
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse getProfileDetails() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, profileDetailsService).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = { "${api.profile.v1.uri.whatsNew}"})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody KeyedApiResponse<String> updateWhatsNewStatus(
            @PathVariable(UriMappingConstants.READ_STATUS_URI_MAPPING) String readStatus) throws Exception {
        return new Update<>(ApiVersion.CURRENT_VERSION, profileDetailsUpdateService, null,
                new ProfileDetailsUpdateDto(readStatus)).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = { "${api.profile.v1.uri.switchProfile}"})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse switchProfile(@ModelAttribute UserPreferenceDto userPreferenceDto) {
        ApiResponse preferenceResponse = new Update<>(ApiVersion.CURRENT_VERSION, userPreferenceDtoService, errorMapper,
                userPreferenceDto)
                .performOperation();

        // if the preference update failed then return the response
        if (preferenceResponse.getStatus() == 0) {
            return preferenceResponse;
        }

        return new FindOne<>(ApiVersion.CURRENT_VERSION, profileDetailsService).performOperation();
    }
}
