package com.bt.nextgen.service.integration.financialdocument;

import java.util.List;

/**
 * Created by L062329 on 16/07/2015.
 */
public interface Criteria {

    void add(Restriction restriction);

    List<Restriction> getRestrictionList();

    Restriction createRestriction();

    String applyCriteria();

    Restriction and(Restriction restriction);

    Restriction and(Restriction... restrictions);

    Restriction or(Restriction restriction);

    Restriction or(Restriction... restrictions);

    Restriction like(String property, Object value);

    Restriction equalTo(String property, Object value);

    Restriction notEqual(String property, Object value);

    Restriction in(String property, List<String> values);

    Restriction notIn(String property, List<String> values);

    Restriction lessThan(String property, Object value);

    Restriction greaterThan(String property, Object value);

    Restriction lessThanEqual(String property, Object value);

    Restriction greaterThanEqual(String property, Object value);

    Restriction isNull(String property);

    Restriction isNotNull(String property);

    Restriction inTree(String folderId);
}
