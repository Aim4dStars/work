package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.EmailAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.TelephoneAddress;
import com.bt.nextgen.service.group.customer.groupesb.email.v7.CustomerEmailV7;
import com.bt.nextgen.service.group.customer.groupesb.email.v7.GroupEsbCustomerContactDetailsRequestV7Builder;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAction;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerContactDetailsRequestBuilderTest {

    @Mock
    RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse;
    @Mock
    Individual individual;
    @Mock
    MaintenanceAuditContext auditContext;

    CustomerData requestParam= new CustomerDataImpl();

    @org.junit.Before
    public void setUp() throws Exception {

        when(cachedResponse.getIndividual()).thenReturn(individual);
        when(cachedResponse.getIndividual().getAuditContext()).thenReturn(auditContext);
        when(cachedResponse.getIndividual().getAuditContext()).thenReturn(auditContext);
        when(cachedResponse.getIndividual().getAuditContext().getVersionNumber()).thenReturn("1.0");
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("66786810081"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        List<CustomerManagementOperation> list = new ArrayList();
        list.add(CustomerManagementOperation.PREFERRED_NAME_UPDATE);
        req.setOperationTypes(list);

        ContactMethodIdentifier identifier = new ContactMethodIdentifier();
        identifier.setContactMethodId("121212");

        TelephoneAddress address = new TelephoneAddress();
        address.setLocalNumber("123123");
        address.setCountryCode("61");
        address.setAreaCode("2");

        MaintenanceAuditContext context = new MaintenanceAuditContext();
        context.setIsActive(true);
        context.setVersionNumber("123");

        List<PhoneAddressContactMethod> phoneList = new ArrayList<>();
        PhoneAddressContactMethod phone1 = new PhoneAddressContactMethod();
        phone1.setContactMethodIdentifier(identifier);
        phone1.setHasAddress(address);
        phone1.setUsage("OTH");
        phone1.setContactMedium("MOBILE");
        phone1.setAuditContext(context);
        phoneList.add(phone1);


        List<EmailAddressContactMethod> emailList = new ArrayList<>();
        EmailAddress email = new EmailAddress();
        email.setEmailAddress("DS@DS.com");
        EmailAddressContactMethod email1 = new EmailAddressContactMethod();
        email1.setContactMethodIdentifier(identifier);
        email1.setHasAddress(email);
        email1.setUsage("OTH");
        email1.setAuditContext(context);
        emailList.add(email1);

        CustomerPhone phone = new CustomerPhone();
        phone.setCountryCode("61");
        phone.setAreaCode("2");
        phone.setNumber("111111111");
        phone.setType(AddressMedium.BUSINESS_TELEPHONE);
        phone.setModificationSeq("1231231");
        phone.setAction(PhoneAction.ADD);
        List<Phone> phones = new ArrayList<>();
        phones.add(phone);

        CustomerEmailV7 custEmail = new CustomerEmailV7();
        custEmail.setModificationSeq("123123");
        custEmail.setEmail("DS@Ds.com");
        custEmail.setType(AddressMedium.EMAIL_ADDRESS_SECONDARY);
        custEmail.setAction(CustomerEmailV7.EmailAction.ADD);

        CustomerEmailV7 custEmail1 = new CustomerEmailV7();
        custEmail1.setModificationSeq("123123");
        custEmail1.setEmail("DS@DS.com");
        custEmail1.setType(AddressMedium.EMAIL_ADDRESS_SECONDARY);
        custEmail1.setAction(CustomerEmailV7.EmailAction.DELETE);

        List<Email> emails = new ArrayList<>();
        emails.add(custEmail);
        emails.add(custEmail1);

        requestParam.setPhoneNumbers(phones);
        requestParam.setEmails(emails);
        requestParam.setRequest(req);

        when(cachedResponse.getIndividual().getHasPhoneAddressContactMethod()).thenReturn(phoneList);
        when(cachedResponse.getIndividual().getHasEmailAddressContactMethod()).thenReturn(emailList);

    }

    @Test
    public void testCreateContactDetailsModificationRequest(){
        MaintainIPContactMethodsRequest request = GroupEsbCustomerContactDetailsRequestV7Builder.createContactDetailsModificationRequest(requestParam, cachedResponse);
        assertNotNull(request);

        assertThat(request.getInvolvedPartyType().value(), Is.is("Individual"));
        assertThat(request.getInvolvedPartyIdentifier().get(0).getInvolvedPartyId(), Is.is("66786810081"));
        assertThat(request.getInvolvedPartyIdentifier().get(0).getSourceSystem(), Is.is("UCM"));
        assertThat(request.getInvolvedPartyIdentifier().get(0).getIdentificationScheme().value(), Is.is("CISKey"));

        assertThat(request.getHasPhoneAddressContactMethod().size(), Is.is(1));
        assertThat(request.getHasPhoneAddressContactMethod().get(0).getHasAddress().getCountryCode(), Is.is("61"));
        assertThat(request.getHasPhoneAddressContactMethod().get(0).getHasAddress().getAreaCode(), Is.is("2"));
        assertThat(request.getHasPhoneAddressContactMethod().get(0).getHasAddress().getLocalNumber(), Is.is("111111111"));
        assertThat(request.getHasPhoneAddressContactMethod().get(0).getRequestedAction().value(), Is.is("Add"));
        assertThat(request.getHasPhoneAddressContactMethod().get(0).getUsageId(),Is.is("WRK"));

        assertThat(request.getHasEmailAddressContactMethod().get(0).getHasAddress().getEmailAddress(), Is.is("DS@Ds.com"));
        assertThat(request.getHasEmailAddressContactMethod().get(0).getRowSetIdIdentifier().getSequenceNumber(), Is.is("0000000001"));
        assertThat(request.getHasEmailAddressContactMethod().get(0).getUsageId(),Is.is("OTH"));
        assertThat(request.getHasEmailAddressContactMethod().get(0).getRequestedAction().value(), Is.is("Add"));

        assertThat(request.getHasEmailAddressContactMethod().get(1).getHasAddress().getEmailAddress(), Is.is("DS@DS.com"));
        assertThat(request.getHasEmailAddressContactMethod().get(1).getRowSetIdIdentifier().getSequenceNumber(), Is.is("0000000002"));
        assertThat(request.getHasEmailAddressContactMethod().get(1).getInternalIdentifier().getContactMethodId(), Is.is("121212"));
        assertThat(request.getHasEmailAddressContactMethod().get(1).getUsageId(), Is.is("OTH"));
        assertThat(request.getHasEmailAddressContactMethod().get(1).getRequestedAction().value(), Is.is("Delete"));

    }


}
