package com.bt.nextgen.core.conversion;

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BigIntegerConverter implements Converter<String, BigInteger> {
    private static final Logger logger = LoggerFactory.getLogger(BigIntegerConverter.class);

    @Override
    public BigInteger convert(String source) {
        try {
            if (!StringUtils.isEmpty(source))
                return new BigInteger(source);
        } catch (NumberFormatException nfe) {
            logger.warn("Error while converting String to BigInteger", nfe);
        }
        return null;
    }
}
