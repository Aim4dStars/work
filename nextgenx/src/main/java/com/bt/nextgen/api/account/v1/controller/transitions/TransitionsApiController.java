package com.bt.nextgen.api.account.v1.controller.transitions;

import com.bt.nextgen.api.account.v1.service.TransitionClientDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by L069552 on 18/09/2015.
 */
@Deprecated
@Controller("TransitionAccountController")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class TransitionsApiController {

    @Autowired
    @Qualifier("TransitionClientService")
    private TransitionClientDtoService transitionClientDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TRANSITION_ACCOUNTS)
    @PreAuthorize("isAuthenticated() and @permissionBaseService.hasBasicPermission('client.intermediary.report.view')")
    public
    @ResponseBody
    ApiResponse getAccounts(@RequestParam(required = false, value = "filter") String filter) {

        if (filter != null) {
            return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION,
                    transitionClientDtoService, filter).performOperation();
        } else {

            return new FindAll<>(ApiVersion.CURRENT_VERSION, transitionClientDtoService).performOperation();

        }

    }
}
