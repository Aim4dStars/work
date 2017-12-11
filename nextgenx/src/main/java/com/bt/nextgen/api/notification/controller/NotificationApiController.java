package com.bt.nextgen.api.notification.controller;

import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.api.notification.model.NotificationUpdateDto;
import com.bt.nextgen.api.notification.service.NotificationCountDtoService;
import com.bt.nextgen.api.notification.service.NotificationDtoMergeService;
import com.bt.nextgen.api.notification.service.NotificationSearchDtoService;
import com.bt.nextgen.api.notification.service.NotificationUpdateDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByKeyedCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.api.operation.Update;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.api.util.ApiConstants.CATEGORIES;
import static com.bt.nextgen.api.util.ApiConstants.CLIENT;
import static com.bt.nextgen.api.util.ApiConstants.END_DATE;
import static com.bt.nextgen.api.util.ApiConstants.ID;
import static com.bt.nextgen.api.util.ApiConstants.PAGING;
import static com.bt.nextgen.api.util.ApiConstants.SEARCH_FIELD;
import static com.bt.nextgen.api.util.ApiConstants.SEARCH_VALUE;
import static com.bt.nextgen.api.util.ApiConstants.SORT_BY;
import static com.bt.nextgen.api.util.ApiConstants.START_DATE;

/**
 * Sample request:
 * secure/api/v1_0/notification?client=true&categories=fail_trx,new_clt,mat_term,prod_news,conf_trx,chg_act,new_stmt&
 * sortby=date,desc&paging={"startIndex":0,"maxResults":"50"}&startDate=13 Jul 2014&endDate=14 Oct 2014
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class NotificationApiController {
    @Autowired
    private NotificationSearchDtoService notificationSearchDtoService;

    @Autowired
    private NotificationCountDtoService notificationCountDtoService;

    @Autowired
    private NotificationUpdateDtoService notificationUpdateDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.NOTIFICATION)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    ApiResponse getNotifications(@RequestParam(required = false, value = START_DATE) String startDate,
        @RequestParam(required = false, value = END_DATE) String endDate,
        @RequestParam(required = false, value = CATEGORIES) String categories,
        @RequestParam(required = false, value = SEARCH_FIELD) String searchField,
        @RequestParam(required = false, value = SEARCH_VALUE) String searchValue,
        @RequestParam(required = false, value = SORT_BY) String sortby,
        @RequestParam(required = false, value = PAGING) String paging,
        @RequestParam(required = false, value = CLIENT) boolean client) throws Exception {
        List<ApiSearchCriteria> criteriaList = getCriteriaList(startDate, endDate, searchField, searchValue,
            categories);

        if (sortby != null && paging != null) {
            return new NotificationDtoMergeService<>(new PageFilter<>(ApiVersion.CURRENT_VERSION,
                (new Sort<>(new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION,
                    notificationSearchDtoService, new NotificationDtoKey(client),
                    criteriaList), sortby)), paging), new FindByKey<>(ApiVersion.CURRENT_VERSION,
                notificationCountDtoService,
                new NotificationDtoKey(client))).performOperation();
        } else if (sortby != null) {
            return new NotificationDtoMergeService<>(new Sort<>(new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION,
                notificationSearchDtoService, new NotificationDtoKey(client),
                criteriaList), sortby), new FindByKey<>(ApiVersion.CURRENT_VERSION,
                notificationCountDtoService,
                new NotificationDtoKey(client))).performOperation();
        } else if (paging != null) {
            return new NotificationDtoMergeService<>(new PageFilter<>(ApiVersion.CURRENT_VERSION,
                (new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION,
                    notificationSearchDtoService, new NotificationDtoKey(client),
                    criteriaList)), paging), new FindByKey<>(ApiVersion.CURRENT_VERSION,
                notificationCountDtoService,
                new NotificationDtoKey(client))).performOperation();
        } else {
            return new NotificationDtoMergeService<>(new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION,
                notificationSearchDtoService, new NotificationDtoKey(client),
                criteriaList), new FindByKey<>(ApiVersion.CURRENT_VERSION,
                notificationCountDtoService,
                new NotificationDtoKey(client))).performOperation();
        }
    }

    private List<ApiSearchCriteria> getCriteriaList(String startDate, String endDate, String searchField, String
        searchValue, String categories) {
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        if (startDate != null) {
            criteriaList.add(new ApiSearchCriteria(START_DATE, SearchOperation.NEG_LESS_THAN, startDate,
                OperationType.STRING));
        }
        if (endDate != null) {
            criteriaList.add(new ApiSearchCriteria(END_DATE, SearchOperation.NEG_GREATER_THAN, endDate,
                OperationType.STRING));
        }
        if (StringUtils.isNotBlank(searchField) && StringUtils.isNotBlank(searchValue)) {
            criteriaList.add(new ApiSearchCriteria(searchField, SearchOperation.EQUALS, searchValue,
                OperationType.STRING));
        }
        if (categories != null) {
            criteriaList.add(new ApiSearchCriteria(CATEGORIES, SearchOperation.EQUALS, categories,
                OperationType.STRING));
        }
        return criteriaList;
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.NOTIFICATION_READ)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    KeyedApiResponse<String> markNotificationsRead(@RequestParam(value = ID, required = true) String id) throws
        Exception {
        return new Update<>(ApiVersion.CURRENT_VERSION, notificationUpdateDtoService, null,
            new NotificationUpdateDto(id,
                NotificationStatus.READ.toString())).performOperation();
    }

    /**
     * This method is used to mark notifications unread
     *
     * @param id - comma separated list of notification ids to be marked as unread (mandatory)
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.NOTIFICATION_UNREAD)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    KeyedApiResponse<String> markNotificationsUnread(@RequestParam(value = ID, required = true) String id) {
        return new Update<>(ApiVersion.CURRENT_VERSION, notificationUpdateDtoService, null,
                new NotificationUpdateDto(id, NotificationStatus.UNREAD.toString())).performOperation();
    }
}