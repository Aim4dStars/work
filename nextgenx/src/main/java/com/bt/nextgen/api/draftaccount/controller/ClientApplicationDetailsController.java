package com.bt.nextgen.api.draftaccount.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationApprovalDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationWithdrawalDto;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationApprovalDtoService;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDetailsDtoService;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationWithdrawalDtoService;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationsOverviewService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.Submit;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API)
public class ClientApplicationDetailsController {

    public static final String APPROVALS = "/approvals";
    public static final String WITHDRAW = "/withdraw";

    @Autowired
    private ClientApplicationDetailsDtoService clientApplicationDetailsDtoService;

    @Autowired
    private ClientApplicationApprovalDtoService clientApplicationApprovalDtoService;

    @Autowired
    private ClientApplicationWithdrawalDtoService clientApplicationWithdrawalDtoService;

    @Autowired
    private ClientApplicationsOverviewService clientApplicationsOverviewService;

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.DRAFT_ACCOUNTS + "/{encodedOnboardingApplicationId}" + APPROVALS , produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'isNotEmulating')")
    @ResponseBody
    public ApiResponse approveApplication(@PathVariable String encodedOnboardingApplicationId) {
        long onboardingApplicationId = Long.parseLong(EncodedString.toPlainText(encodedOnboardingApplicationId));
        OnboardingApplicationKey key = OnboardingApplicationKey.valueOf(onboardingApplicationId);
        ClientApplicationApprovalDto clientApplicationApprovalDto = new ClientApplicationApprovalDto(key, null);
        return new Submit<>(ApiVersion.CURRENT_VERSION, clientApplicationApprovalDtoService, null, clientApplicationApprovalDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.DRAFT_ACCOUNTS + "/{clientApplicationId}" + WITHDRAW , produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser')  and @permissionBaseService.hasBasicPermission('account.application.withdraw')")
    @ResponseBody
    public ApiResponse withdrawApplication(@PathVariable String clientApplicationId) {
        ClientApplicationKey key = new ClientApplicationKey(Long.parseLong(clientApplicationId));
        ClientApplicationWithdrawalDto clientApplicationWithdrawalDto = new ClientApplicationWithdrawalDto(key);
        return new Submit<>(ApiVersion.CURRENT_VERSION, clientApplicationWithdrawalDtoService, null, clientApplicationWithdrawalDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS + "/{encodedAccountNumber}" + APPROVALS , produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    // Get an application for the approval page if account number supplied (from overview page)
    @ResponseBody
    public ApiResponse getClientApplicationDetailsForLoggedInUser(@PathVariable String encodedAccountNumber) {
        return new ApiResponse(ApiVersion.CURRENT_VERSION, clientApplicationDetailsDtoService.findByAccountNumber(EncodedString.toPlainText(encodedAccountNumber), new ServiceErrorsImpl()));
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS + APPROVALS , produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    // Get an application for the approval page if no account number supplied
    @ResponseBody
    public ApiResponse getClientApplicationDetailsForNewUser() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, clientApplicationDetailsDtoService).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS + "/client_application")
    @PreAuthorize("isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser') and @permissionBaseService.hasBasicPermission('account.application.view')")
    // View Application from tracking page
    @ResponseBody
    public ApiResponse getClientApplicationDetailsByDraftAppId(@RequestParam("id") Long draftAccountId) {
        return new ApiResponse(ApiVersion.CURRENT_VERSION, clientApplicationDetailsDtoService.findByClientApplicationId(draftAccountId, new ServiceErrorsImpl()));
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS + "/fundestablishment")
    @PreAuthorize("isAuthenticated()")
    // View Fund Establishment Status Application from tracking page
    @ResponseBody
    public ApiResponse getFundEstablishmentStatus(@RequestParam("id") Long draftAccountId,
                                                  @RequestParam("accountNumber") String accountNumber){
        return new ApiResponse(ApiVersion.CURRENT_VERSION, clientApplicationDetailsDtoService.findFundEstablishmentStatusByClientApplicationId(draftAccountId, accountNumber, new ServiceErrorsImpl()));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/client_application/applications", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    // ClientOverview: displays only the online accounts (US18507 - excludes pending offline approvals)
    @ResponseBody
    public ApiResponse getClientApplicationsOverviewForLoggedInInvestor() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, clientApplicationsOverviewService).performOperation();
    }
}

