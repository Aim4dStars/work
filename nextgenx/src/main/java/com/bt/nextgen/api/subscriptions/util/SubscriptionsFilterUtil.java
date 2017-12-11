package com.bt.nextgen.api.subscriptions.util;

import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import org.apache.commons.beanutils.BeanUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionsFilterUtil {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionsFilterUtil.class);

    private List<ApiSearchCriteria> criteriaList;

    private List<SubscriptionDto> subscriptionDtos;

    public SubscriptionsFilterUtil(List<ApiSearchCriteria> criteriaList, List<SubscriptionDto> subscriptionDtos) {
        this.subscriptionDtos = subscriptionDtos;
        this.criteriaList= criteriaList;
    }

    public List<SubscriptionDto> filter() {
        List<SubscriptionDto> filteredDtos = new ArrayList<>();
        boolean isFilter = false;
        for (ApiSearchCriteria criteria : criteriaList) {
            for (SubscriptionDto subscriptionDto : subscriptionDtos){
                String value = getPropertyValue(subscriptionDto, criteria.getProperty());
                switch (criteria.getOperation())
                {
                    case EQUALS:
                        isFilter = Matchers.equalTo(criteria.getValue()).matches(value);
                        break;
                    case NEG_EQUALS:
                        isFilter = !Matchers.equalTo(criteria.getValue()).matches(value);
                        break;
                    default:
                        break;
                }
                if(isFilter) {
                    filteredDtos.add(subscriptionDto);
                }
            }
        }
        return filteredDtos;
    }

    private String getPropertyValue(SubscriptionDto subscriptionDto, String property) {
        String value = null;
        try {
            value = BeanUtils.getProperty(subscriptionDto, property);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Exception while getting property {} of object", property, subscriptionDto.getClass().getName(), e);
        }
        return value;
    }
}