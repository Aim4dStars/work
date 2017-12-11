package com.bt.nextgen.api.client.controller;

import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.service.ClientKeyDtoService;
import com.bt.nextgen.api.client.service.CustomerDataDto;
import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.DirectInvestorDataDtoService;
import com.bt.nextgen.api.client.service.DirectInvestorStatusDtoService;
import com.bt.nextgen.api.client.service.GlobalCustomerDtoService;
import com.bt.nextgen.api.client.validation.ClientDetailsDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.security.SamlAuthenticationDetailsSource;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerDataApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerDataApiController.class);
    @Autowired
    private CustomerDataDtoService customerDataDtoService;

    @Autowired
    private DirectInvestorDataDtoService directInvestorDataDtoService;

    @Autowired
    private ClientDetailsDtoErrorMapper clientDetailsDtoErrorMapper;

    @Autowired
    private GlobalCustomerDtoService globalCustomerDtoService;

    @Autowired
    private ClientKeyDtoService clientKeyDtoService;

    @Autowired
    private FeatureTogglesService togglesService;

    @Autowired
    private SamlAuthenticationDetailsSource samlSource;

    @Autowired
    private DirectInvestorStatusDtoService directInvestorStatusDtoService;

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.CUSTOMER_DATA)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_client_details')")
    @ResponseBody
    public ApiResponse getCustomerData(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientId,
                                @RequestParam(value = "type", required = true) String updateType,
                                @RequestParam(value = "cis", required = true) String cisKey,
                                @RequestParam(value = "clientType", required = true) String clientType) {
        ClientUpdateKey id = new ClientUpdateKey(clientId, updateType,cisKey,clientType);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, customerDataDtoService, id).performOperation();
    }

    /**
     * Update GCM details for a customer under Westpac Branded Adviser. <br>
     * This API will also deal with the contact details update as described in US34522 @ https://rally1.rallydev.com/#/detail/userstory/155207392996?fdp=true <br>
     *  <b>This API must be used only for customers that are <i>new to Panorama</i>. For existing Panorama customers please use the "<i>/clients/{client-id}/update</i>" API.</b>
     *
     * @param customerDataDto  contains CIS key , attribute to be updated(tin, address, email  etc.)
     * @return ApiResponse dto
     */
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.GLOBAL_CUSTOMER_UPDATE)
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_client_details')")
    @ResponseBody
    public ApiResponse globalUpdate(@ModelAttribute ("customerDataDto") CustomerDataDto customerDataDto) {
        ClientUpdateKey key = new ClientUpdateKey("", customerDataDto.getUpdatedAttribute(), customerDataDto.getCisKey(), "INDIVIDUAL");
        customerDataDto.setKey(key);
        return new Update<>(ApiVersion.CURRENT_VERSION, customerDataDtoService, clientDetailsDtoErrorMapper, customerDataDto).performOperation();
    }

    @InitBinder("customerDataDto")
    public void globalUpdateBinder(WebDataBinder binder) {
        binder.setAllowedFields("taxResidenceCountries","taxResidenceCountries[*].taxResidenceCountry","taxResidenceCountries[*].tin","taxResidenceCountries[*].taxExemptionReason","taxResidenceCountries[*].startDate","taxResidenceCountries[*].endDate","taxResidenceCountries[*].versionNumber",
                "updatedAttribute", "investorTypeUpdated", "cisKey");
    }

    /**
     * Get GCM details for direct investor (who has no client id in Avaloq)
     *
     * @param type      types of data to retrieve e.g. email, preferred name, address
     * @param cisKey    provided cisKey, this is used to test direct investors without WPL integration ready
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DIRECT_CLIENT_INFO)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getDirectCustomerData(@RequestParam(required = true, value = "type") String type,
                                             @RequestParam(required = false, value = "cisKey") String cisKey,
                                             @RequestParam(required = false, value = "dummy_Tfn") String dummyTfn,
                                             HttpServletRequest request) {
        final boolean wplLiveIntegration = togglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("wplLiveIntegration");
        final String cisKeyVal = wplLiveIntegration ? getCisId(request) : cisKey;
        // Dummy TFN only added for Testing purpose until the supercheck service is ready
        String inputParam = type;
        if (StringUtils.isNotBlank(dummyTfn)) {
            inputParam = inputParam + dummyTfn;
        }
        ClientUpdateKey id = null;
        final String panNumberVal = getPanNumber(request);
        if (StringUtils.isNotBlank(panNumberVal) && panNumberVal.length() == 9) {
           id =  new ClientUpdateKey(panNumberVal, type, cisKeyVal, "INDIVIDUAL");
        }else{
           id =  new ClientUpdateKey("", type, cisKeyVal, "INDIVIDUAL");
        }
        LOGGER.info("wplIntegration: {}; cisKeyVal: {}; type: {}", wplLiveIntegration, cisKeyVal, inputParam);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, directInvestorDataDtoService, id).performOperation();
    }

    /**
     * Get GCM details for customer (who has no client id in Avaloq) under Westpac Branded Adviser
     *
     * @param type      types of data to retrieve e.g. email, preferred name, address
     * @param cisKey    provided cisKey
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.GLOBAL_CUSTOMER_RETRIEVE_BY_OPERATION)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getCustomerDataByOperation(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING)  String cisKey,
                                             @RequestParam(required = true, value = "type") String type) {

        final ClientUpdateKey id = new ClientUpdateKey("", type, cisKey, "INDIVIDUAL");
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, directInvestorDataDtoService, id).performOperation();
    }

    /**
     * Investors from direct onboarding do not have a profile in Avaloq, so we extract their CIS ID from their SAML token
     *
     * @param request request containing the investors SAML token
     * @return CIS key
     */
    private String getCisId(HttpServletRequest request) {
        final Profile profile = samlSource.buildDetails(request);
        if (profile.getToken() != null && profile.getToken().getCISKey() != null) {
            return profile.getToken().getCISKey().getId();
        }
        return null;
    }

    /**
     * Fetch customer demographic details from group-wide Customer Master system.
     * @param cisKey the CIS Key of the customer to be retrieved.
     * @return the relevant customer details, or <b>null</b> if no such customer can be found.
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.GLOBAL_CUSTOMER_RETRIEVE)
    @PreAuthorize("isAuthenticated() and @profileDetailsService.isWestpacBrandedAdviser()")
    @ResponseBody
    public ApiResponse getGlobalCustomer(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String cisKey) {
        LOGGER.info("Fetching GCM details for cisKey: {}", cisKey);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, globalCustomerDtoService, new ClientKey(cisKey)).performOperation();
    }

    /**
     * Get client key for direct customer, if they're an existing Panorama customer
     *
     * @param panNumber    provided panNumber, this is used to test direct investors without WPL integration ready
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DIRECT_CLIENT_KEY)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getClientKeyForDirectCustomer(@RequestParam(required = false, value = "pan") String panNumber,
                                             HttpServletRequest request) {
        final boolean wplLiveIntegration = togglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("wplLiveIntegration");
        final String panNumberVal = wplLiveIntegration ? getPanNumber(request) : panNumber;
        LOGGER.info("wplIntegration: {}; panNumberVal: {}", wplLiveIntegration, panNumberVal);
        ClientKey id = null;
        if (StringUtils.isNotBlank(panNumberVal) && panNumberVal.length() == 9) {
            id = new ClientKey(panNumberVal);
        }
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, clientKeyDtoService, id).performOperation();
    }

    /**
     * Extract direct investor's PAN number form their SAML token
     *
     * @param request request containing the investors SAML token
     * @return PAN number
     */
    private String getPanNumber(HttpServletRequest request) {
        final Profile profile = samlSource.buildDetails(request);
        if (profile.getToken() != null && StringUtils.isNotBlank(profile.getToken().getGcmId())) {
            return profile.getToken().getGcmId();
        }
        return null;
    }

    /**
     * Return a user's PAN from their SAML token
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DIRECT_PAN_NUMBER)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse checkPanoramaNumberInSamlToken() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, directInvestorStatusDtoService).performOperation();
    }
}
