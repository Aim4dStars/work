package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationSubmitDto;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDtoService;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationSubmitDtoService;
import com.bt.nextgen.api.draftaccount.service.DashboardClientApplicationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.Delete;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientApplicationApiController {

    public static final String SUBMIT_DRAFT_ACCOUNTS_URI = "/draft_accounts/submit";

    private static final String AUTHENTICATED_VALID_ADVISER = "isAuthenticated() and hasPermission(#adviserId, 'isValidAdviser')";

    private static final String ADVISER_VIEW_PERMISSION = AUTHENTICATED_VALID_ADVISER
            + " and @permissionBaseService.hasBasicPermission('account.application.view')";

    private static final String ADVISER_CREATE_PERMISSION = AUTHENTICATED_VALID_ADVISER
            + " and @permissionBaseService.hasBasicPermission('account.application.create')";

    private static final String NON_EMULATING_ADVISER_CREATE_PERMISSION = ADVISER_CREATE_PERMISSION
            + " and hasPermission(null, 'isNotEmulating')";

    private static final String NON_EMULATING_ADVISER_DISCARD_PERMISSION = AUTHENTICATED_VALID_ADVISER
            + " and hasPermission(null, 'isNotEmulating') and @permissionBaseService.hasBasicPermission('account.application.discard')";

    private static final Logger logger = LoggerFactory.getLogger(ClientApplicationApiController.class);

    @Autowired
    private ClientApplicationDtoService clientApplicationDtoService;

    @Autowired
    private DashboardClientApplicationDtoService dashboardClientApplicationDtoService;

    @Autowired
    private ClientApplicationSubmitDtoService clientApplicationSubmitDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS)
    @PreAuthorize(ADVISER_VIEW_PERMISSION)
    @ResponseBody
    public ApiResponse getDraftAccounts() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, clientApplicationDtoService).performOperation();
    }


    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS_LATEST)
    @PreAuthorize(ADVISER_VIEW_PERMISSION)
    @ResponseBody
    public ApiResponse getCertainNumberOfLatestDraftAccounts(@RequestParam("count") String numberOfDraftsToGet) {
        return new ApiResponse(ApiVersion.CURRENT_VERSION, dashboardClientApplicationDtoService.getLatestDraftAccounts(Integer.parseInt(numberOfDraftsToGet), new ServiceErrorsImpl()));
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.DRAFT_ACCOUNTS)
    @PreAuthorize(ADVISER_CREATE_PERMISSION)
    @ResponseBody
    public KeyedApiResponse<ClientApplicationKey> createDraftAccount(@RequestBody ClientApplicationDto clientApplicationDto) {
        logger.debug("createDraftAccount - dto: {}", clientApplicationDto);
        return new Create<>(ApiVersion.CURRENT_VERSION, clientApplicationDtoService, clientApplicationDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST,value = UriMappingConstants.DRAFT_ACCOUNTS +"/{id}")
    @PreAuthorize(ADVISER_CREATE_PERMISSION)
    @ResponseBody
    public KeyedApiResponse<ClientApplicationKey> saveDraftAccount(@PathVariable long id, @RequestBody ClientApplicationDto clientApplicationDto) {
        logger.debug("saveDraftAccount - id: {} - dto: {}", id, clientApplicationDto);
        clientApplicationDto.setKey(new ClientApplicationKey(id));
        return new Update<>(ApiVersion.CURRENT_VERSION, clientApplicationDtoService, null, clientApplicationDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST,value = SUBMIT_DRAFT_ACCOUNTS_URI + "/{id}")
    @PreAuthorize(NON_EMULATING_ADVISER_CREATE_PERMISSION)
    @ResponseBody
    public KeyedApiResponse<ClientApplicationKey> submitDraftAccount(@PathVariable long id,@RequestBody ClientApplicationSubmitDto clientApplicationSubmitDto) {
        clientApplicationSubmitDto.setKey(new ClientApplicationKey(id));
        return new Submit<>(ApiVersion.CURRENT_VERSION, clientApplicationSubmitDtoService, null, clientApplicationSubmitDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST,value = UriMappingConstants.DRAFT_ACCOUNTS +"/{id}/delete")
    @PreAuthorize(NON_EMULATING_ADVISER_DISCARD_PERMISSION)
    @ResponseBody
    public ApiResponse deleteDraftAccount(@PathVariable long id) {
        return new Delete<>(ApiVersion.CURRENT_VERSION, clientApplicationDtoService,new ClientApplicationKey(id)).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET,value = UriMappingConstants.DRAFT_ACCOUNTS +"/{id}")
    @PreAuthorize(ADVISER_VIEW_PERMISSION)
    @ResponseBody
    public KeyedApiResponse<ClientApplicationKey> findDraftAccount(@PathVariable long id) {
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, clientApplicationDtoService, new ClientApplicationKey(id)).performOperation();
    }

}
