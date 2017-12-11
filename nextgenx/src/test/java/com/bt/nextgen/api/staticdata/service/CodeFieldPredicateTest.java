package com.bt.nextgen.api.staticdata.service;

import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.LIST_CONTAINS;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.NEG_EQUALS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CodeFieldPredicateTest {

    private static final String FIELD_NAME = "fieldName";

    private CodeFieldPredicate predicate;

    @Mock
    private Code code;

    @Mock
    private Field field;

    @Before
    public void initCode() {
        when(code.getField(FIELD_NAME)).thenReturn(field);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void applyWithUnsupportedOperation() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "+", LIST_CONTAINS);
        when(field.getValue()).thenReturn(null);
        predicate.apply(code);
    }

    @Test
    public void applyWithEqualsAndNullValue() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "+");
        when(field.getValue()).thenReturn(null);
        assertFalse(predicate.apply(code));
    }

    @Test
    public void applyWithEqualsAndCorrectValue() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "+");
        when(field.getValue()).thenReturn("+");
        assertTrue(predicate.apply(code));
    }

    @Test
    public void applyWithEqualsAndIncorrectValue() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "+");
        when(field.getValue()).thenReturn("-");
        assertFalse(predicate.apply(code));
    }

    @Test
    public void applyWithNotEqualsAndNullValue() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "-", NEG_EQUALS);
        when(field.getValue()).thenReturn(null);
        assertTrue(predicate.apply(code));
    }

    @Test
    public void applyWithNotEqualsAndCorrectValue() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "-", NEG_EQUALS);
        when(field.getValue()).thenReturn("+");
        assertTrue(predicate.apply(code));
    }

    @Test
    public void applyWithNotEqualsAndIncorrectValue() throws Exception {
        predicate = new CodeFieldPredicate(FIELD_NAME, "-", NEG_EQUALS);
        when(field.getValue()).thenReturn("-");
        assertFalse(predicate.apply(code));
    }
}
