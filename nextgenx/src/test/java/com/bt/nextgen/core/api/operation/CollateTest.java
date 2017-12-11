package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.model.ApiError;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import org.junit.Test;

import java.util.List;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@code Collate} API operation.
 */
public class CollateTest {

    /** Collate operation under test. */
    private Collate collate;

    private void initOperations(ApiResponse... responses) {
        final ControllerOperation[] operations = new ControllerOperation[responses.length];
        for (int i = 0; i < responses.length; i++) {
            operations[i] = mock(ControllerOperation.class);
            when(operations[i].performOperation()).thenReturn(responses[i]);
        }
        collate = new Collate(operations);
    }

    private ApiResponse[] initOperations(Object... dataOrErrors) {
        final ApiResponse[] responses = new ApiResponse[dataOrErrors.length];
        for (int i = 0; i < dataOrErrors.length; i++) {
            if (dataOrErrors[i] instanceof Dto) {
                responses[i] = new ApiResponse(CURRENT_VERSION, (Dto) dataOrErrors[i]);
            } else {
                responses[i] = new ApiResponse(CURRENT_VERSION, (ApiError) dataOrErrors[i]);
            }
        }
        initOperations(responses);
        return responses;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void performOperationWithSuccesses() {
        final Dto[] successes = { mock(Dto.class), mock(Dto.class), mock(Dto.class), mock(Dto.class), mock(Dto.class) };
        final ResultListDto<Dto> resultList = new ResultListDto<>(asList(successes[1], successes[2]));
        initOperations(successes[0], resultList, successes[3], successes[4]);

        final ApiResponse collated = collate.performOperation();
        assertThat(collated.getApiVersion(), is(CURRENT_VERSION));
        assertThat(collated.getStatus(), is(1));
        assertThat(collated.getPaging(), is(nullValue()));
        assertThat(collated.getError(), is(nullValue()));
        assertTrue(collated.getData() instanceof ResultListDto);
        List<Dto> results = ((ResultListDto) collated.getData()).getResultList();
        assertThat(results.size(), is(successes.length));
        assertThat(results, contains(successes));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void performOperationWithErrors() {
        final DomainApiErrorDto[] domainErrors = {
                new DomainApiErrorDto("NEXTGEN", "NPE", "NullPointerException occurred"),
                new DomainApiErrorDto("NEXTGEN", "CWU", "Cache warm-up routine incomplete"),
                new DomainApiErrorDto("NEXTGEN", "GSE", "Source code contains grammer and speling errurs"),
                new DomainApiErrorDto("NEXTGEN", "SSV", "Satellite modules are -SNAPSHOT versions"),
                new DomainApiErrorDto("NEXTGEN", "NED", "Code documentation is non-existent")
        };
        final ApiError[] errors = {
                new ApiError(7006, "First error", "Ref#7006", asList(domainErrors[0], domainErrors[1])),
                new ApiError(7007, "Second error"),
                new ApiError(7008, "Third error", "Ref#7008", asList(domainErrors[2], domainErrors[3], domainErrors[4]))
        };
        initOperations(errors);

        final ApiResponse collated = collate.performOperation();
        assertThat(collated.getApiVersion(), is(CURRENT_VERSION));
        assertThat(collated.getStatus(), is(0));
        assertThat(collated.getPaging(), is(nullValue()));
        assertTrue(collated.getData() instanceof ResultListDto);
        final List<Dto> results = ((ResultListDto) collated.getData()).getResultList();
        assertThat(results.size(), is(0));
        final ApiError error = collated.getError();
        assertThat(error.getCode(), is(7006));
        assertThat(error.getMessage(), is("First error ; Second error ; Third error"));
        assertThat(error.getReference(), is("Ref#7006 ; Ref#7008"));
        assertThat(error.getErrors(), contains(domainErrors));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void performOperationWithSuccessesAndErrors() {
        final DomainApiErrorDto[] domainErrors = {
                new DomainApiErrorDto("NEXTGEN", "NPE", "NullPointerException occurred"),
                new DomainApiErrorDto("NEXTGEN", "CWU", "Cache warm-up routine incomplete"),
        };
        final ApiError[] errors = {
                new ApiError(7006, "First error", "Ref#7006", asList(domainErrors[0], domainErrors[1])),
                new ApiError(7007, "Second error")
        };
        final Dto[] successes = { mock(Dto.class), mock(Dto.class), mock(Dto.class) };
        initOperations(successes[0], errors[0], successes[1], errors[1], successes[2]);

        final ApiResponse collated = collate.performOperation();
        assertThat(collated.getApiVersion(), is(CURRENT_VERSION));
        assertThat(collated.getStatus(), is(0));
        assertThat(collated.getPaging(), is(nullValue()));
        assertTrue(collated.getData() instanceof ResultListDto);
        final List<Dto> results = ((ResultListDto) collated.getData()).getResultList();
        assertThat(results, contains(successes));
        final ApiError error = collated.getError();
        assertThat(error.getCode(), is(7006));
        assertThat(error.getMessage(), is("First error ; Second error"));
        assertThat(error.getReference(), is("Ref#7006"));
        assertThat(error.getErrors(), contains(domainErrors));
    }
}
