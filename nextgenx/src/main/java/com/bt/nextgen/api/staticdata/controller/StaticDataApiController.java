package com.bt.nextgen.api.staticdata.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.staticdata.service.StaticDataDtoService;
import com.bt.nextgen.core.api.model.ApiError;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.Group;
import com.bt.nextgen.core.api.operation.SearchByCriteria;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_VERSION_API;
import static com.bt.nextgen.core.api.UriMappingConstants.STATIC_DATA;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.NEG_EQUALS;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = { CURRENT_VERSION_API, CURRENT_DIRECT_ONBOARDING_VERSION_API}, produces = MediaType.APPLICATION_JSON_VALUE)
public class StaticDataApiController
{
    /**
     * See also "btfg$order_by".
     */
	private static final String PANORAMA_DISPLAY_FIELD = "field:btfg$is_panorama_val";

	private static final String PANORAMA_DISPLAY_IGNORE = "-";

    private static final String PERSON_IDENT_TYPE_FIELD = "field:btfg$is_ident_exempt";

    private static final String PERSON_IDENT_TYPE_VAL =  "+";


    @Autowired
	private StaticDataDtoService staticDataDtoService;

	@RequestMapping(method = GET, value = STATIC_DATA)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getStaticCodes(@RequestParam(required = false) String criteria,
			@RequestParam(required = false) String[] category,
			@RequestParam(required = false, defaultValue = "false") boolean panorama,@RequestParam(required = false, defaultValue = "false")boolean isIdentificationValueProvided)
	{
		final List<ApiSearchCriteria> searchCriteria = new ArrayList<>();
		if (hasText(criteria)) {
			searchCriteria.addAll(ApiSearchCriteria.parseQueryString(CURRENT_VERSION, criteria));
		}
		if (category != null) {
			for (String cat : category) {
				searchCriteria.add(new ApiSearchCriteria("category", cat));
			}
		}
		if (searchCriteria.isEmpty()) {
			return new ApiResponse(CURRENT_VERSION, new ApiError(SC_BAD_REQUEST, "Please specify either criteria or category"));
		}
		if (panorama) {
			searchCriteria.add(new ApiSearchCriteria(PANORAMA_DISPLAY_FIELD, NEG_EQUALS, PANORAMA_DISPLAY_IGNORE));
		}
        if(isIdentificationValueProvided){
            searchCriteria.add(new ApiSearchCriteria(PERSON_IDENT_TYPE_FIELD, EQUALS, PERSON_IDENT_TYPE_VAL));
        }

		return new Group<>(CURRENT_VERSION, new SearchByCriteria<>(CURRENT_VERSION, staticDataDtoService,
				searchCriteria), "listName").performOperation();
	}
}
