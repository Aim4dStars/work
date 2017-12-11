package com.bt.nextgen.api.country.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.country.model.CountryCode;
import com.bt.nextgen.api.country.service.CountryDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Collate;
import com.bt.nextgen.core.api.operation.ControllerOperation;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Sort;

import static com.bt.nextgen.api.country.model.CountryCode.parseCountryCodes;
import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static com.bt.nextgen.core.api.UriMappingConstants.COUNTRIES;
import static com.bt.nextgen.core.api.UriMappingConstants.COUNTRY_BY_CODE;
import static com.bt.nextgen.core.api.UriMappingConstants.COUNTRY_CODE_URI_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_VERSION_API;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * API front-end controller for serving out country data from the Avaloq static data cache.
 * @author M013938
 */
@Controller
@RequestMapping(value = { CURRENT_VERSION_API, CURRENT_DIRECT_ONBOARDING_VERSION_API}, produces = APPLICATION_JSON_VALUE)
public class CountryApiController {

    private static final Logger LOGGER = getLogger(CountryApiController.class);

    @Autowired
    private CountryDtoService countryDtoService;

    @RequestMapping(method = GET, value = COUNTRIES)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getCountries() {
        return new Sort<>(new FindAll<>(CURRENT_VERSION, countryDtoService), "name").performOperation();
    }

    @RequestMapping(method = GET, value = COUNTRY_BY_CODE)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getCountry(@PathVariable(COUNTRY_CODE_URI_MAPPING) String countryCode) {
        LOGGER.debug("Retrieving details for country codes: [{}]", countryCode);
        final List<CountryCode> codes = parseCountryCodes(countryCode);
        final ControllerOperation operation;
        if (codes.size() == 1) {
            operation = new FindByKey<>(CURRENT_VERSION, countryDtoService, codes.get(0));
        } else {
            final Collate collate = new Collate();
            for (CountryCode code : codes) {
                collate.addOperation(new FindByKey<>(CURRENT_VERSION, countryDtoService, code));
            }
            operation = collate;
        }
        return operation.performOperation();
    }
}
