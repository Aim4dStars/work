package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
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
import static org.junit.Assert.assertThat;

public class GroupEsbCustomerTaxResidenceCountryTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("taxResiCountryManagementV11Service")
    CustomerDataManagementIntegrationService groupEsbCustomerTaxResidenceCountryV11;

    @Before
    public void setUp(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("brandSilo","WPAC");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testRetrieveCustomerInformation() {
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        request.setCISKey(CISKey.valueOf("66786610081"));
        List<CustomerManagementOperation> operations = new ArrayList<>();
        operations.add(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        request.setOperationTypes(operations);
        ServiceErrors errors = new ServiceErrorsImpl();
        CustomerData res = groupEsbCustomerTaxResidenceCountryV11.retrieveCustomerInformation(request, null, errors);
        assertThat(res.getTaxResidenceCountries().isEmpty(), is(false));
        assertThat(res.getTaxResidenceCountries().get(0).getTin(), Is.is("123456789"));
    }

    @Test
    public void testRetrieveCustomerInformationOrganisation() {
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
        request.setCISKey(CISKey.valueOf("87458125478"));
        List<CustomerManagementOperation> operations = new ArrayList<>();
        operations.add(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        request.setOperationTypes(operations);

        ServiceErrors errors = new ServiceErrorsImpl();
        CustomerData res = groupEsbCustomerTaxResidenceCountryV11.retrieveCustomerInformation(request, null, errors);
        assertThat(res.getTaxResidenceCountries().isEmpty(), is(false));
        assertThat(res.getTaxResidenceCountries().get(0).getTin(), Is.is("123489789"));
    }

    @Test
    public void testIndividualUpdate() {
        CustomerData customerData = new CustomerDataImpl();
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        request.setCISKey(CISKey.valueOf("47658592"));
        customerData.setRequest(request);

        List<TaxResidenceCountry> taxResidenceCountryList = new ArrayList<>();
        TaxResidenceCountry taxResidenceCountry = new TaxResidenceCountry();
        taxResidenceCountry.setTin("123456");
        taxResidenceCountry.setResidenceCountry("US");
        taxResidenceCountry.setVersionNumber("1");
        taxResidenceCountry.setStartDate(new DateTime());
        taxResidenceCountryList.add(taxResidenceCountry);
        customerData.setTaxResidenceCountries(taxResidenceCountryList);

        ServiceErrors errors = new ServiceErrorsImpl();
        boolean status = groupEsbCustomerTaxResidenceCountryV11.updateCustomerInformation(customerData, errors);

        Assert.assertThat("Individual Update status is SUCCESS", status, is(true));
    }

    @Test
    public void testOrganisationUpdate() {
        CustomerData customerData = new CustomerDataImpl();
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
        request.setCISKey(CISKey.valueOf("47658591"));
        customerData.setRequest(request);

        List<TaxResidenceCountry> taxResidenceCountryList = new ArrayList<>();
        TaxResidenceCountry taxResidenceCountry = new TaxResidenceCountry();
        taxResidenceCountry.setTin("49FIE93");
        taxResidenceCountry.setResidenceCountry("UK");
        taxResidenceCountry.setVersionNumber("1");
        taxResidenceCountry.setStartDate(new DateTime());
        taxResidenceCountryList.add(taxResidenceCountry);
        customerData.setTaxResidenceCountries(taxResidenceCountryList);

        ServiceErrors errors = new ServiceErrorsImpl();
        boolean status = groupEsbCustomerTaxResidenceCountryV11.updateCustomerInformation(customerData, errors);

        Assert.assertThat("Organsation Update status is SUCCESS", status, is(false));
    }

}
