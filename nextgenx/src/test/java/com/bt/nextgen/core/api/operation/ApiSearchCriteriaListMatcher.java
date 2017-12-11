package com.bt.nextgen.core.api.operation;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Matcher for a list of {@link ApiSearchCriteria}.
 */
public class ApiSearchCriteriaListMatcher extends BaseMatcher<List<ApiSearchCriteria>> {
    private List<ApiSearchCriteria> expectedCriteriaList;

    /**
     * Ctor.
     *
     * @param criteriaArray Array of criteria to match.
     */
    public ApiSearchCriteriaListMatcher(ApiSearchCriteria... criteriaArray) {
        this.expectedCriteriaList = asList(criteriaArray);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("List<ApiSearchCriteria>");
    }

    @Override
    public boolean matches(Object item) {
        final List<ApiSearchCriteria> criteriaList = (List<ApiSearchCriteria>) item;

        if (criteriaList == null) {
            return false;
        }

        if (criteriaList.size() != expectedCriteriaList.size()) {
            return false;
        }

        for (int i = 0; i < criteriaList.size(); i++) {
            final ApiSearchCriteria criteria = criteriaList.get(i);
            final ApiSearchCriteria expectedCriteria = expectedCriteriaList.get(i);

            if (!criteria.getOperation().equals(expectedCriteria.getOperation())
                    || !criteria.getOperationType().equals(expectedCriteria.getOperationType())
                    || !criteria.getProperty().equals(expectedCriteria.getProperty())
                    || !criteria.getValue().equals(expectedCriteria.getValue())) {
                return false;
            }
        }

        return true;
    }
}
