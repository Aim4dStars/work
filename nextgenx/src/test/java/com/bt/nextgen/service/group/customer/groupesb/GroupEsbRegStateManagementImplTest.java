package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationcustomer.v2.svc0339.ModifyOrganisationCustomerRequest;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.bt.nextgen.service.group.customer.groupesb.state.GroupEsbRegStateManagementImpl;
import com.bt.nextgen.service.integration.user.CISKey;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GroupEsbRegStateManagementImplTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("regStateManagementService")
    GroupEsbRegStateManagementImpl groupEsbRegStateManagementImpl;

    CustomerManagementRequest customerManagementRequest;

    ModifyOrganisationCustomerRequest modifyOrganisationCustomerRequest;

    CustomerData customerRegData;

    @Before
    public void setUp() throws Exception
    {
        customerManagementRequest = new CustomerManagementRequestImpl();
        customerManagementRequest.setCISKey(CISKey.valueOf("66786810081"));
        customerManagementRequest.setInvolvedPartyRoleType(RoleType.ORGANISATION);
        List<CustomerManagementOperation> operations = new ArrayList<CustomerManagementOperation>();
        operations.add(CustomerManagementOperation.REGISTRATION_STATE);
        customerManagementRequest.setOperationTypes(operations);

        CustomerRegisteredState registeredState = new CustomerRegisteredState();
        registeredState.setCountry("AU");
        registeredState.setRegistrationState("VIC");
        registeredState.setRegistrationNumber("53004085616");
        registeredState.setRegistrationType("ABN");

        customerRegData = new CustomerDataImpl();
        customerRegData.setRequest(customerManagementRequest);
        customerRegData.setRegisteredState(registeredState);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testGetCustomerRegStateRetrievalSuccess()
    {
        ServiceErrors errors = null;
        CustomerData customerDataResponse = groupEsbRegStateManagementImpl.retrieveCustomerInformation(customerManagementRequest, null, errors);
        assertNotNull(customerDataResponse);
        assertNotNull(customerDataResponse.getRegisteredState());

        assertThat(customerDataResponse.getRegisteredState().getRegistrationNumber(), is("9208081497"));
        assertThat(customerDataResponse.getRegisteredState().getRegistrationType(), is("ARBN"));
        assertThat(customerDataResponse.getRegisteredState().getCountry(), is("AU"));
        assertThat(customerDataResponse.getRegisteredState().getRegistrationState(), is("ACT"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testGetCustomerRegStateUpdateSuccess()
    {
        ServiceErrors errors = null;
        boolean status = groupEsbRegStateManagementImpl.updateCustomerInformation(customerRegData, errors);
        assertTrue(status);
        assertThat(status, is(true));
    }
}
