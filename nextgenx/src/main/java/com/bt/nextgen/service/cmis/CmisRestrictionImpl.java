package com.bt.nextgen.service.cmis;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.integration.financialdocument.Restriction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Implementation of @see com.bt.nextgen.service.integration.financialdocument.Restriction interface.
 */
public class CmisRestrictionImpl implements Restriction {

    private StringBuilder restrictionQuery = new StringBuilder();

    @Override
    public void and(Restriction restriction) {
        restrictionQuery.append(" AND (");
        restrictionQuery.append(restriction.toString());
        restrictionQuery.append(" )");
    }

    @Override
    public void and(Restriction... restrictions) {
        addMultiple("AND", restrictions);
    }

    @Override
    public void or(Restriction restriction) {
        restrictionQuery.append(" OR (");
        restrictionQuery.append(restriction.toString());
        restrictionQuery.append(" )");
    }

    @Override
    public void or(Restriction... restrictions) {
        addMultiple("OR", restrictions);
    }

    @Override
    public void like(String property, Object value) {
        restrictionQuery.append(" " + property + " like '%" + value.toString() + "%'");
    }

    @Override
    public void equalTo(String property, Object value) {
        restrictionQuery.append(" " + property + " = " + dataConverter(value));
    }

    @Override
    public void notEqual(String property, Object value) {
        restrictionQuery.append(" " + property + " <> " + dataConverter(value));
    }

    @Override
    public void in(String property, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new BadRequestException("property value list can not be empty.");
        }
        restrictionQuery.append(" " + property + " IN ('" + StringUtils.join(values, "','") + "')");

    }

    @Override
    public void notIn(String property, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            throw new BadRequestException("property value list can not be empty.");
        }
        restrictionQuery.append(" " + property + " NOT IN ('" + StringUtils.join(values, "','") + "')");
    }


    @Override
    public void lessThan(String property, Object value) {
        restrictionQuery.append(" " + property + " < " + dataConverter(value));
    }

    @Override
    public void greaterThan(String property, Object value) {
        restrictionQuery.append(" " + property + " > " + dataConverter(value));
    }

    @Override
    public void greaterThanEqual(String property, Object value) {
        restrictionQuery.append(" " + property + " >= " + dataConverter(value));
    }

    @Override
    public void lessThanEqual(String property, Object value) {
        restrictionQuery.append(" " + property + " <= " + dataConverter(value));
    }

    private void addMultiple(String andOr, Restriction... restrictions) {
        if (restrictions != null && restrictions.length > 0) {
            restrictionQuery.append(" ");
            restrictionQuery.append(andOr);
            restrictionQuery.append(" (");
            for (Restriction restriction : restrictions) {
                restrictionQuery.append(restriction.toString());
            }
            restrictionQuery.append(")");
        }
    }

    @Override
    public void isNull(String property) {
        restrictionQuery.append(" " + property + " IS NULL");
    }

    @Override
    public void isNotNull(String property) {
        restrictionQuery.append(" " + property + " IS NOT NULL");
    }

    @Override
    public void inTree(String folderId) {
        restrictionQuery.append(" IN_TREE "+ "('"+folderId+"') ");
    }

    @Override
    public String toString() {
        return restrictionQuery.toString();
    }

    private String dataConverter(Object inputValue) {
        String returnValue = null;
        if (inputValue instanceof String) {
            returnValue = "\'" + inputValue + "\'";
        } else if (inputValue instanceof DateTime) {
            returnValue = "timestamp \'" + inputValue.toString() + "\'";
        } else {
            returnValue = inputValue.toString();
        }
        return returnValue;
    }
}
