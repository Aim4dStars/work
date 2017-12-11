package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.SendEmailDto;
import com.bt.nextgen.api.draftaccount.service.SendEmailService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API)
public class SendEmailApiController {

    @Autowired
    private SendEmailService sendEmailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailApiController.class);


    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.DRAFT_ACCOUNTS + "/{clientApplicationId}/resend_code/{clientId}")
    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser') and hasPermission(null, 'isNotEmulating') and @permissionBaseService.hasBasicPermission('account.application.create')")
    @ResponseBody
    public ApiResponse resendEmailCode(@PathVariable Long clientApplicationId, @PathVariable String clientId)
    {
        try {
            LOGGER.info(LoggingConstants.ONBOARDING_SEND + "resendEmailCode begin");
            LOGGER.info(LoggingConstants.ONBOARDING_SEND + "resendEmailCode for clientApplicationId=" + clientApplicationId + ", clientId=" + clientId);
            SendEmailDto sendEmailDto = new SendEmailDto(clientApplicationId, clientId);
            return new Submit<>(ApiVersion.CURRENT_VERSION, sendEmailService, null, sendEmailDto).performOperation();
        } finally {
            LOGGER.info(LoggingConstants.ONBOARDING_SEND + "resendEmailCode end");
        }
    }
}
