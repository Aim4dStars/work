package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.email.CustomerEmail;
import com.bt.nextgen.service.group.customer.groupesb.email.GroupEsbCustomerContactDetailsManagementImpl;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAction;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import org.hamcrest.core.Is;
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

public class GroupEsbCustomerContactDetailsManagementImplTest extends BaseSecureIntegrationTest {

    @Autowired
    @Qualifier("contactDetailsManagementService")
    GroupEsbCustomerContactDetailsManagementImpl groupEsbCustomerContactDetailsManagement;

    private CustomerManagementRequest request;
    private CustomerData customerInstance;

    @Before
    public void setUp() throws Exception
    {
        request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        request.setCISKey(CISKey.valueOf("66786810081"));
        List<CustomerManagementOperation> operations = new ArrayList<>();
        operations.add(CustomerManagementOperation.CONTACT_DETAILS_UPDATE);
        request.setOperationTypes(operations);

        customerInstance = new CustomerDataImpl();
        List<Email> emailList = new ArrayList<>();
        CustomerEmail email = new CustomerEmail();
        email.setEmail("DS@DS.com");
        emailList.add(email);
        customerInstance.setEmails(emailList);

        List<Phone> phoneList = new ArrayList<>();
        CustomerPhone phone = new CustomerPhone();
        phone.setNumber("2344");
        phone.setCountryCode("61");
        phone.setAreaCode("02");
        phone.setModificationSeq("123423");
        phone.setType(AddressMedium.BUSINESS_TELEPHONE);
        phone.setAction(PhoneAction.ADD);
        phoneList.add(phone);
        customerInstance.setPhoneNumbers(phoneList);

        customerInstance.setRequest(request);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testRetrieveCustomerEmailReqSuccess()
    {
        ServiceErrors errors = null;
        CustomerData res = groupEsbCustomerContactDetailsManagement.retrieveCustomerInformation(request, null, errors);
        assertThat(res.getEmails().isEmpty(), is(false));
        assertThat(res.getEmails().get(0).getEmail(), Is.is("DS@DS.com"));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testEmailAddSuccess() {
        ServiceErrors errors = null;
        CustomerEmail customerEmail = (CustomerEmail) customerInstance.getEmails().get(0);
        customerEmail.setAction(CustomerEmail.EmailAction.ADD);
        boolean status = groupEsbCustomerContactDetailsManagement.updateCustomerInformation(customerInstance, errors);
        assertThat(status,is(true));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testEmailDeleteSuccess() {
        ServiceErrors errors = null;
        CustomerEmail customerEmail = (CustomerEmail) customerInstance.getEmails().get(0);
        customerEmail.setAction(CustomerEmail.EmailAction.DELETE);

        boolean status = groupEsbCustomerContactDetailsManagement.updateCustomerInformation(customerInstance, errors);
        assertThat(status,is(true));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testEmailUpdateSuccess() {

        ServiceErrors errors = null;

        CustomerEmail customerEmail = (CustomerEmail) customerInstance.getEmails().get(0);
        customerEmail.setAction(CustomerEmail.EmailAction.MODIFY);
        customerEmail.setEmail("abcChanged@gmail.com");
        customerEmail.setOldAddress("DS@DS.com");

        boolean status = groupEsbCustomerContactDetailsManagement.updateCustomerInformation(customerInstance, errors);
        assertThat(status,is(true));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testRetrieveCustomerPhoneReqSuccess()
    {
        ServiceErrors errors = null;
        CustomerData res = groupEsbCustomerContactDetailsManagement.retrieveCustomerInformation(request, null, errors);
        assertThat(res.getPhoneNumbers().isEmpty(), is(false));
        assertThat(res.getPhoneNumbers().size(), is(11));

        assertThat(res.getPhoneNumbers().get(0).getAreaCode(), Is.is("4"));
        assertThat(res.getPhoneNumbers().get(0).getCountryCode(), Is.is("61"));
        assertThat(res.getPhoneNumbers().get(0).getNumber(), Is.is("01872264"));
    }
}
