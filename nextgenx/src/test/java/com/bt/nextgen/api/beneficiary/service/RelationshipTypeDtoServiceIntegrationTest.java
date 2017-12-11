package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.beneficiary.model.RelationshipTypeDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

/**
 * Integration test class for  {@link RelationshipTypeDtoService}
 * Created by M035995 on 6/07/2016.
 */
public class RelationshipTypeDtoServiceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    RelationshipTypeDtoService relationshipTypeDtoService;

    @Test
    public void testAllRelationshipTypes() {
        List<RelationshipTypeDto> codeList = relationshipTypeDtoService.findAll(new ServiceErrorsImpl());

        assertThat("Total Relationship types", codeList.size(), equalTo(5));

        RelationshipTypeDto relationshipTypeDto = codeList.get(0);
        assertThat("relationship type 0 - id", relationshipTypeDto.getId(), equalTo("1"));
        assertThat("relationship type 0 - name value", relationshipTypeDto.getValue(), equalTo("SPOUSE"));
        assertThat("relationship type 0 - name label", relationshipTypeDto.getLabel(), equalTo("Spouse"));
        assertThat("relationship type 0 - IntlId", relationshipTypeDto.getIntlId(), equalTo("spouse"));
        assertThat("relationship type 0 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 0 - dependent", relationshipTypeDto.isDependent(), equalTo(true));
        assertThat("relationship type 0 - orderId", relationshipTypeDto.getOrderId(), equalTo(1));

        relationshipTypeDto = codeList.get(1);
        assertThat("relationship type 1 - id", relationshipTypeDto.getId(), equalTo("2"));
        assertThat("relationship type 1 - name value", relationshipTypeDto.getValue(), equalTo("CHILD"));
        assertThat("relationship type 1 - name label", relationshipTypeDto.getLabel(), equalTo("Child"));
        assertThat("relationship type 1 - IntlId", relationshipTypeDto.getIntlId(), equalTo("child"));
        assertThat("relationship type 1 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 1 - dependent", relationshipTypeDto.isDependent(), equalTo(true));
        assertThat("relationship type 1 - orderId", relationshipTypeDto.getOrderId(), equalTo(2));

        relationshipTypeDto = codeList.get(2);
        assertThat("relationship type 2 - id", relationshipTypeDto.getId(), equalTo("3"));
        assertThat("relationship type 2 - name value", relationshipTypeDto.getValue(), equalTo("FIN_DEP"));
        assertThat("relationship type 2 - name label", relationshipTypeDto.getLabel(), equalTo("Financial dependant"));
        assertThat("relationship type 2 - IntlId", relationshipTypeDto.getIntlId(), equalTo("fin_dep"));
        assertThat("relationship type 2 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 2 - dependent", relationshipTypeDto.isDependent(), equalTo(true));
        assertThat("relationship type 2 - orderId", relationshipTypeDto.getOrderId(), equalTo(3));

        relationshipTypeDto = codeList.get(3);
        assertThat("relationship type 3 - id", relationshipTypeDto.getId(), equalTo("4"));
        assertThat("relationship type 3 - name value", relationshipTypeDto.getValue(), equalTo("INTERDEPENDENT"));
        assertThat("relationship type 3 - name label", relationshipTypeDto.getLabel(), equalTo("Interdependant"));
        assertThat("relationship type 3 - IntlId", relationshipTypeDto.getIntlId(), equalTo("interdependent"));
        assertThat("relationship type 3 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 3 - dependent", relationshipTypeDto.isDependent(), equalTo(true));
        assertThat("relationship type 3 - orderId", relationshipTypeDto.getOrderId(), equalTo(4));

        relationshipTypeDto = codeList.get(4);
        assertThat("relationship type 4 - id", relationshipTypeDto.getId(), equalTo("5"));
        assertThat("relationship type 4 - name value", relationshipTypeDto.getValue(), equalTo("LPR"));
        assertThat("relationship type 4 - name label", relationshipTypeDto.getLabel(), equalTo("Legal personal representative"));
        assertThat("relationship type 4 - IntlId", relationshipTypeDto.getIntlId(), equalTo("lpr"));
        assertThat("relationship type 4 - listName", relationshipTypeDto.getListName(), equalTo("person_rel_type"));
        assertThat("relationship type 4 - dependent", relationshipTypeDto.isDependent(), equalTo(false));
        assertThat("relationship type 4 - orderId", relationshipTypeDto.getOrderId(), equalTo(5));

    }
}
