package com.bt.nextgen.api.user.v1.controller;

import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDto;
import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDtoKey;
import com.bt.nextgen.api.user.v1.service.TermsAndConditionsDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.Submit;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This API is used to retrieve and accept terms and conditions for a user
 * <p/>
 * GET: secure/api/user/v1_0/terms-and-conditions/
 * <p/>
 * POST: secure/api/user/v1_0/terms-and-conditions/{tnc}/{version}
 */
@Api("Provides services to support fetching and accepting terms and conditions")
@Controller("TermsAndConditionsApiControllerV1")
public class TermsAndConditionsApiController {

    @Autowired
    private TermsAndConditionsDtoService termsAndConditionsService;

    @Autowired
    private UserProfileService profileService;

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Retrieves a list of terms and conditions and their statuses for the current user", response = TermsAndConditionsDto.class, responseContainer = "List")
    @RequestMapping(method = RequestMethod.GET, value = "${api.user.v1.uri.termsandconditions}")
    public @ResponseBody ApiResponse getTermsAndConditions() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, termsAndConditionsService).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "Accepts terms and conditions (single item) for the current user")
    @RequestMapping(method = RequestMethod.POST, value = "${api.user.v1.uri.accepttermsandconditions}")
    public @ResponseBody ApiResponse acceptTermsAndConditions(@PathVariable("tnc-id") @ApiParam(value = "Terms and conditions ID") String tncId,
            @PathVariable("version") @ApiParam(value="Terms and conditions version") Integer version) {
        String userId = profileService.getActiveProfile().getBankReferenceId();

        TermsAndConditionsDtoKey key = new TermsAndConditionsDtoKey(EncodedString.fromPlainText(userId).toString(), tncId,
                version);
        TermsAndConditionsDto dto = new TermsAndConditionsDto(key);

        return new Submit<>(ApiVersion.CURRENT_VERSION, termsAndConditionsService, null, dto).performOperation();
    }
}
