package com.bt.nextgen.api.beneficiary.controller;

import com.bt.nextgen.api.beneficiary.model.RelationshipTypeDto;
import com.bt.nextgen.api.beneficiary.service.RelationshipTypeDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test case for RelationshipApiController
 * Created by M035995 on 7/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class RelationshipApiControllerTest {

    @InjectMocks
    RelationshipApiController relationshipApiController;

    @Mock
    RelationshipTypeDtoService relationshipTypeDtoService;

    private List<RelationshipTypeDto> relationshipTypeDtoList = null;

    @Before
    public void init() {
        relationshipTypeDtoList = new ArrayList<>();
        RelationshipTypeDto relationshipTypeDto = new RelationshipTypeDto("2", "CHILD", "child", "person_rel_type", "Child", true, 2);
        relationshipTypeDtoList.add(relationshipTypeDto);
        relationshipTypeDto = new RelationshipTypeDto("3", "FIN_DEP", "fin_dep",
                "person_rel_type", "Financial Dependent", true, 3);
        relationshipTypeDtoList.add(relationshipTypeDto);
        relationshipTypeDto = new RelationshipTypeDto("4", "INTERDEPENDENT", "interdependent", "person_rel_type",
                "Interdependent", true, 4);
        relationshipTypeDtoList.add(relationshipTypeDto);
        relationshipTypeDto = new RelationshipTypeDto("5", "LPR", "lpr", "person_rel_type",
                "Legal Personal Representative", false, 5);
        relationshipTypeDtoList.add(relationshipTypeDto);
        relationshipTypeDto = new RelationshipTypeDto("1", "SPOUSE", "spouse", "person_rel_type",
                "Spouse", true, 1);
        relationshipTypeDtoList.add(relationshipTypeDto);
        when(relationshipTypeDtoService.findAll(any(ServiceErrors.class))).
                thenReturn(relationshipTypeDtoList);
    }

    @Test
    public void getRelationshipTypes() {
        ApiResponse response = relationshipApiController.getRelationshipTypes();
        verify(relationshipTypeDtoService, times(1)).findAll(any(ServiceErrors.class));

        assertThat(response, is(notNullValue()));

        ResultListDto<RelationshipTypeDto> resultListDto = (ResultListDto<RelationshipTypeDto>) response.getData();
        assertThat("List size", resultListDto.getResultList().size(), is(5));

        RelationshipTypeDto relationshipTypeDto = resultListDto.getResultList().get(0);
        assertThat("relationship type 0 - id", relationshipTypeDto.getId(), equalTo("2"));
        assertThat("relationship type 0 - name value", relationshipTypeDto.getValue(), equalTo("CHILD"));
        assertThat("relationship type 0 - name label", relationshipTypeDto.getLabel(), equalTo("Child"));
        assertThat("relationship type 0 - IntlId", relationshipTypeDto.getIntlId(), equalTo("child"));
        assertThat("relationship type 0 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 0 - dependent", relationshipTypeDto.isDependent(), equalTo(true));

        relationshipTypeDto = resultListDto.getResultList().get(1);
        assertThat("relationship type 1 - id", relationshipTypeDto.getId(), equalTo("3"));
        assertThat("relationship type 1 - name value", relationshipTypeDto.getValue(), equalTo("FIN_DEP"));
        assertThat("relationship type 1 - name label", relationshipTypeDto.getLabel(), equalTo("Financial Dependent"));
        assertThat("relationship type 1 - IntlId", relationshipTypeDto.getIntlId(), equalTo("fin_dep"));
        assertThat("relationship type 1 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 1 - dependent", relationshipTypeDto.isDependent(), equalTo(true));

        relationshipTypeDto = resultListDto.getResultList().get(2);
        assertThat("relationship type 2 - id", relationshipTypeDto.getId(), equalTo("4"));
        assertThat("relationship type 2 - name value", relationshipTypeDto.getValue(), equalTo("INTERDEPENDENT"));
        assertThat("relationship type 2 - name label", relationshipTypeDto.getLabel(), equalTo("Interdependent"));
        assertThat("relationship type 2 - IntlId", relationshipTypeDto.getIntlId(), equalTo("interdependent"));
        assertThat("relationship type 2 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 2 - dependent", relationshipTypeDto.isDependent(), equalTo(true));

        relationshipTypeDto = resultListDto.getResultList().get(3);
        assertThat("relationship type 3 - id", relationshipTypeDto.getId(), equalTo("5"));
        assertThat("relationship type 3 - name value", relationshipTypeDto.getValue(), equalTo("LPR"));
        assertThat("relationship type 3 - name label", relationshipTypeDto.getLabel(), equalTo("Legal Personal Representative"));
        assertThat("relationship type 3 - IntlId", relationshipTypeDto.getIntlId(), equalTo("lpr"));
        assertThat("relationship type 3 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 3 - dependent", relationshipTypeDto.isDependent(), equalTo(false));

        relationshipTypeDto = resultListDto.getResultList().get(4);
        assertThat("relationship type 4 - id", relationshipTypeDto.getId(), equalTo("1"));
        assertThat("relationship type 4 - name value", relationshipTypeDto.getValue(), equalTo("SPOUSE"));
        assertThat("relationship type 4 - name label", relationshipTypeDto.getLabel(), equalTo("Spouse"));
        assertThat("relationship type 4 - IntlId", relationshipTypeDto.getIntlId(), equalTo("spouse"));
        assertThat("relationship type 4 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 4 - dependent", relationshipTypeDto.isDependent(), equalTo(true));
    }

    @Test
    public void testForNullRelationshipTypeList() {
        when(relationshipTypeDtoService.findAll(any(ServiceErrors.class))).
                thenReturn(null);
        ApiResponse response = relationshipApiController.getRelationshipTypes();
        verify(relationshipTypeDtoService, times(1)).findAll(any(ServiceErrors.class));
        ResultListDto<RelationshipTypeDto> resultListDto = (ResultListDto<RelationshipTypeDto>) response.getData();
        //Verify that the result list is null
        assertThat("List size", resultListDto.getResultList(), is(nullValue()));
    }

}
