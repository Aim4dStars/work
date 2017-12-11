package com.bt.nextgen.api.draftaccount.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.service.DirectOnboardingDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.web.controller.HomePageController;

import static com.bt.nextgen.core.session.SessionUtils.ORIGINATING_SYSTEM;

@Controller @RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API,
        UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE) public class DirectOnboardingApiController {

    private static final String SUBMIT_DIRECT_ACCOUNTS_URI = "/client_application/direct_account/submit/";

    @Autowired private DirectOnboardingDtoService directOnboardingDtoService;

    @RequestMapping(method = RequestMethod.POST, value = SUBMIT_DIRECT_ACCOUNTS_URI) @PreAuthorize("isAuthenticated()") @ResponseBody public KeyedApiResponse<ClientApplicationKey> submit(
            HttpSession session, @RequestBody ClientApplicationDto clientApplicationDto) {
        // Hack for setting the originating system for post boarding. Need to remove once we support direct onboarding via other systems.
        session.setAttribute(ORIGINATING_SYSTEM, HomePageController.CHANNEL_WESTPAC_LIVE);
        return new Submit<>(ApiVersion.CURRENT_VERSION, directOnboardingDtoService, null, clientApplicationDto)
                .performOperation();
    }
}
