package com.bt.nextgen.api.investor.controller;

import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.safi.controller.TwoFactorAuthenticationBaseController;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * This API retrieves Investors detail for your detail page
 *
 * Sample request:
 * http://localhost:9080/ng/secure/api/v1_0/safiAnalyze - 2FA
 * http://localhost:9080/ng/secure/api/v1_0/safiChallenge - 2FA
 * http://localhost:9080/ng/secure/api/v1_0/verify - 2FA
 * http://localhost:9080/ng/secure/api/v1_0/investors/{client-id} -
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class InvestorApiController extends TwoFactorAuthenticationBaseController{

    private static final Logger logger = LoggerFactory.getLogger(InvestorApiController.class);

    @Autowired
    private ClientListDtoService clientListDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.INVESTOR)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Person_requests')")
    public
    @ResponseBody
    ApiResponse getInvestorDetails(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientId) {
        final String clientKey = new EncodedString(clientId).plainText();
        if (profileService.isInvestor() && clientKey.equals(profileService.getActiveProfile().getClientKey().getId())) {
            final ClientKey id = new ClientKey(clientId);
            return new FindByKey<>(ApiVersion.CURRENT_VERSION, clientListDtoService, id).performOperation();
        } else {
            this.logger.info("Access denied to view the investor detail.");
            throw new AccessDeniedException("Access Denied");
        }
    }

}
