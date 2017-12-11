package com.bt.nextgen.service.group.customer.groupesb;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.config.SecureTestContext;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.integration.user.CISKey;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GroupEsbCustomerAddressManagementImplTest extends BaseSecureIntegrationTest
{
    @Autowired
    @Qualifier("addressManagementService")
    CustomerDataManagementIntegrationService addressManagementIntegrationService;

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testAddressReqSuccess()
    {
        ServiceErrors errors = null;
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("66786810081"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        List<CustomerManagementOperation> list = new ArrayList();
        list.add(CustomerManagementOperation.ADDRESS_UPDATE);
        req.setOperationTypes(list);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CustomerData customerData = addressManagementIntegrationService.retrieveCustomerInformation(req, null, errors);

        assertThat(customerData.getAddress(), notNullValue());
        assertThat(customerData.getAddress().getFloor(), Is.is("90"));
        assertThat(customerData.getAddress().getUnit(), Is.is("9"));
        assertThat(customerData.getAddress().getStreetName(), Is.is("Market"));
        assertThat(customerData.getAddress().getStreetType(), Is.is("St"));
        assertThat(customerData.getAddress().getBuilding(), Is.is("Ece Arc"));
        assertThat(customerData.getAddress().getCity(), Is.is("Sydney"));
        assertThat(customerData.getAddress().getState(), Is.is("NSW"));
        assertThat(customerData.getAddress().getPostCode(), Is.is("2000"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testUpdateAddressSuccess()
    {
        ServiceErrors errors = null;
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("66786810081"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        List<CustomerManagementOperation> list = new ArrayList();
        list.add(CustomerManagementOperation.ADDRESS_UPDATE);
        req.setOperationTypes(list);

        CustomerData customerData = new CustomerDataImpl();
        CustomerAddress address = new CustomerAddress();
        address.setFloorNumber("12");
        address.setUnitNumber("9");
        address.setStreetName("Market");
        address.setStreetType("St");
        address.setBuildingName("Ece Arc");
        address.setCity("Sydney");
        address.setStateName("NSW");
        address.setPostCode("2000");
        address.setCountryName("Au");
        customerData.setAddress(address);
        customerData.setRequest(req);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        boolean response = addressManagementIntegrationService.updateCustomerInformation(customerData, errors);
        assertThat(response, Is.is(true));

    }
}