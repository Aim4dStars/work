package com.bt.nextgen.service.group.customer.groupesb.phone.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.TelephoneAddress;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * Created by F058391 on 18/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerPhoneV11ConverterTest {
    private static final String GESB_MOBILE_CONSTANT = "MOBILE";
    private static final String GESB_PHONE_CONSTANT = "Phone";

    @Test
    public void convertResponseInPhoneIndividual_Mobile(){
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject =
                getRetrieveDetailsAndArrangementRelationshipsForIPsResponseForIndividual(getPhoneAddressContactMethod(GESB_MOBILE_CONSTANT, "A", "11111111", "04", "61"));

        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);

        CustomerData customerData = CustomerPhoneV11Converter.convertResponseInPhone(responseObject, request);
        assertThat(customerData.getPhoneNumbers().size(), is(1));

        Phone phone = customerData.getPhoneNumbers().get(0);
        assertThat(phone.getNumber(), is("11111111"));
        assertThat(phone.getAreaCode(), is("4"));
        assertThat(phone.getCountryCode(), is("61"));
    }

    @Test
    public void convertResponseInPhoneIndividual_Phone(){
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject =
                getRetrieveDetailsAndArrangementRelationshipsForIPsResponseForIndividual(getPhoneAddressContactMethod(GESB_MOBILE_CONSTANT, "A", "22222222", "04", "61"));

        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);

        CustomerData customerData = CustomerPhoneV11Converter.convertResponseInPhone(responseObject, request);
        assertThat(customerData.getPhoneNumbers().size(), is(1));

        Phone phone = customerData.getPhoneNumbers().get(0);
        assertThat(phone.getNumber(), is("22222222"));
        assertThat(phone.getAreaCode(), is("4"));
        assertThat(phone.getCountryCode(), is("61"));
    }

    @Test
    public void convertResponseInPhoneIndividual_Obsolete(){
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject =
                getRetrieveDetailsAndArrangementRelationshipsForIPsResponseForIndividual(getPhoneAddressContactMethod(GESB_MOBILE_CONSTANT, "O", "22222222", "04", "61"));

        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);

        CustomerData customerData = CustomerPhoneV11Converter.convertResponseInPhone(responseObject, request);
        assertThat(customerData.getPhoneNumbers().size(), is(0));
    }

    @Test
    public void convertResponseInPhoneOrganisation_phone(){
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        responseObject.setOrganisation(new Organisation());
        responseObject.getOrganisation().getHasPhoneAddressContactMethod().add(getPhoneAddressContactMethod(GESB_PHONE_CONSTANT, "A", "11111111", "02", "61"));

        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.ORGANISATION);

        CustomerData customerData = CustomerPhoneV11Converter.convertResponseInPhone(responseObject, request);
        assertThat(customerData.getPhoneNumbers().size(), is(1));

        Phone phone = customerData.getPhoneNumbers().get(0);
        assertThat(phone.getNumber(), is("11111111"));
        assertThat(phone.getAreaCode(), is("2"));
        assertThat(phone.getCountryCode(), is("61"));
    }

    @Test
    public void convertResponseInAddressMedium_mobile(){
        PhoneAddressContactMethod phoneAddressContactMethod = getPhoneAddressContactMethod(GESB_MOBILE_CONSTANT, "A", "11111111", "02", "61");
        assertThat(CustomerPhoneV11Converter.convertResponseInAddressMedium(phoneAddressContactMethod), is(AddressMedium.MOBILE_PHONE_SECONDARY));
    }

    @Test
    public void convertResponseInAddressMedium_home(){
        PhoneAddressContactMethod phoneAddressContactMethod = getPhoneAddressContactMethod(GESB_PHONE_CONSTANT, "A", "11111111", "02", "61");
        phoneAddressContactMethod.setUsage("HOM");
        assertThat(CustomerPhoneV11Converter.convertResponseInAddressMedium(phoneAddressContactMethod), is(AddressMedium.PERSONAL_TELEPHONE));
    }

    @Test
    public void convertResponseInAddressMedium_work(){
        PhoneAddressContactMethod phoneAddressContactMethod = getPhoneAddressContactMethod(GESB_PHONE_CONSTANT, "A", "11111111", "02", "61");
        phoneAddressContactMethod.setUsage("WRK");
        assertThat(CustomerPhoneV11Converter.convertResponseInAddressMedium(phoneAddressContactMethod), is(AddressMedium.BUSINESS_TELEPHONE));
    }

    @Test
    public void convertResponseInAddressMedium_business(){
        PhoneAddressContactMethod phoneAddressContactMethod = getPhoneAddressContactMethod(GESB_PHONE_CONSTANT, "A", "11111111", "02", "61");
        phoneAddressContactMethod.setUsage("WRK");
        assertThat(CustomerPhoneV11Converter.convertResponseInAddressMedium(phoneAddressContactMethod), is(AddressMedium.BUSINESS_TELEPHONE));
    }

    @Test
    public void convertResponseInAddressMedium_other(){
        PhoneAddressContactMethod phoneAddressContactMethod = getPhoneAddressContactMethod(GESB_PHONE_CONSTANT, "A", "11111111", "02", "61");
        phoneAddressContactMethod.setUsage("OTH");
        assertThat(CustomerPhoneV11Converter.convertResponseInAddressMedium(phoneAddressContactMethod), is(AddressMedium.OTHER));
    }

    private PhoneAddressContactMethod getPhoneAddressContactMethod(String contactMedium, String validityStatus, String localNumber, String areaCode, String countryCode) {
        PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
        phoneAddressContactMethod.setContactMedium(contactMedium);
        phoneAddressContactMethod.setValidityStatus(validityStatus);
        TelephoneAddress value = new TelephoneAddress();
        value.setLocalNumber(localNumber);
        value.setAreaCode(areaCode);
        value.setCountryCode(countryCode);
        phoneAddressContactMethod.setHasAddress(value);
        return phoneAddressContactMethod;
    }

    private RetrieveDetailsAndArrangementRelationshipsForIPsResponse getRetrieveDetailsAndArrangementRelationshipsForIPsResponseForIndividual(PhoneAddressContactMethod a) {
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse responseObject = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        responseObject.setIndividual(new Individual());
        responseObject.getIndividual().getHasPhoneAddressContactMethod().add(a);
        return responseObject;
    }
}