package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.SuperfundDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.rollover.CashRolloverService;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CashRolloverDtoServiceImpl implements CashRolloverDtoService {

    @Autowired
    private CashRolloverService rolloverService;

    public List<SuperfundDto> findAll(ServiceErrors serviceErrors) {

        List<SuperfundDto> superfunds = new ArrayList<>();
        List<SuperfundDetails> funds = rolloverService.loadAvailableSuperfunds(serviceErrors);

        if (funds != null) {
            for (SuperfundDetails fund : funds) {
                SuperfundDto fDto = new SuperfundDto(fund.getUsi(), fund.getValidFrom(), fund.getValidTo(), fund.getAbn(),
                        fund.getOrgName(), fund.getProductName());
                superfunds.add(fDto);
            }
        }

        return superfunds;
    }

    @Override
    public List<SuperfundDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<SuperfundDto> funds = findAll(serviceErrors);
        if (criteriaList == null || criteriaList.isEmpty()) {
            return funds;
        }

        String query = null;
        for (ApiSearchCriteria parameter : criteriaList) {
            switch (parameter.getProperty()) {
                case "query":
                    query = parameter.getValue();
                    break;
                default:
                    break;
            }
        }

        // Determine if an exact match is required.
        if (query.split(":").length == 3) {
            return Collections.singletonList(exactMatchFundDetails(funds, query));
        }
        return flexibleMatch(query, funds);
    }

    /**
     * Retrieve the fund which matches the ABN, USI and product-name specified in the array.
     * 
     * @param funds
     * @param query
     * @return
     */
    private SuperfundDto exactMatchFundDetails(List<SuperfundDto> funds, String query) {
        final String abnLbl = " ABN: ";
        final String usiLbl = " USI: ";

        String pdtName = query.substring(0, query.indexOf(abnLbl));
        String abn = query.substring(query.indexOf(abnLbl) + abnLbl.length(), query.indexOf(usiLbl));
        String usi = query.substring(query.indexOf(usiLbl) + usiLbl.length());

        for (SuperfundDto fund : funds) {
            boolean match = usi.equals(fund.getUsi()) && abn.equals(fund.getAbn()) && pdtName.equals(fund.getProductName());
            if (match) {
                return fund;
            }
        }
        return null;
    }

    /**
     * Flexible match method where the method returns true as long as the specified query string contains the corresponding fund's
     * values (and vice versa).
     * 
     * @param query
     * @param funds
     * @return
     */
    private List<SuperfundDto> flexibleMatch(String query, List<SuperfundDto> funds) {
        List<SuperfundDto> result = new ArrayList<>();
        for (SuperfundDto fund : funds) {
            boolean fitCriteria = matchStringValue(query, fund.getUsi()) || matchStringValue(query, fund.getAbn())
                    || matchStringValue(query, fund.getProductName());
            if (fitCriteria) {
                result.add(fund);
            }
        }
        return result;
    }

    /**
     * Determines if the specified queryString matches the value provided. If either of the value is null, return false.
     * 
     * @param queryString
     * @param value
     * @return
     */
    private boolean matchStringValue(String queryString, String value) {
        if (StringUtils.isNotEmpty(queryString) && StringUtils.isNotEmpty(value)) {
            // Convert to lower case
            String lqueryString = queryString.toLowerCase();
            String lvalue = value.toLowerCase();

            return lqueryString.equals(lvalue) || lvalue.contains(lqueryString);
        }

        return Boolean.FALSE;
    }
}
