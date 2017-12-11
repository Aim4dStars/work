package com.bt.nextgen.api.client.v3.controller;

import com.bt.nextgen.api.client.service.ClientQuickSearchDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.util.ApiConstants.QUERY;

/**
 * @author L096395
 */
@Controller("ClientQuickSearchApiController")
@RequestMapping(produces = "application/json")
@SuppressWarnings({"squid:S1142", "squid:MethodCyclomaticComplexity"})
public class ClientQuickSearchApiController {

    private static final Logger logger = LoggerFactory.getLogger(ClientQuickSearchDtoService.class);

    @Autowired
    private ClientQuickSearchDtoService clientQuickSearchDtoService;

    /*
         /secure/api/clients/v3_0/quicksearch
     */
    @RequestMapping(method = RequestMethod.GET, value = "${api.clients.v3.uri.quicksearch}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public @ResponseBody
    ApiResponse getClients(@RequestParam(required = true, value = QUERY) String query) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.FILTER_FOR_ACCOUNT, ApiSearchCriteria.SearchOperation.EQUALS, query,
                ApiSearchCriteria.OperationType.STRING));
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, clientQuickSearchDtoService, criteria).performOperation();
    }
}