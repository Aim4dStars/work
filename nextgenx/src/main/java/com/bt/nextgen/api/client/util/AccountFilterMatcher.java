package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;

import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by L062329 on 22/12/2014.
 */
public class AccountFilterMatcher extends  FilterMatcher<AccountKey, AccountDto>{

   private List<ApiSearchCriteria> criteriaList;

    public AccountFilterMatcher(List<ApiSearchCriteria> criteriaList, Map<AccountKey, AccountDto> accountDtoMap) {
       super(accountDtoMap);
       this.criteriaList= criteriaList;
    }

    public boolean matchesSafely(AccountDto accountDto) {
        boolean isFilter = false;
        for (ApiSearchCriteria criteria : criteriaList) {
            String value = getPropertyValue(accountDto, criteria.getProperty());
            switch (criteria.getOperation()) {
                case LIST_CONTAINS:
                    isFilter = listSearch(value, criteria.getValue());
                    break;
                case EQUALS:
                    isFilter = Matchers.equalTo(criteria.getValue()).matches(value);
                    break;
                case NEG_EQUALS:
                    isFilter = !Matchers.equalTo(criteria.getValue()).matches(value);
                    break;
                case GREATER_THAN:
                    isFilter = (new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) > 0 ? true : false);
                    break;
                case LESS_THAN:
                    isFilter = (new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) < 0 ? true : false);
                    break;
                case NEG_LESS_THAN:
                    isFilter = (new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) >= 0 ? true : false);
                    break;
                case NEG_GREATER_THAN:
                    isFilter = (new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) <= 0 ? true : false);
                    break;
                case CONTAINS:
                    isFilter = searchFilter(accountDto, criteria.getValue());
                    break;
            }
            if(!isFilter) {
                break;
            }
        }
        return isFilter;
    }

    private boolean listSearch(String beanValue , String criteriavalue) {
        boolean isFilter = false;
        String[] criteriaValues = criteriavalue.split(",");
        for(String criteria : criteriaValues ) {
            if(Matchers.equalTo(criteria).matches(beanValue)) {
                isFilter = true;
                break;
            }
        }
        return isFilter;
    }

    private boolean searchFilter(AccountDto accountDto, String value) {
        Pattern pattern = SearchUtil.getPattern(value);
        if (pattern != null && SearchUtil.matches(pattern, accountDto.getAccountName(), accountDto.getAccountNumber())) {
            return true;
        }
        return false;
    }
}
