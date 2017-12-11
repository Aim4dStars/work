package com.bt.nextgen.api.account.v3.util;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.client.util.FilterMatcher;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Class AccountFilterMatcher.
 */
public class AccountFilterMatcher extends FilterMatcher<AccountKey, AccountDto> {

    /** The criteria list. */
    private List<ApiSearchCriteria> criteriaList;

    /**
     * Instantiates a new account filter matcher.
     * 
     * @param criteriaList
     *            the criteria list
     * @param accountDtoMap
     *            the account dto map
     */
    public AccountFilterMatcher(List<ApiSearchCriteria> criteriaList, Map<AccountKey, AccountDto> accountDtoMap) {
        super(accountDtoMap);
        this.criteriaList = criteriaList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * FilterMatcher#matchesSafely(java.lang.
     * Object)
     */
    @Override
    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    public boolean matchesSafely(AccountDto accountDto) {
        Boolean isFiltered = false;
        for (ApiSearchCriteria criteria : criteriaList) {
            final String value = getPropertyValue(accountDto, criteria.getProperty());
            isFiltered = isFiltered(criteria, accountDto, value);
            if (!isFiltered) {
                break;
            }
        }
        return isFiltered;
    }

    private boolean isFiltered(ApiSearchCriteria criteria, AccountDto accountDto, String value) {
        boolean isFilter = false;
        switch (criteria.getOperation()) {
        case EQUALS:
            isFilter = Matchers.equalTo(criteria.getValue()).matches(value);
            break;
        case NEG_EQUALS:
            isFilter = !Matchers.equalTo(criteria.getValue()).matches(value);
            break;
        case GREATER_THAN:
            isFilter = new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) > 0;
            break;
        case LESS_THAN:
            isFilter = new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) < 0;
            break;
        case NEG_LESS_THAN:
            isFilter = new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) >= 0;
            break;
        case NEG_GREATER_THAN:
            isFilter = new BigDecimal(value).compareTo(new BigDecimal(criteria.getValue())) <= 0;
            break;
        case CONTAINS:
            isFilter = searchFilter(accountDto, criteria.getValue());
            break;
        case LIST_CONTAINS:
            final String[] criteriaValues = criteria.getValue() != null ? criteria.getValue().split(",") : new String[]{};
            for (String criteriaValue : criteriaValues) {
                if (Matchers.equalTo(criteriaValue).matches(value)) {
                    isFilter = true;
                    break;
                }
            }
            break;
        default:
            break;
        }
        return isFilter;
    }

    /**
     * Search filter.
     * 
     * @param accountDto
     *            the account dto
     * @param value
     *            the value
     * @return true, if successful
     */
    private boolean searchFilter(AccountDto accountDto, String value) {
        final Pattern pattern = SearchUtil.getPattern(value);
        if (pattern != null && SearchUtil.matches(pattern, accountDto.getAccountName(), accountDto.getAccountNumber())) {
            return true;
        }
        return false;
    }
}
