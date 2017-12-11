package com.bt.nextgen.core.conversion;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BooleanConverter implements Converter<String, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(BooleanConverter.class);

    @Override
    public Boolean convert(String source) {
        try {
            if (StringUtils.isNotEmpty(source))
                return ("Y").equalsIgnoreCase(source)
                        || ("true").equalsIgnoreCase(source) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception e) {
            logger.warn("Error while converting String to Boolean", e);
        }
        return null;
    }
}
