package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Restriction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class for Criteria interface
 */
public class CmisCriteriaImpl implements Criteria {

    private List<Restriction> restrictionList = new ArrayList<Restriction>();

    @Override
    public List<Restriction> getRestrictionList() {
        return restrictionList;
    }

    @Override
    public Restriction createRestriction() {
        return new CmisRestrictionImpl();
    }

    @Override
    public String applyCriteria() {
        StringBuilder criteriaString = new StringBuilder("WHERE");
        if(!CollectionUtils.isEmpty(restrictionList)){
            for (Restriction restriction: restrictionList) {
                criteriaString.append(restriction);
            }
        }
        else {
            return StringUtils.EMPTY;
        }
        return criteriaString.toString();
    }

    @Override
    public void add(Restriction restriction) {
        restrictionList.add(restriction);
    }

    @Override
    public Restriction and(Restriction... restrictions) {
        Restriction andRestriction = new CmisRestrictionImpl();
        andRestriction.and(restrictions);
        return andRestriction;
    }

    @Override
    public Restriction and(Restriction restriction) {
        Restriction andRestriction = new CmisRestrictionImpl();
        andRestriction.and(restriction);
        return andRestriction;
    }

    @Override
    public Restriction or(Restriction restriction) {
        Restriction orRestriction = new CmisRestrictionImpl();
        orRestriction.or(restriction);
        return orRestriction;
    }

    @Override
    public Restriction or(Restriction... restrictions) {
        Restriction andRestriction = new CmisRestrictionImpl();
        andRestriction.or(restrictions);
        return andRestriction;
    }

    @Override
    public Restriction like(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.like(property, value);
        return restriction;
    }

    @Override
    public Restriction equalTo(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.equalTo(property, value);
        return restriction;
    }

    @Override
    public Restriction notEqual(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.notEqual(property, value);
        return restriction;
    }

    @Override
    public Restriction in(String property, List<String> values) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.in(property, values);
        return restriction;
    }

    @Override
    public Restriction notIn(String property, List<String> values) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.notIn(property, values);
        return restriction;
    }

    @Override
    public Restriction lessThan(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.lessThan(property, value);
        return restriction;
    }

    @Override
    public Restriction greaterThan(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.greaterThan(property, value);
        return restriction;
    }

    @Override
    public Restriction lessThanEqual(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.lessThanEqual(property, value);
        return restriction;
    }

    @Override
    public Restriction greaterThanEqual(String property, Object value) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.greaterThanEqual(property, value);
        return restriction;
    }

    @Override
    public Restriction isNull(String property) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.isNull(property);
        return restriction;
    }

    @Override
    public Restriction isNotNull(String property) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.isNotNull(property);
        return restriction;
    }

    @Override
    public Restriction inTree(String folderId) {
        Restriction restriction = new CmisRestrictionImpl();
        restriction.inTree(folderId);
        return restriction;
    }
}
