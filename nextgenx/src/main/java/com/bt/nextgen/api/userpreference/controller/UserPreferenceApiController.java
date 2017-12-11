package com.bt.nextgen.api.userpreference.controller;

import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.service.UserPreferenceDtoService;
import com.bt.nextgen.api.userpreference.validation.UserPreferenceDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.SearchByKey;
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
 * This API is used to retrieve user preferences which are stored across sessions/PCs
 * The user id can be various id's depending on the preference requirement. These include gcm id and job id.
 * <p/>
 * GET: secure/api/v1_0/userpreference/user/defaultrole
 * <p/>
 * UPDATE: secure/api/v1_0/userpreference/update
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class UserPreferenceApiController {

    @Autowired
    private UserPreferenceDtoService userPreferenceDtoService;

    private UserPreferenceDtoErrorMapper errorMapper;

    /**
     * Retrieve a users preference as specified in the key. The key can be comma delimited to retrieve multiple
     * preferences.
     *
     * @param userType     this indicates if this user preference is for the user's job or the user
     * @param preferenceId preference key to retrieve
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.USER_PREFERENCE_GET)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse find(@PathVariable(UriMappingConstants.USER_PREF_URI_MAPPING) String preferenceId,
        @PathVariable(UriMappingConstants.USER_TYPE_URI_MAPPING) String userType) {
        UserPreferenceDtoKey key = new UserPreferenceDtoKey();
        key.setPreferenceId(preferenceId);
        key.setUserType(userType);
        return new SearchByKey<>(ApiVersion.CURRENT_VERSION, userPreferenceDtoService, key).performOperation();
    }

    /**
     * Update a user's preference, if multiple preferences are required, this should be called one by one.
     *
     * @param userPreferenceDto dto with key and preference value to add/update
     */
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.USER_PREFERENCE_UPDATE)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse update(@ModelAttribute UserPreferenceDto userPreferenceDto) {
        return new Update<>(ApiVersion.CURRENT_VERSION, userPreferenceDtoService,
            errorMapper, userPreferenceDto).performOperation();
    }
}
