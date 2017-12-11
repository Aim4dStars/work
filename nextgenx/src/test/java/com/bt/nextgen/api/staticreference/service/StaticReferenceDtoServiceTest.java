package com.bt.nextgen.api.staticreference.service;

import ch.lambdaj.group.Group;
import com.bt.nextgen.api.staticreference.model.StaticReferenceDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.group;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class StaticReferenceDtoServiceTest {
    @Test
    public void testRetrievePolicyStatus() {
        ApiSearchCriteria criteria = new ApiSearchCriteria("category", ApiSearchCriteria.SearchOperation.EQUALS, "policyStatus", ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(criteria);

        StaticReferenceDtoService staticReferenceDtoService = new StaticReferenceDtoServiceImpl();
        List<StaticReferenceDto> referenceList = staticReferenceDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(referenceList);
        assertThat(referenceList.size(), Is.is(6));
    }

    @Test
    public void testRetrieveAll() {
        ApiSearchCriteria criteria = new ApiSearchCriteria("category", ApiSearchCriteria.SearchOperation.EQUALS, "all", ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(criteria);

        StaticReferenceDtoService staticReferenceDtoService = new StaticReferenceDtoServiceImpl();
        List<StaticReferenceDto> referenceList = staticReferenceDtoService.search(criteriaList, new ServiceErrorsImpl());
        Group<StaticReferenceDto> result = group(referenceList, "category");

        assertNotNull(result);
        assertThat(result.keySet().size(), Is.is(8));
    }
}