package com.bt.nextgen.api.superannuation.caps.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.superannuation.caps.service.ContributionCapsDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByKeyedCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.UriMappingConstants.SUPER_CONTRIBUTIONS_CAPS;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class SuperContributionCapController {
    @Autowired
    private ContributionCapsDtoService contributionCapsDtoService;

    /**
     * Retrieve contribution caps for a super account
     *
     * @param accId encoded account id
     * @param date  starting date of the financial year to return caps for. Date format 'YYYY-MM-DD'.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = SUPER_CONTRIBUTIONS_CAPS)
    @ResponseBody
    public ApiResponse getSuperContributionCaps(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
            @RequestParam("date") String date) {
        AccountKey accountKey = null;

        if (StringUtils.isNotEmpty(accId)) {
            accountKey = new AccountKey(EncodedString.toPlainText(accId));
        }
        else {
            throw new IllegalArgumentException("Account id provided was null");
        }

        if (!StringUtils.isNotEmpty(date)) {
            throw new IllegalArgumentException("financial year provided was null");
        }

        ApiSearchCriteria dateCritiera = new ApiSearchCriteria("date", ApiSearchCriteria.SearchOperation.EQUALS, date,
                ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(dateCritiera);

        return new SearchByKeyedCriteria<>(ApiVersion.CURRENT_VERSION, contributionCapsDtoService,
                accountKey, criteriaList).performOperation();
    }
}