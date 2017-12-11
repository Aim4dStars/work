package com.bt.nextgen.api.uar.controller;

import com.bt.nextgen.api.uar.model.UarDetailsDto;
import com.bt.nextgen.api.uar.model.UarTrxDto;
import com.bt.nextgen.api.uar.service.UarClientDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.*;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.integration.uar.validation.UarErrorMapper;
import com.bt.nextgen.service.integration.user.UserKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L081012 on 18/09/2015.
 */
@Controller("UarApiController")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class UarApiController {

    @Autowired
    @Qualifier("UarClientService")
    private UarClientDtoService uarClientDtoService;

    @Autowired
    private UarErrorMapper uarErrorMapper;

    @Autowired
    private UserProfileService profileService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.UAR_RECORDS)
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('client.uar.view')")
    public
    @ResponseBody
    ApiResponse getUarClients(@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy,
                              @RequestParam(value = Sort.SORT_PARAMETER, required = false) String sortOrder)  {

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();

        return new Group <>(ApiVersion.CURRENT_VERSION, new Sort <>(new SearchByCriteria <>(ApiVersion.CURRENT_VERSION,
                uarClientDtoService,
                criteria), sortOrder), groupBy).performOperation();

        //return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, uarClientDtoService, filter).performOperation();
            //return new FindOne<>(ApiVersion.CURRENT_VERSION, uarClientDtoService).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUBMIT_UAR)
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('client.uar.view')")
    public
    @ResponseBody
    ApiResponse submitUarClients(@ModelAttribute UarTrxDto uarTrxDto)  {
        if (!profileService.isEmulating()) {
            UarDetailsDto uarDetailsDto = new UarDetailsDto();
            uarDetailsDto.setUarComponent(uarTrxDto.getUarComponent());
            uarDetailsDto.setDocId(uarTrxDto.getDocId());
            uarDetailsDto.setKey(UserKey.valueOf(uarTrxDto.getKey()));
            return new Submit<>(ApiVersion.CURRENT_VERSION, uarClientDtoService, uarErrorMapper, uarDetailsDto).performOperation();
        }
        else throw new AccessDeniedException("Access Denied");
    }

    /*@RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SUBMIT_UAR)
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#adviserId, 'account.fee.advice.update')")
    public @ResponseBody
    KeyedApiResponse<AccountKey> submitUar(@PathVariable(UriMappingConstants.ADVISER_ID_URI_MAPPING) String adviserId,
                                            @ModelAttribute UarDto uarDto)
    {
        if (!profileService.isEmulating()) {
            AccountKey key = new AccountKey(accId);
            UarDto uarDto = new UarDto();
            uarDto.setKey(key);
            scheduleDto.setTransactionDto(feeScheduleTransactionDto);
            return new Submit<>(ApiVersion.CURRENT_VERSION, feeScheduleDtoService, feesScheduleDtoErrorMapper, uarDto).performOperation();
        }
        else {
            throw new AccessDeniedException("Access Denied");
        }
    }
    */
}
