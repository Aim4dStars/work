package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.ModifyIndividualCustomerRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.*;
import com.bt.nextgen.service.integration.user.CISKey;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by L070815 on 7/08/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbPreferredNameUpdateReqTest {

    @Mock
    RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse;
    @Mock
    Individual individual;
    @Mock
    MaintenanceAuditContext auditContext;
    @Mock
    IndividualName hasforName;
    @Mock
    AlternateName cachedAlternateName;

    CustomerData requestParam= new CustomerDataImpl();
    List<AlternateName> names = new ArrayList<>();

    @org.junit.Before
    public void setUp() throws Exception {

        when(cachedResponse.getIndividual()).thenReturn(individual);
        when(cachedResponse.getIndividual().getAuditContext()).thenReturn(auditContext);
        when(cachedResponse.getIndividual().getAuditContext()).thenReturn(auditContext);
        when(cachedResponse.getIndividual().getAuditContext().getVersionNumber()).thenReturn("1.0");
        when(cachedResponse.getIndividual().getHasForName()).thenReturn(hasforName);
        when(cachedAlternateName.getAuditContext()).thenReturn(auditContext);
        when(cachedAlternateName.getIsPreferred()).thenReturn("Y");
        when(cachedAlternateName.getAuditContext().isIsActive()).thenReturn(true);
        when(cachedAlternateName.getAuditContext().getVersionNumber()).thenReturn("1.0");
        when(cachedAlternateName.getName()).thenReturn("gcm test");
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("66786810081"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        List<CustomerManagementOperation> list = new ArrayList();
        list.add(CustomerManagementOperation.PREFERRED_NAME_UPDATE);
        req.setOperationTypes(list);


        when(cachedResponse.getIndividual().getHasForName().getHasAlternateName()).thenReturn(names);
        requestParam.setRequest(req);
    }

    @Test
    public void testCreateRetrieveDetailsAndArrangementRelationshipsForIPsRequest_ADD1() {
        names.add(cachedAlternateName);
        when(cachedAlternateName.getIsPreferred()).thenReturn("N");
        requestParam.setPreferredName("test");
        ModifyIndividualCustomerRequest request = GroupEsbPreferredNameUpdateRequestBuilder.createModifyIndividualCustomerRequest(requestParam,cachedResponse);
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getName(), Is.is("test"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getStartDate());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getIsPreferred(), Is.is("Y"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getRequestedAction().toString(), Is.is("ADD"));
        assertThat(request.getIndividual().getNoAlternateName().toString(),Is.is("N"));

    }

    @Test
    public void testCreateRetrieveDetailsAndArrangementRelationshipsForIPsRequest_ADD2() {
        requestParam.setPreferredName("test");
        ModifyIndividualCustomerRequest request = GroupEsbPreferredNameUpdateRequestBuilder.createModifyIndividualCustomerRequest(requestParam,cachedResponse);
        assertThat(request.getIndividual().getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getName(), Is.is("test"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getStartDate());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getIsPreferred(), Is.is("Y"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getRequestedAction().toString(), Is.is("ADD"));
        assertThat(request.getIndividual().getNoAlternateName().toString(),Is.is("N"));

    }
    @Test
    public void testCreateRetrieveDetailsAndArrangementRelationshipsForIPsRequest_MODIFY() {
        names.add(cachedAlternateName);
        requestParam.setPreferredName("test");
        ModifyIndividualCustomerRequest request = GroupEsbPreferredNameUpdateRequestBuilder.createModifyIndividualCustomerRequest(requestParam,cachedResponse);
        assertThat(request.getIndividual().getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getAuditContext().getLastUpdateTimestamp());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getName(), Is.is("gcm test"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getIsPreferred(), Is.is("N"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getRequestedAction().toString(), Is.is("MODIFY"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getEndDate());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(1).getName(), Is.is("test"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(1).getIsPreferred(), Is.is("Y"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(1).getRequestedAction().toString(), Is.is("ADD"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(1).getStartDate());

    }

    @Test
    public void testCreateRetrieveDetailsAndArrangementRelationshipsForIPsRequest_DELETE_noAltName() {

        names.add(cachedAlternateName);
        requestParam.setPreferredName("");
        ModifyIndividualCustomerRequest request = GroupEsbPreferredNameUpdateRequestBuilder.createModifyIndividualCustomerRequest(requestParam,cachedResponse);
        assertThat(request.getIndividual().getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getEndDate());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getAuditContext().getLastUpdateTimestamp());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getName(), Is.is("gcm test"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getRequestedAction().toString(), Is.is("DELETE"));
        assertThat(request.getIndividual().getNoAlternateName().toString(), Is.is("Y"));

    }
    @Test
    public void testCreateRetrieveDetailsAndArrangementRelationshipsForIPsRequest_DELETE() {
        names.add(cachedAlternateName);
        cachedAlternateName.setName("test");
        names.add(cachedAlternateName);
        requestParam.setPreferredName("");
        ModifyIndividualCustomerRequest request = GroupEsbPreferredNameUpdateRequestBuilder.createModifyIndividualCustomerRequest(requestParam,cachedResponse);
        assertThat(request.getIndividual().getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertThat(request.getIndividual().getNoAlternateName().toString(),Is.is("N"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getEndDate());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getAuditContext().getVersionNumber(), Is.is("1.0"));
        assertNotNull(request.getIndividual().getHasForName().getHasAlternateName().get(0).getAuditContext().getLastUpdateTimestamp());
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getName(), Is.is("gcm test"));
        assertThat(request.getIndividual().getHasForName().getHasAlternateName().get(0).getRequestedAction().toString(), Is.is("DELETE"));

    }
}
