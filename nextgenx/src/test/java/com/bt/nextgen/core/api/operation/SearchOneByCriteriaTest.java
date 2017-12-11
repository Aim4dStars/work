package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.SearchOneByCriteriaDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link SearchOneByCriteria}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchOneByCriteriaTest {
    private static final String API_VERSION = "api_v1";

    private class MockDto implements Dto {
        @Override
        public String getType() {
            return "MockDto";
        }
    }


    @Mock
    private SearchOneByCriteriaDtoService<MockDto> service;

    @Mock
    private MockDto dto;

    @Mock
    private ServiceError serviceError;

    private ApiSearchCriteria apiCriteria = new ApiSearchCriteria("prop", "value");

    private List<ApiSearchCriteria> criteria;


    @Before
    public void init() {
        criteria = asList(apiCriteria);
    }


    @Test
    public void performOperationSuccessful() {
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, criteria);
        final ApiResponse response;

        when(service.search(eq(criteria), any(ServiceErrors.class))).thenReturn(dto);

        response = searcher.performOperation();
        verify(service).search(eq(criteria), any(ServiceErrors.class));
        assertThat("response exists", response, notNullValue());
        assertThat("response version", response.getApiVersion(), equalTo(API_VERSION));
        assertThat("response contains dto", response.getData(), equalTo((Dto) dto));
    }

    @Test
    public void performOperationSuccessfulWithQueryString() {
        final String searchCriteriaStr = "[{'prop':'attr1','op':'=','val':'value1','type':'string'}]";
        final ApiSearchCriteria searchCriteria = new ApiSearchCriteria("attr1", EQUALS, "value1", STRING);
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, searchCriteriaStr);
        final ApiResponse response;

        when(service.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(dto);

        response = searcher.performOperation();
        verify(service).search(argThat(new ApiSearchCriteriaListMatcher(searchCriteria)), any(ServiceErrors.class));
        assertThat("response exists", response, notNullValue());
        assertThat("response version", response.getApiVersion(), equalTo(API_VERSION));
        assertThat("response contains dto", response.getData(), equalTo((Dto) dto));
    }

    @Test
    public void performOperationSuccessfulWithCriteriaArray() {
        final ApiSearchCriteria searchCriteria = new ApiSearchCriteria("attr1", EQUALS, "value1", STRING);
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, searchCriteria);
        final ApiResponse response;

        when(service.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(dto);

        response = searcher.performOperation();
        verify(service).search(argThat(new ApiSearchCriteriaListMatcher(searchCriteria)), any(ServiceErrors.class));
        assertThat("response exists", response, notNullValue());
        assertThat("response version", response.getApiVersion(), equalTo(API_VERSION));
        assertThat("response contains dto", response.getData(), equalTo((Dto) dto));
    }

    @Test(expected = NotFoundException.class)
    public void performOperationReturningNullResult() {
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, criteria);
        final ApiResponse response;

        when(service.search(eq(criteria), any(ServiceErrors.class))).thenReturn(null);

        response = searcher.performOperation();
    }

    @Test(expected = ServiceException.class)
    public void performOperationReturningServiceError() {
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, criteria);
        final ApiResponse response;

        when(service.search(eq(criteria), any(ServiceErrors.class)))
                .thenAnswer(new Answer<MockDto>() {
                    @Override
                    public MockDto answer(InvocationOnMock invocation) throws Throwable {
                        final ServiceErrors errors = (ServiceErrors) invocation.getArguments()[1];

                        errors.addError(serviceError);

                        return dto;
                    }
                });

        response = searcher.performOperation();
    }

    @Test(expected = ApiException.class)
    public void performOperationThrowingApiException() {
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, criteria);
        final ApiResponse response;

        when(service.search(eq(criteria), any(ServiceErrors.class)))
                .thenThrow(new ApiException(API_VERSION));

        response = searcher.performOperation();
    }

    @Test(expected = ApiException.class)
    public void performOperationThrowingNonApiException() {
        final SearchOneByCriteria searcher = new SearchOneByCriteria(API_VERSION, service, criteria);
        final ApiResponse response;

        when(service.search(eq(criteria), any(ServiceErrors.class)))
                .thenThrow(new IllegalStateException());

        response = searcher.performOperation();
    }
}
