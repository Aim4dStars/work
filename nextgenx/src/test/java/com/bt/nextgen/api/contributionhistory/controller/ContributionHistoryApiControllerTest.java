package com.bt.nextgen.api.contributionhistory.controller;

import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.api.contributionhistory.service.ContributionHistoryDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteriaListMatcher;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by M022641 on 14/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContributionHistoryApiControllerTest {
    private static final String VALID_DATE_STR = "2016-07-01";
    private static final String VALID_ACCOUNT_ID = "43847";
    private static final String VALID_ENCODED_ACCOUNT_ID = "1F73FBB840D273921114472060626B88AA4C90C698817AB9";
    @Mock
    private ContributionHistoryDtoService dtoService;

    @InjectMocks
    private ContributionHistoryApiController controller;

    @Test(expected = IllegalArgumentException.class)
    public void getContributionSummaryWithEmptyAccountId() {
        controller.getContributionSummary("", VALID_DATE_STR, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionSummaryWithEmptyDateStr() {
        controller.getContributionSummary(VALID_ENCODED_ACCOUNT_ID, "", "false");
    }

    @Test
    public void getContributionSummary() {
        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", EQUALS, VALID_ACCOUNT_ID, STRING);
        final ApiSearchCriteria financialYearDateCriteria = new ApiSearchCriteria("financialYearDate", EQUALS,
                VALID_DATE_STR, STRING);
        final ApiResponse response;
        final ContributionHistoryDto dto = new ContributionHistoryDto();

        when(dtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(dto);
        response = controller.getContributionSummary(VALID_ENCODED_ACCOUNT_ID, VALID_DATE_STR, "false");

        verify(dtoService)
                .search(argThat(new ApiSearchCriteriaListMatcher(accountIdCriteria, financialYearDateCriteria)),
                        any(ServiceErrors.class));

        assertThat("response - Dto", (ContributionHistoryDto) response.getData(), equalTo(dto));
    }
}
