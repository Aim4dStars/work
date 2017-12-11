package com.bt.nextgen.api.staticdata.service;

import ch.lambdaj.function.matcher.Predicate;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;

/**
 * Predicate class to filter out codes that do not have the required external field value.
 */
final class CodeFieldPredicate extends Predicate<Code> {

    private final String fieldName;

    private final String fieldValue;

    private final SearchOperation operation;

    CodeFieldPredicate(String fieldName, String fieldValue, SearchOperation operation) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.operation = operation;
    }

    CodeFieldPredicate(String fieldName, String fieldValue) {
        this(fieldName, fieldValue, EQUALS);
    }

    @Override
    public boolean apply(Code code) {
        final Field field = code.getField(fieldName);
        final boolean result;
        switch (operation) {
            case EQUALS:
                result = checkEquals(field);
                break;
            case NEG_EQUALS:
                result = !checkEquals(field);
                break;
            default:
                throw new UnsupportedOperationException("No support for operation: " + operation);
        }
        return result;
    }

    private boolean checkEquals(Field field) {
        return field != null && fieldValue.equals(field.getValue());
    }
}
