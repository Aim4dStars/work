package com.bt.nextgen.api.client.util;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by L062329 on 29/12/2014.
 */
public abstract class FilterMatcher<T,K> {

    private static final Logger logger = LoggerFactory.getLogger(FilterMatcher.class);

    private Map<T, K> map;

    protected FilterMatcher(Map<T, K> map) {
        this.map = map;
    }

    public abstract boolean matchesSafely(K object);

    public Map<T,K> filter() {
        Map<T,K> filteredMap = new HashMap<>();
        Set<T> keys = map.keySet();
        for (T key : keys) {
            K k = map.get(key);
            if(matchesSafely(k)) {
                filteredMap.put(key,  k);
            }
        }
        return filteredMap;
    }

    protected String getPropertyValue(K object, String property) {
        String value = null;
        try {
            value = BeanUtils.getProperty(object, property);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //log this error as INFO only (if it's a real exception it should throw it and not return)
            logger.info("Exception while getting property: {} of object: {}", property, object.getClass().getName());
        }
        return value;
    }
}
