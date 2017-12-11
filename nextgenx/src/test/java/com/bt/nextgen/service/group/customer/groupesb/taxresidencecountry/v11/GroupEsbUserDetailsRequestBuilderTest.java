package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.integration.user.CISKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GroupEsbUserDetailsRequestBuilderTest {

    @Test
    public void createRetrieveDetailsAndArrangementRelationshipsTest() {
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
        request.setCISKey(CISKey.valueOf("87458125478"));
        List<CustomerManagementOperation> operations = new ArrayList<>();
        operations.add(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        request.setOperationTypes(operations);

        RetrieveDetailsAndArrangementRelationshipsForIPsRequest iPsRequest = GroupEsbUserDetailsRequestBuilder.createRetrieveDetailsAndArrangementRelationships(request);
        Assert.assertNotNull(iPsRequest);
        Assert.assertEquals(RoleType.ORGANISATION.getDescription(), iPsRequest.getOrganisationInvolvedPartyRole().getRoleType());
        Assert.assertEquals("87458125478",iPsRequest.getInvolvedPartyIdentifier().get(0).getInvolvedPartyId());
    }
}
