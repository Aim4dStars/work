package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class GroupEsbCustomerPreferredNameManagementImplTest extends BaseSecureIntegrationTest
{
    @Autowired
    @Qualifier("preferredNameManagementService")
    CustomerDataManagementIntegrationService preferredNameManagementIntegrationService;

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testPreferredNameReqSuccess()
    {
        ServiceErrors errors = null;
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("66786810081"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        List<CustomerManagementOperation> list = new ArrayList();
        list.add(CustomerManagementOperation.PREFERRED_NAME_UPDATE);
        req.setOperationTypes(list);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CustomerData customerData = preferredNameManagementIntegrationService.retrieveCustomerInformation(req, null, errors);

        assertThat(customerData.getPreferredName(), notNullValue());
        assertThat(customerData.getPreferredName(), Is.is("Ds"));
    }
}