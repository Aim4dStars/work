package com.bt.nextgen.api.account.v1.util;

import com.bt.nextgen.api.account.v1.model.transitions.TransitionAccountDto;
import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.api.client.util.FilterMatcher;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by L069552 on 20/09/2015.
 */
@Deprecated
public class TransitionFilterMatcher extends FilterMatcher<AccountKey,TransitionAccountDto> {

    private List<ApiSearchCriteria> criteriaList;

    public TransitionFilterMatcher(List<ApiSearchCriteria> criteriaList,Map<AccountKey,TransitionAccountDto> transitionAccountDtoMap){

        super(transitionAccountDtoMap);
        this.criteriaList = criteriaList;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.bt.nextgen.api.client.util.FilterMatcher#matchesSafely(java.lang.
     * Object)
     */
    @Override
    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    public boolean matchesSafely(TransitionAccountDto transitionAccountDto) {
        Boolean isFiltered = false;
        for (ApiSearchCriteria criteria : criteriaList) {
            final String value = getPropertyValue(transitionAccountDto, criteria.getProperty());
            isFiltered = isFiltered(criteria, value);
            if (!isFiltered) {
                break;
            }
        }
        return isFiltered;
    }

    private boolean isFiltered(ApiSearchCriteria criteria,String value) {
        boolean isFilter = false;
        switch (criteria.getOperation()) {
            case EQUALS:
                isFilter = Matchers.equalTo(criteria.getValue()).matches(value);
                break;
            case NEG_EQUALS:
                isFilter = !Matchers.equalTo(criteria.getValue()).matches(value);
                break;

            default:
                break;
        }
        return isFilter;
    }

}
