package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.TelephoneAddress;
import com.bt.nextgen.service.group.customer.groupesb.phone.v7.CustomerPhoneV7Converter;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.user.CISKey;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by F057654 on 15/09/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerPhoneConverterTest {

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

        TelephoneAddress telephoneAddress = new TelephoneAddress();
        telephoneAddress.setAreaCode("02");
        telephoneAddress.setCountryCode("61");
        telephoneAddress.setLocalNumber("12131231");


        PhoneAddressContactMethod phone = new PhoneAddressContactMethod();
        phone.setContactMedium("MOBILE");
        phone.setUsage("WRK");
        phone.setHasAddress(telephoneAddress);
        List<PhoneAddressContactMethod> phoneList = new ArrayList<>();
        phoneList.add(phone);

        TelephoneAddress telephoneAddress1 = new TelephoneAddress();
        telephoneAddress1.setAreaCode("02");
        telephoneAddress1.setCountryCode("61");
        telephoneAddress1.setLocalNumber("12131231");


        PhoneAddressContactMethod phone1 = new PhoneAddressContactMethod();
        phone1.setContactMedium("Phone");
        phone1.setUsage("BUS");
        phone1.setHasAddress(telephoneAddress1);
        phoneList.add(phone1);

        TelephoneAddress telephoneAddress2 = new TelephoneAddress();
        telephoneAddress1.setAreaCode("02");
        telephoneAddress1.setCountryCode("61");
        telephoneAddress1.setLocalNumber("12131231");


        PhoneAddressContactMethod phone2 = new PhoneAddressContactMethod();
        phone2.setValidityStatus("O");
        phone2.setContactMedium("Phone");
        phone2.setUsage("BUS");
        phone2.setHasAddress(telephoneAddress2);
        phoneList.add(phone2);

        when(cachedResponse.getIndividual().getHasPhoneAddressContactMethod()).thenReturn(phoneList);
        requestParam.setRequest(req);
    }

    @Test
    public void testConvertResponseInPhone() {
        CustomerData data = CustomerPhoneV7Converter.convertResponseInPhone(cachedResponse, requestParam.getRequest());
        assertThat(data.getPhoneNumbers().get(0).getCountryCode(), Is.is("61"));
        assertThat(data.getPhoneNumbers().get(0).getAreaCode(), Is.is("2"));
        assertThat(data.getPhoneNumbers().get(0).getNumber(), Is.is("12131231"));
        assertThat(data.getPhoneNumbers().get(0).getType(), Is.is(AddressMedium.MOBILE_PHONE_SECONDARY));

        assertThat(data.getPhoneNumbers().get(1).getCountryCode(), Is.is("61"));
        assertThat(data.getPhoneNumbers().get(1).getAreaCode(), Is.is("2"));
        assertThat(data.getPhoneNumbers().get(1).getNumber(), Is.is("12131231"));
        assertThat(data.getPhoneNumbers().get(1).getType(), Is.is(AddressMedium.BUSINESS_TELEPHONE));
        assertThat(data.getPhoneNumbers().size(),Is.is(2));
    }

}
