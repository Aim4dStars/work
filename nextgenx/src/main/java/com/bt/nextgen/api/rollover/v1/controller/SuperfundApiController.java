package com.bt.nextgen.api.rollover.v1.controller;

import com.bt.nextgen.api.rollover.v1.service.CashRolloverDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(produces = "application/json")
public class SuperfundApiController {

    @Autowired
    private CashRolloverDtoService rolloverService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.rollover.v1.uri.superfunds}", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    ApiResponse searchSuperfunds(@RequestParam(value = "query", required = false) String query) {
        final ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, query,
                OperationType.STRING);
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        if (StringUtils.isNotBlank(query)) {
            criteriaList.add(queryCriteria);
        }

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, rolloverService, criteriaList).performOperation();
    }
}
