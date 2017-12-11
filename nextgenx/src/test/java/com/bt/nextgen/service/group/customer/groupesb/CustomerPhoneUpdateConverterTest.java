package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.TelephoneAddress;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAction;
import com.bt.nextgen.service.group.customer.groupesb.phone.v7.CustomerPhoneUpdateV7Converter;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by F057654 on 17/09/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerPhoneUpdateConverterTest {

    @Mock
    RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse;
    @Mock
    Individual individual;
    @Mock
    MaintenanceAuditContext auditContext;
    @Mock
    PhoneAddressContactMethod cachedPhoneAddressContactMethod;

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

        TelephoneAddress telephoneAddress = new TelephoneAddress();
        telephoneAddress.setAreaCode("02");
        telephoneAddress.setCountryCode("61");
        telephoneAddress.setLocalNumber("1111111111");


        PhoneAddressContactMethod phone = new PhoneAddressContactMethod();
        phone.setContactMedium("MOBILE");
        phone.setUsage("WRK");
        ContactMethodIdentifier identifier = new ContactMethodIdentifier();
        identifier.setContactMethodId("12123312");
        phone.setContactMethodIdentifier(identifier);
        phone.setHasAddress(telephoneAddress);
        List<PhoneAddressContactMethod> phoneList = new ArrayList<>();
        phoneList.add(phone);

        when(cachedResponse.getIndividual().getHasPhoneAddressContactMethod()).thenReturn(phoneList);
        requestParam.setRequest(req);

        CustomerPhone phone2 = new CustomerPhone();
        phone2.setGcm(true);
        phone2.setNumber("12531212");
        phone2.setAreaCode("2");
        phone2.setCountryCode("61");
        phone2.setAction(PhoneAction.ADD);
        phone2.setType(AddressMedium.MOBILE_PHONE_PRIMARY);

        CustomerPhone phone1 = new CustomerPhone();
        phone1.setGcm(true);
        phone1.setNumber("2323424323");
        phone1.setAreaCode("2");
        phone1.setCountryCode("61");
        phone1.setAction(PhoneAction.ADD);
        phone1.setType(AddressMedium.MOBILE_PHONE_SECONDARY);

        CustomerPhone phone3 = new CustomerPhone();
        phone3.setGcm(true);
        phone3.setNumber("2323424323");
        phone3.setAreaCode("2");
        phone3.setCountryCode("61");
        phone3.setAction(PhoneAction.ADD);
        phone3.setType(AddressMedium.MOBILE_PHONE_SECONDARY);

        List<Phone> phoneList1 = new ArrayList<>();
        phoneList1.add(phone1);
        phoneList1.add(phone2);
        phoneList1.add(phone3);
        requestParam.setPhoneNumbers(phoneList1);

        req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("123456"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        requestParam.setRequest(req);
    }

    @Test
    public void testConvertResponseInPhone() {
        when(cachedPhoneAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPhoneAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPhoneAddressContactMethod.getAuditContext().getVersionNumber()).thenReturn("1.0");
        List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod> response = CustomerPhoneUpdateV7Converter.convertResponseInPhone(requestParam, cachedResponse);
        assertNotNull(response);
        assertThat(response.size(), Is.is(3));
        assertThat(response.get(0).getHasAddress().getCountryCode(), Is.is("61"));
        assertThat(response.get(0).getHasAddress().getAreaCode(), Is.is("2"));
        assertThat(response.get(0).getHasAddress().getLocalNumber(), Is.is("2323424323"));
        assertThat(response.get(0).getUsageId(), Is.is("OTH"));
        assertThat(response.get(0).getHasAddress().getContactMedium(), Is.is("MOBILE"));

    }

    @Test
      public void testCreateAddPhoneAddress() {
        when(cachedPhoneAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPhoneAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPhoneAddressContactMethod.getAuditContext().getVersionNumber()).thenReturn("1.0");
        List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod> response = CustomerPhoneUpdateV7Converter.convertResponseInPhone(requestParam, cachedResponse);
        assertNotNull(response);
        assertThat(response.size(), Is.is(3));
        assertThat(response.get(0).getRequestedAction(), Is.is(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action.ADD));
        assertThat(response.get(1).getRequestedAction(), Is.is(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action.ADD));
    }

    @Test
    public void testDeletePhoneAddress() {
        when(cachedPhoneAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPhoneAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPhoneAddressContactMethod.getAuditContext().getVersionNumber()).thenReturn("1.0");
        List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod> response = CustomerPhoneUpdateV7Converter.convertResponseInPhone(requestParam, cachedResponse);
        assertNotNull(response);
        assertThat(response.size(), Is.is(3));
        assertThat(response.get(0).getHasAddress().getCountryCode(), Is.is("61"));
        assertThat(response.get(0).getHasAddress().getAreaCode(), Is.is("2"));
        assertThat(response.get(0).getHasAddress().getLocalNumber(), Is.is("2323424323"));
        assertThat(response.get(0).getUsageId(), Is.is("OTH"));
        assertThat(response.get(0).getHasAddress().getContactMedium(), Is.is("MOBILE"));
        assertThat(response.get(2).getRequestedAction(), Is.is(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action.ADD));
    }
}
