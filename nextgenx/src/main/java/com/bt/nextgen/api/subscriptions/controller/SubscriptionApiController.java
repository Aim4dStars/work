package com.bt.nextgen.api.subscriptions.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.api.subscriptions.service.SubscriptionDtoService;
import com.bt.nextgen.api.subscriptions.validation.SubscriptionDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.*;
import com.bt.nextgen.core.repository.SubscriptionDetails;
import com.bt.nextgen.core.repository.SubscriptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class SubscriptionApiController {

    @Autowired
    private SubscriptionDtoService subscriptionDtoService;

    @Autowired
    private SubscriptionDtoErrorMapper subscriptionDtoErrorMapper;

    @Autowired
    private  SubscriptionsRepository subscriptionsRepository;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_SUBSCRIPTIONS)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse getSubscriptionsForAccount(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
                                           @RequestParam(required = false, value = "filter") String filter) {
        AccountKey key = new AccountKey(accountId);
        return new BeanFilter(ApiVersion.CURRENT_VERSION, new SearchByKey<>(ApiVersion.CURRENT_VERSION, subscriptionDtoService, key), filter).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SUBSCRIPTIONS)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse getPendingSubscriptions(@RequestParam(required = false, value = "filter") String filter,
                                        @RequestParam(required = false, value = "sortby") String orderby,
                                        @RequestParam(required = false, value = "paging") String groupby,
                                        @RequestParam(required = false, value = "paging") String paging) {
        ControllerOperation operation = new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, subscriptionDtoService, filter);
        operation = new Sort<>(operation, orderby);
        operation = new Group<>(ApiVersion.CURRENT_VERSION, operation, groupby);
        operation = new PageFilter<>(ApiVersion.CURRENT_VERSION, operation, paging);
        return operation.performOperation();
    }


    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ACCOUNT_SUBSCRIPTIONS)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse subscribeServices(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
                                  @ModelAttribute("subscription") SubscriptionDto subscriptionDto) {
        subscriptionDto.setKey(new AccountKey(accountId));
        return new Submit<>(ApiVersion.CURRENT_VERSION, subscriptionDtoService, subscriptionDtoErrorMapper, subscriptionDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/secure/subscriptions")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<SubscriptionDetails> subscribeServices() {
        return subscriptionsRepository.findAll();
    }

}