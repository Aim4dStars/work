package com.bt.nextgen.api.client.v2.controller;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientTxnDto;
import com.bt.nextgen.api.client.service.ClientListFilterDtoService;
import com.bt.nextgen.api.client.service.ClientSearchDtoService;
import com.bt.nextgen.api.client.service.ExistingClientSearchDtoService;
import com.bt.nextgen.api.client.v2.service.ClientListDtoService;
import com.bt.nextgen.api.client.validation.ClientDetailsDtoErrorMapper;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.BeanFilter;
import com.bt.nextgen.core.api.operation.BeanFilter.Strictness;
import com.bt.nextgen.core.api.operation.ControllerOperation;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.ServiceFilter;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.CONTAINS;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.LIST_CONTAINS;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller("ClientListApiControllerV2")
@RequestMapping(value = "/secure/api/v2_0", produces = "application/json")
@SuppressWarnings({ "squid:S1142", "squid:MethodCyclomaticComplexity" })
public class ClientListApiController {

    public static final String V2_0 = "v2_0";

    @Autowired
    private ClientListDtoService clientListDtoService;

    @Autowired
    private ClientSearchDtoService clientSearchDtoService;

    @Autowired
    private ExistingClientSearchDtoService existingClientSearchDtoService;

    @Autowired
    private ClientDetailsDtoErrorMapper clientDetailsDtoErrorMapper;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private ClientListFilterDtoService clientListFilterDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "/clients")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public
    @ResponseBody
    ApiResponse getClients(@RequestParam(required = false, value = "filter") String filter,
                           @RequestParam(required = false, value = "sortby") String orderby,
                           @RequestParam(required = false, value = "paging") String paging) {

        ControllerOperation operation;
        if (isNotBlank(filter)) {
            operation = new SearchByCriteria<>(V2_0, clientListDtoService, filter);
        } else {
            operation = new FindAll<>(V2_0, clientListDtoService);
        }
        if (isNotBlank(orderby)) {
            operation = new Sort<>(operation, orderby);
        }
        if (isNotBlank(paging)) {
            operation = new PageFilter<>(V2_0, operation, paging);
        }
        return operation.performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clients/search")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public
    @ResponseBody
    ApiResponse getSearchedClients(@RequestParam(required = false, value = ApiConstants.QUERY) String queryString,
                                   @RequestParam(required = false, value = ApiConstants.ACCOUNT_STATUS) String status,
                                   @RequestParam(required = false, value = ApiConstants.CLIENT) boolean searchClientOnly,
                                   @RequestParam(required = false, value = Attribute.INVESTORS) String investorType) {
        final List<ApiSearchCriteria> filterCriteria = new ArrayList<>();

        if (isNotBlank(status)) {
            for (String state : status.split(",")) {
                filterCriteria.add(new ApiSearchCriteria("accountStatus", EQUALS, state));
            }
        }
        if (isNotBlank(investorType)) {
            final List<String> investorTypes = Arrays.asList(investorType.split(","));
            for (String type : investorTypes) {
                filterCriteria.add(new ApiSearchCriteria(type, EQUALS, type));
            }
        }

        if (searchClientOnly) {
            filterCriteria.add(new ApiSearchCriteria(ApiConstants.DISPLAY_NAME, CONTAINS, queryString));

            return new Sort<>(new SearchByCriteria<>(V2_0, clientSearchDtoService, filterCriteria),
                    ApiConstants.DISPLAY_NAME).performOperation();
        } else {
            return new Sort<>(new ServiceFilter<>(V2_0, clientSearchDtoService, queryString, filterCriteria),
                    ApiConstants.DISPLAY_NAME).performOperation();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/individuals/search")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
    public
    @ResponseBody
    ApiResponse getSearchedClients(@RequestParam(required = false, value = ApiConstants.QUERY) String queryString,
                                   @RequestParam(required = false, value = ApiConstants.ADVISERID) String adviserId) {
        final Collection<BrokerIdentifier> advisersForUser = brokerIntegrationService.getAdvisersForUser(profileService.getActiveProfile(),
                new FailFastErrorsImpl());
        final ApiSearchCriteria queryCriteria = new ApiSearchCriteria(Attribute.CLIENTS, CONTAINS, queryString);
        final SearchByCriteria<ClientIdentificationDto> searchByCriteria = new SearchByCriteria<>(ApiVersion.CURRENT_VERSION,
                existingClientSearchDtoService, queryCriteria);
        final List<ApiSearchCriteria> filterCriteria = new ArrayList<>();
        filterCriteria.add(new ApiSearchCriteria(Attribute.ID_VERIFIED, EQUALS, "true"));
        filterCriteria.add(new ApiSearchCriteria(Attribute.INVESTOR_TYPE, EQUALS, "Individual"));
        filterCriteria.add(new ApiSearchCriteria(Attribute.INDIVIDUAL_INVESTOR, EQUALS, "true"));
        if (advisersForUser.size() > 1) {
            filterCriteria.add(new ApiSearchCriteria("adviserPositionIds", LIST_CONTAINS, EncodedString.toPlainText(adviserId)));
        }

        final BeanFilter beanFilter = new BeanFilter(ApiVersion.CURRENT_VERSION, searchByCriteria, Strictness.ALL, filterCriteria);
        return beanFilter.performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clients/{client-id}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_client_details')")
    public
    @ResponseBody
    ApiResponse getClientDetails(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientId) {
        return new FindByKey<>(V2_0, clientListDtoService, new ClientKey(clientId)).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/clients/{client-id}/accounts/account-id/update")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'client.details.update')")
    public
    @ResponseBody
    ApiResponse update(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientId,
                       @ModelAttribute ClientTxnDto clientTxnDto) {
        if (!profileService.isInvestor() && !profileService.isEmulating()) {
            final ClientKey key = new ClientKey(clientId);
            clientTxnDto.setKey(key);
            return new Update<>(V2_0, clientListDtoService,
                    clientDetailsDtoErrorMapper, clientTxnDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @InitBinder("clientTxnDto")
    public void updateInitBinder(WebDataBinder binder) {
        binder.setAllowedFields("updatedAttribute", "preferredName", "resiCountryCodeForTax", "resiCountryforTax",
                "registrationForGst", "fullName", "registrationState", "registrationStateCode", "exemptionReason", "tfn",
                "tfnExemptId", "saTfnExemptId", "modificationSeq", "tfnProvided", "investorTypeUpdated", "cisKey",
                "addresses[*].*", "emails[*].*", "emails[*].*", "taxResidenceCountries[*].*", "warnings[*]*");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clients/filter")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_client_reports')")
    public
    @ResponseBody
    ApiResponse getClientListFilters() {
        return new FindOne<>(V2_0, clientListFilterDtoService).performOperation();
    }
}
