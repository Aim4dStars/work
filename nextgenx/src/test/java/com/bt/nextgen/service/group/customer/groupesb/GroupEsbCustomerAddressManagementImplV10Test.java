package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.config.WebServiceConfig;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.CacheManagedCustomerDataManagementServiceV10Impl;
import com.bt.nextgen.service.group.customer.groupesb.address.v10.GroupEsbCustomerAddressManagementV10Impl;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by M041926 on 8/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerAddressManagementImplV10Test {

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private CmsService cmsService;

    private GroupEsbCustomerAddressManagementV10Impl service;

    private CacheManagedCustomerDataManagementServiceV10Impl cacheCustomerAddressManagementService;

    @Before
    public void setup() throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(GroupEsbWebServicesTestConfig.class, WebServiceConfig.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        WebServiceProvider provider = (WebServiceProvider) ctx.getBean("webServiceTemplateProvider");
        cacheCustomerAddressManagementService = new CacheManagedCustomerDataManagementServiceV10Impl(provider, userSamlService);
        service = new GroupEsbCustomerAddressManagementV10Impl(provider, userSamlService, cacheCustomerAddressManagementService, cmsService);
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void superTest() {
        ServiceErrors errors = null;
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("66786810081"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        List<CustomerManagementOperation> list = new ArrayList();
        list.add(CustomerManagementOperation.ADDRESS_UPDATE);
        req.setOperationTypes(list);
        CustomerData customerData = service.retrieveCustomerInformation(req, null, errors);

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
}
