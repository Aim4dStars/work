package com.bt.nextgen.service.avaloq.beneficiary;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by M035995 on 12/07/2016.
 */
public class RelationshipTypeTest {

    @Test
    public void testRelationshipTypeValues() {
        //Validate total values in enum
        assertThat(RelationshipType.values().length, is(5));

        // Validate the enum attribute values for all relationship types
        RelationshipType relationshipType = RelationshipType.SPOUSE;
        assertThat(relationshipType.toString(), is("spouse"));
        assertThat(relationshipType.getAvaloqInternalId(), is("spouse"));
        assertThat(relationshipType.getName(), is("Spouse"));
        assertThat(relationshipType.getOrderId(), is(1));

        relationshipType = RelationshipType.FINANCIAL_DEPENDENT;
        assertThat(relationshipType.toString(), is("fin_dep"));
        assertThat(relationshipType.getAvaloqInternalId(), is("fin_dep"));
        assertThat(relationshipType.getName(), is("Financial Dependent"));
        assertThat(relationshipType.getOrderId(), is(3));

        relationshipType = RelationshipType.INTERDEPENDENT;
        assertThat(relationshipType.toString(), is("interdependent"));
        assertThat(relationshipType.getAvaloqInternalId(), is("interdependent"));
        assertThat(relationshipType.getName(), is("Interdependent"));
        assertThat(relationshipType.getOrderId(), is(4));

        relationshipType = RelationshipType.LPR;
        assertThat(relationshipType.toString(), is("lpr"));
        assertThat(relationshipType.getAvaloqInternalId(), is("lpr"));
        assertThat(relationshipType.getName(), is("Legal Personal Representative"));
        assertThat(relationshipType.getOrderId(), is(5));

        relationshipType = RelationshipType.CHILD;
        assertThat(relationshipType.toString(), is("child"));
        assertThat(relationshipType.getAvaloqInternalId(), is("child"));
        assertThat(relationshipType.getName(), is("Child"));
        assertThat(relationshipType.getOrderId(), is(2));
    }

    @Test
    public void testFindByAvaloqId() {
        RelationshipType relationshipType = RelationshipType.findByAvaloqId("interdependent");
        assertThat(relationshipType.toString(), is("interdependent"));
        assertThat(relationshipType.getAvaloqInternalId(), is("interdependent"));
        assertThat(relationshipType.getName(), is("Interdependent"));
        assertThat(relationshipType.getOrderId(), is(4));
    }

}
