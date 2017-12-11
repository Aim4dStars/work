package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by L062329 on 23/12/2014.
 */
public class ClientFilterMatcher extends FilterMatcher<ClientKey, ClientDto> {

    private List<ApiSearchCriteria> criteriaList;

    public ClientFilterMatcher(List<ApiSearchCriteria> criteriaList, Map<ClientKey, ClientDto> clientDtoMap) {
        super(clientDtoMap);
        this.criteriaList= criteriaList;
    }

    public boolean matchesSafely(ClientDto clientDto) {
        boolean isFilter = false;
        for (ApiSearchCriteria criteria : criteriaList) {
            String value = getPropertyValue(clientDto, criteria.getProperty());
            switch (criteria.getOperation())
            {
                case EQUALS:
                    isFilter = Matchers.equalTo(criteria.getValue()).matches(value);
                    break;
                case NEG_EQUALS:
                    isFilter = !Matchers.equalTo(criteria.getValue()).matches(value);
                    break;
                case GREATER_THAN:
                    isFilter = compareGreater(value,criteria.getValue());
                    break;
                case LESS_THAN:
                    isFilter = compareLess(value,criteria.getValue());
                    break;
                case NEG_LESS_THAN:
                    isFilter = compareGreaterEquals(value,criteria.getValue());
                    break;
                case NEG_GREATER_THAN:
                    isFilter = compareLessEquals(value,criteria.getValue());
                    break;
                case CONTAINS:
                    isFilter = searchFilter(clientDto, criteria.getValue());
                    break;
            }
            if(!isFilter) {
                break;
            }
        }
        return isFilter;
    }

    private boolean compareGreater(String value, String compareValue) {
        return new BigDecimal(value).compareTo(new BigDecimal(compareValue)) > 0 ? true : false;
    }
    private boolean compareLess(String value, String compareValue) {
        return new BigDecimal(value).compareTo(new BigDecimal(compareValue)) < 0 ? true : false;
    }
    private boolean compareGreaterEquals(String value, String compareValue) {
        return new BigDecimal(value).compareTo(new BigDecimal(compareValue))  >= 0 ? true : false;
    }
    private boolean compareLessEquals(String value, String compareValue) {
        return new BigDecimal(value).compareTo(new BigDecimal(compareValue))  <= 0 ? true : false;
    }

    private boolean searchFilter(ClientDto clientDto, String value) {
        Pattern pattern = SearchUtil.getPattern(value);
        if (pattern != null && SearchUtil.matches(pattern, clientDto.getDisplayName())) {
            return true;
        }
        return false;
    }
}
