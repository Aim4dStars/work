package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ModifyOrganisationCustomerRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.bt.nextgen.service.group.customer.groupesb.state.GroupEsbRegStateRequestBuilder;
import com.bt.nextgen.service.integration.user.CISKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbRegStateRequestBuilderTest {

    @Mock
    private RetrieveDetailsAndArrangementRelationshipsForIPsResponse response;
    @Mock
    private Organisation organisation;

    private CustomerData customerData;
    private CustomerManagementRequest req;
    private MaintenanceAuditContext auditContext;

    @Before
    public void setup() {

        customerData = new CustomerDataImpl();
        CustomerRegisteredState regState = new CustomerRegisteredState();
        regState.setCountry("AU");
        regState.setRegistrationState("NSW");
        customerData.setRegisteredState(regState);

        req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("123456"));
        customerData.setRequest(req);

        auditContext = new MaintenanceAuditContext();
        auditContext.setIsActive(Boolean.TRUE);
        auditContext.setVersionNumber("8");
    }

    @Test
    public void createStateModificationRequestShouldReturnMinimumValuesWithModifyOrgRequest() {

        when(organisation.getAuditContext()).thenReturn(auditContext);
        response = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        response.setOrganisation(organisation);

        MaintenanceAuditContext auditConext = new MaintenanceAuditContext();
        auditConext.setIsActive(Boolean.TRUE);
        auditConext.setVersionNumber("8");
        when(organisation.getAuditContext()).thenReturn(auditConext);

        ModifyOrganisationCustomerRequest modificationRequest = GroupEsbRegStateRequestBuilder.createStateModificationRequest(customerData, response);

        assertThat(modificationRequest.getOrganisation().getAuditContext().getVersionNumber(),is("8"));
        assertThat(modificationRequest.getOrganisation().getHasRegistration().getIsIssuedAt().getCountry(),is("AU"));
        assertThat(modificationRequest.getOrganisation().getHasRegistration().getIsIssuedAt().getState(),is("NSW"));

        assertThat(modificationRequest.getOrganisation().getInvolvedPartyIdentifier().getInvolvedPartyId() ,is("123456"));
        assertThat(modificationRequest.getOrganisation().getInvolvedPartyIdentifier().getIdentificationScheme().value() ,is("CISKey"));
        assertThat(modificationRequest.getOrganisation().getInvolvedPartyIdentifier().getSourceSystem() ,is("UCM"));
    }

    @Test
    public void createStateModificationRequestShouldNotFailToNullValues() {

        CustomerData customerData = new CustomerDataImpl();
        CustomerRegisteredState regState = new CustomerRegisteredState();
        regState.setCountry("AU");
        regState.setRegistrationState("NSW");
        customerData.setRegisteredState(regState);
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("123456"));
        customerData.setRequest(req);

        response = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        response.setOrganisation(organisation);

        ModifyOrganisationCustomerRequest modificationRequest = GroupEsbRegStateRequestBuilder.createStateModificationRequest(customerData, response);

        assertThat(modificationRequest.getOrganisation().getAuditContext().getVersionNumber(),is(nullValue()));
        assertThat(modificationRequest.getOrganisation().getHasRegistration().getIsIssuedAt().getCountry(),is("AU"));
        assertThat(modificationRequest.getOrganisation().getHasRegistration().getIsIssuedAt().getState(),is("NSW"));

        assertThat(modificationRequest.getOrganisation().getInvolvedPartyIdentifier().getInvolvedPartyId() ,is("123456"));
        assertThat(modificationRequest.getOrganisation().getInvolvedPartyIdentifier().getIdentificationScheme().value() ,is("CISKey"));
        assertThat(modificationRequest.getOrganisation().getInvolvedPartyIdentifier().getSourceSystem() ,is("UCM"));
    }

}
