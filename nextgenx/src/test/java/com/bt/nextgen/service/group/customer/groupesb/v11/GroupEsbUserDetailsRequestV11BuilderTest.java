package com.bt.nextgen.service.group.customer.groupesb.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyEntityFilter;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsRequest;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationContext;
import au.com.westpac.gn.utility.xsd.pagination.v1.PaginationInstruction;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.integration.user.CISKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by F058391 on 19/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbUserDetailsRequestV11BuilderTest {

    @Test
    public void createRetrieveDetailsAndArrangementRelationships_demographicDetails() {
        ArrayList<CustomerManagementOperation> operationTypes = new ArrayList<>();
        operationTypes.add(CustomerManagementOperation.ARRANGEMENTS);
        operationTypes.add(CustomerManagementOperation.PREFERRED_NAME_UPDATE);
        operationTypes.add(CustomerManagementOperation.INDIVIDUAL_DETAILS);
        CustomerManagementRequest customerManagementRequest = new CustomerManagementRequestImpl(CISKey.valueOf("1234567"), RoleType.INDIVIDUAL, operationTypes);

        RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = GroupEsbUserDetailsRequestV11Builder.createRetrieveDetailsAndArrangementRelationships(customerManagementRequest);
        InvolvedPartyEntityFilter partyEntityFilter = request.getInvolvedPartyEntityFilter();

        assertNotNull(request.getIndividualInvolvedPartyRole());
        assertNull(request.getOrganisationInvolvedPartyRole());
        assertThat(partyEntityFilter.isIncludeDemographics(), is(true));
        assertThat(partyEntityFilter.isIncludeAlternateNames(), is(true));
        assertThat(partyEntityFilter.isAlternateNamesActiveFlag(), is(true));
        assertThat(partyEntityFilter.isIncludeArrangements(), is(true));
    }

    @Test
    public void createRetrieveDetailsAndArrangementRelationships_contactDetails() {
        ArrayList<CustomerManagementOperation> operationTypes = new ArrayList<>();
        operationTypes.add(CustomerManagementOperation.ADDRESS_UPDATE);
        operationTypes.add(CustomerManagementOperation.CONTACT_DETAILS_UPDATE);
        CustomerManagementRequest customerManagementRequest = new CustomerManagementRequestImpl(CISKey.valueOf("1234567"), RoleType.INDIVIDUAL, operationTypes);

        RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = GroupEsbUserDetailsRequestV11Builder.createRetrieveDetailsAndArrangementRelationships(customerManagementRequest);
        InvolvedPartyEntityFilter partyEntityFilter = request.getInvolvedPartyEntityFilter();

        assertNotNull(request.getIndividualInvolvedPartyRole());
        assertNull(request.getOrganisationInvolvedPartyRole());
        assertThat(partyEntityFilter.isIncludePostalAddresses(), is(true));
        assertThat(partyEntityFilter.isIPPostalAddressActiveFlag(), is(true));
        assertThat(partyEntityFilter.isIncludeCommunicationAddresses(), is(true));
        assertThat(partyEntityFilter.isIPCommAddressActiveFlag(), is(true));
    }

    @Test
    public void createRetrieveDetailsAndArrangementRelationships_crsDetails() {
        ArrayList<CustomerManagementOperation> operationTypes = new ArrayList<>();;
        operationTypes.add(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        CustomerManagementRequest customerManagementRequest = new CustomerManagementRequestImpl(CISKey.valueOf("1234567"), RoleType.INDIVIDUAL, operationTypes);

        RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = GroupEsbUserDetailsRequestV11Builder.createRetrieveDetailsAndArrangementRelationships(customerManagementRequest);
        InvolvedPartyEntityFilter partyEntityFilter = request.getInvolvedPartyEntityFilter();

        assertThat(partyEntityFilter.isIncludeTaxResidency(), is(true));
        assertThat(partyEntityFilter.getTaxResidencyFilter().isTaxResidencyActiveFlag(), is(true));
    }


    @Test
    public void createPaginatedRetrieveDetailsAndArrangementRelationships(){
        ArrayList<CustomerManagementOperation> operationTypes = new ArrayList<>();
        operationTypes.add(CustomerManagementOperation.ARRANGEMENTS);
        CustomerManagementRequest customerManagementRequest = new CustomerManagementRequestImpl(CISKey.valueOf("1234567"), RoleType.INDIVIDUAL, operationTypes);

        PaginationInstruction paginationInstruction = new PaginationInstruction();
        PaginationContext paginationContext = new PaginationContext();
        paginationInstruction.getPaginationContext().add(paginationContext);
        paginationContext.setName("Pagination");
        paginationContext.setValue("50");

        RetrieveDetailsAndArrangementRelationshipsForIPsRequest request = GroupEsbUserDetailsRequestV11Builder.createPaginatedRetrieveDetailsAndArrangementRelationships(customerManagementRequest, paginationInstruction);

        PaginationInstruction arrangementPaginationInstruction = request.getArrangementPaginationInstruction();
        assertNotNull(arrangementPaginationInstruction);
        assertThat(arrangementPaginationInstruction.getPaginationContext().get(0).getName(), is("Pagination"));
        assertThat(arrangementPaginationInstruction.getPaginationContext().get(0).getValue(), is("50"));
    }
}