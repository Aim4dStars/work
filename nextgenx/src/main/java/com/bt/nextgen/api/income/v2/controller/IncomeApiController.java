package com.bt.nextgen.api.income.v2.controller;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.service.IncomeDetailsDtoService;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;

@Controller("IncomeApiControllerV2")
@RequestMapping(produces = "application/json")
public class IncomeApiController {

    @Autowired
    private IncomeDetailsDtoService incomeDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.income.v2.uri.income}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<IncomeDetailsKey> getIncome(@PathVariable("account-id") String accountId,
            @RequestParam(value = "start-date", required = false) String startDateString,
            @RequestParam(value = "end-date", required = false) String endDateString,
            @RequestParam(value = "income-type", required = true) IncomeDetailsType incomeType) {
        DateTime startDate = new DateTime(startDateString);
        DateTime endDate = new DateTime(endDateString);

        IncomeDetailsKey key = new IncomeDetailsKey(accountId, incomeType, startDate, endDate);

        return new FindByKey<>("v2_0", incomeDtoService, key).performOperation();
    }
}
