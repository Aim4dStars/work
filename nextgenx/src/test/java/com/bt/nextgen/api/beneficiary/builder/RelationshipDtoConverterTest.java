package com.bt.nextgen.api.beneficiary.builder;

import com.bt.nextgen.api.beneficiary.model.RelationshipTypeDto;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class RelationshipDtoConverterTest {

    Collection<Code> typeCodeList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        Code code1 = new CodeImpl("21", "none", "None", "none");
        ((CodeImpl) code1).addField("btfg$ui_dep", "+");

        Code code2 = new CodeImpl("2", "CHILD", "Child", "child");
        ((CodeImpl) code2).addField("btfg$ui_dep", "");

        typeCodeList.add(code1);
        typeCodeList.add(code2);
    }
    @Test
    public void testGetRelationshipList()
    {
        final RelationshipDtoConverter dtoConverter = new RelationshipDtoConverter();
        final List<RelationshipTypeDto> relationshipList = dtoConverter.getRelationshipList(typeCodeList);
        assertEquals(relationshipList.size(),1);

        RelationshipTypeDto relationshipDto1 = relationshipList.get(0);
        assertThat("nomination type 0 - id", relationshipDto1.getId(), equalTo("2"));
        assertThat("nomination type 0 - label", relationshipDto1.getLabel() , equalTo("Child"));
        assertThat("nomination type 0 - intlId", relationshipDto1.getIntlId(), equalTo("child"));
        assertThat("nomination type 0 - listName", relationshipDto1.getListName(), equalTo("person_rel_type"));
        assertThat("nomination type 0 - value", relationshipDto1.getValue(), equalTo("CHILD"));

    }

}
