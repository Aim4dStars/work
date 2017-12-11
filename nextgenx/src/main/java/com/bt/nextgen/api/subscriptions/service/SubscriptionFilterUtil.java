package com.bt.nextgen.api.subscriptions.service;

import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionFilterUtil {

    private List<SubscriptionDto> subscriptions;
    private List<ApiSearchCriteria> criteriaList;

    public SubscriptionFilterUtil(List<SubscriptionDto> subscriptions, List<ApiSearchCriteria> criteriaList) {
        this.subscriptions = subscriptions;
        this.criteriaList= criteriaList;
    }

    public List<SubscriptionDto> filter() {
        List<SubscriptionDto> filteredSubscriptions = new ArrayList<>();
        for (SubscriptionDto dto : subscriptions) {
            if (matches(dto)) {
                filteredSubscriptions.add(dto);
            }
        }
        return filteredSubscriptions;
    }

    private boolean matches(SubscriptionDto dto) {
        boolean isFilter = false;
        for (ApiSearchCriteria searchCriteria : criteriaList) {
            try
            {
                String beanPropertyValue = BeanUtils.getProperty(dto, searchCriteria.getProperty());
                String expected = searchCriteria.getValue();
                ApiSearchCriteria.SearchOperation operation = searchCriteria.getOperation();
                switch (operation) {
                    case EQUALS:
                        isFilter = Matchers.equalTo(expected).matches(beanPropertyValue);
                        break;
                    case BETWEEN:
                        isFilter = compareValueInRange(beanPropertyValue, expected, searchCriteria.getOperationType());
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported filter " + operation);
                }
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, "unable to filter bean", e);
            }
        }
        return isFilter;
    }

    private boolean compareValueInRange(String value, String compareValue, ApiSearchCriteria.OperationType operationType) {
        String[] rangeValues = StringUtils.split(compareValue, ",");
        String startValue = formatCompareValue(rangeValues[0], operationType);
        String endValue = formatCompareValue(rangeValues[1], operationType);
        return Matchers.both(Matchers.greaterThanOrEqualTo(startValue)).
                and(Matchers.lessThanOrEqualTo(endValue)).matches(value);
    }

    private String formatCompareValue(String compareValue, ApiSearchCriteria.OperationType operationType) {
        if (ApiSearchCriteria.OperationType.DATE.equals(operationType)) {
            DateTime formattedDate = new DateTime(compareValue);
            return formattedDate.toString();
        }
        else
            return compareValue;
    }
}
