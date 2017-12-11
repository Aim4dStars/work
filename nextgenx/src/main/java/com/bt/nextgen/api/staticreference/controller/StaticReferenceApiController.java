package com.bt.nextgen.api.staticreference.controller;

import com.bt.nextgen.api.staticreference.service.StaticReferenceDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.Group;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_VERSION_API;

/**
 * Created by M035801 on 21/07/2016.
 */
@Controller
@RequestMapping(value = CURRENT_VERSION_API)
public class StaticReferenceApiController
{
    @Autowired
    StaticReferenceDtoService staticReferenceDtoService;


    @RequestMapping(method = RequestMethod.GET, value = "staticreference")
    public @ResponseBody ApiResponse getStaticReferences(@RequestParam String category)
    {
        if (StringUtils.isEmpty(category))
        {
            throw new IllegalArgumentException("category is invalid");
        }

        ApiSearchCriteria criteria = new ApiSearchCriteria("category", ApiSearchCriteria.SearchOperation.EQUALS, category, ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
        searchCriteriaList.add(criteria);

        return new Group<>(CURRENT_VERSION, new SearchByCriteria<>(CURRENT_VERSION, staticReferenceDtoService, searchCriteriaList), "category").performOperation();
    }
}