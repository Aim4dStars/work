package com.bt.nextgen.service.integration.financialdocument;

import java.util.List;

/**
 *
 */
public interface Restriction {
    void and(Restriction restriction);

    void and(Restriction... restrictions);

    void or(Restriction restriction);

    void or(Restriction... restrictions);

    void like(String property, Object value);

    void equalTo(String property, Object value);

    void notEqual(String property, Object value);

    void in(String property, List<String> values);

    void notIn(String property, List<String> values);

    void lessThan(String property, Object value);

    void greaterThan(String property, Object value);

    void greaterThanEqual(String property, Object date);

    void lessThanEqual(String property, Object dateTime);

    void isNull(String property);

    void isNotNull(String property);

    void inTree(String folderId);
}
