package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.api.client.model.IndividualWithAdvisersDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.ResultListDto;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.LIST_CONTAINS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static com.bt.nextgen.core.api.operation.BeanFilter.Strictness.ALL;
import static com.bt.nextgen.core.api.operation.BeanFilter.Strictness.ANY;

public class BeanFilterTest {

    private static final String VERSION = "API";

    private BeanFilter filter;

    @Test
    public void shouldFilterResultMatchingEntryInAPropertyThatIsAList() {
        initFilter(ANY, new ApiSearchCriteria("adviserPositionIds", LIST_CONTAINS, "ADVISER_ID"));
        List<IndividualWithAdvisersDto> resultList = performFiltering(individual("CLIENT1", "ADVISER_ID", "1233"), individual("CLIENT2", "ADVISER_ID123"));
        assertThat(resultList, hasSize(1));
        assertThat(resultList.get(0).getDisplayName(), is("CLIENT1"));
    }

    @Test
    public void multipleCriteriaWithStrictnessAllWillFilterOnlyItemsThatMatchAllOfThem() {
        initFilter(ALL, new ApiSearchCriteria("adviserPositionIds", LIST_CONTAINS, "ADVISER_ID"), new ApiSearchCriteria("displayName", "Billy"));
        List<IndividualWithAdvisersDto> resultList = performFiltering(individual("Billy", "1233"), individual("Bob", "ADVISER_ID"), individual("Billy", "ADVISER_ID", "1233"));
        assertThat(resultList, hasSize(1));
        assertThat(resultList.get(0).getDisplayName(), is("Billy"));
        assertThat(resultList.get(0).getAdviserPositionIds(), hasSize(2));
    }

    @Test
    public void multipleCriteriaWithStrictnessAnyWillFilterItemsThatMatchAnyOfThem() {
        initFilter(new ApiSearchCriteria("adviserPositionIds", LIST_CONTAINS, "ADVISER_ID"), new ApiSearchCriteria("displayName", "Billy"));
        List<IndividualWithAdvisersDto> resultList = performFiltering(individual("Billy", "1233"), individual("Bob", "ADVISER_ID"), individual("Billy", "ADVISER_ID", "1233"));
        assertThat(resultList, hasSize(3));
        assertThat(resultList.get(0).getDisplayName(), is("Billy"));
        assertThat(resultList.get(0).getAdviserPositionIds(), hasSize(1));
        assertThat(resultList.get(1).getDisplayName(), is("Bob"));
        assertThat(resultList.get(1).getAdviserPositionIds(), hasSize(1));
        assertThat(resultList.get(2).getDisplayName(), is("Billy"));
        assertThat(resultList.get(2).getAdviserPositionIds(), hasSize(2));
    }

    private void initFilter(BeanFilter.Strictness strictness, ApiSearchCriteria... criteria) {
        filter = new BeanFilter(VERSION, null, strictness, criteria);
    }

    private void initFilter(ApiSearchCriteria... criteria) {
        filter = new BeanFilter(VERSION, null, criteria);
    }

    @SuppressWarnings("unchecked")
    private <D extends Dto> List<D> performFiltering(D... items) {
        final ApiResponse filtered = filter.performChainedOperation(resultList(items));
        return ((ResultListDto<D>) filtered.getData()).getResultList();
    }

    private static ApiResponse resultList(Dto... dtos) {
        return new ApiResponse(VERSION, new ResultListDto<>(dtos));
    }

    private static IndividualWithAdvisersDto individual(String displayName, String... adviserPositionIds) {
        final IndividualWithAdvisersDto client = new IndividualWithAdvisersDto();
        client.setAdviserPositionIds(new HashSet<>(asList(adviserPositionIds)));
        client.setDisplayName(displayName);
        client.setIdVerified(true);
        client.setInvestorType("Individual");
        return client;
    }
}