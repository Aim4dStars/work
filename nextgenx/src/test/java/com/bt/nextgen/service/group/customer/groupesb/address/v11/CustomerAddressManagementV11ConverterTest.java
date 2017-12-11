package com.bt.nextgen.service.group.customer.groupesb.address.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.integration.domain.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by F058391 on 18/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerAddressManagementV11ConverterTest {

    @Test
    public void convertAddressFromResponse_standardPostal(){
        PostalAddressContactMethod postalAddressContactMethod = new PostalAddressContactMethod();
        StandardPostalAddress standardPostalAddress = getStandardPostalAddress();
        postalAddressContactMethod.setHasAddress(standardPostalAddress);
        AddressAdapterV11 address = (AddressAdapterV11) CustomerAddressManagementV11Converter.convertAddressFromResponse(postalAddressContactMethod);

        assertThat(address.getBuilding(), is("Westpac"));
        assertThat(address.getStreetName(), is("Kent"));
        assertThat(address.getStreetNumber(), is("275"));
        assertThat(address.getCity(), is("Sydney"));
        assertThat(address.getState(), is("NSW"));
        assertThat(address.getCountry(), is("AU"));
        assertThat(address.isStandardAddressFormat(), is(true));
    }

    @Test
    public void convertAddressFromResponse_nonStandardPostal(){
        PostalAddressContactMethod postalAddressContactMethod = new PostalAddressContactMethod();
        NonStandardPostalAddress nonStandardPostalAddress = getNonStandardPostalAddress();
        postalAddressContactMethod.setHasAddress(nonStandardPostalAddress);
        InternationalAddressV11Adapter address = (InternationalAddressV11Adapter) CustomerAddressManagementV11Converter.convertAddressFromResponse(postalAddressContactMethod);

        assertThat(address.isStandardAddressFormat(), is(false));
        assertThat(address.getAddressLine1(), is("Line1"));
        assertThat(address.getAddressLine2(), is("Line2"));
        assertThat(address.getCity(), is("Pune"));
        assertThat(address.getPostCode(), is("500000"));
    }

    @Test
    public void convertAddressFromResponse_nullInput(){
        Address address = CustomerAddressManagementV11Converter.convertAddressFromResponse(null);
        assertNotNull(address);
        assertTrue(address instanceof CustomerAddress);
    }

    @Test
    public void convertResponseInAddressModel_individual(){
        PostalAddressContactMethod postalAddressContactMethod = getPostalAddressContactMethod();
        postalAddressContactMethod.setUsage("RESIDENTIAL ADDRESS");
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        Individual individual = new Individual();
        response.setIndividual(individual);
        individual.getHasPostalAddressContactMethod().add(postalAddressContactMethod);

        assertNotNull(CustomerAddressManagementV11Converter.convertResponseInAddressModel(response, request));
    }

    @Test
    public void convertResponseInAddressModel_organisation(){
        PostalAddressContactMethod postalAddressContactMethod = getPostalAddressContactMethod();
        postalAddressContactMethod.setUsage("REGISTERED ADDRESS");
        RetrieveDetailsAndArrangementRelationshipsForIPsResponse response = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
        Organisation organisation = new Organisation();
        organisation.getHasPostalAddressContactMethod().add(postalAddressContactMethod);
        response.setOrganisation(organisation);

        assertNotNull(CustomerAddressManagementV11Converter.convertResponseInAddressModel(response, request));
    }

    private PostalAddressContactMethod getPostalAddressContactMethod() {
        PostalAddressContactMethod postalAddressContactMethod = new PostalAddressContactMethod();
        NonStandardPostalAddress nonStandardPostalAddress = getNonStandardPostalAddress();
        postalAddressContactMethod.setHasAddress(nonStandardPostalAddress);
        postalAddressContactMethod.setValidityStatus("A");
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setIsActive(true);
        postalAddressContactMethod.setAuditContext(maintenanceAuditContext);
        return postalAddressContactMethod;
    }

    private NonStandardPostalAddress getNonStandardPostalAddress() {
        NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
        nonStandardPostalAddress.setAddressLine1("Line1");
        nonStandardPostalAddress.setAddressLine2("Line2");
        nonStandardPostalAddress.setCity("Pune");
        nonStandardPostalAddress.setPostCode("500000");
        return nonStandardPostalAddress;
    }

    private StandardPostalAddress getStandardPostalAddress() {
        StandardPostalAddress address = new StandardPostalAddress();
        address.setStreetNumber("275");
        address.setBuildingName("Westpac");
        address.setStreetName("Kent");
        address.setStreetType("ST");
        address.setCity("Sydney");
        address.setPostCode("2000");
        address.setState("NSW");
        address.setCountry("AU");
        return address;
    }
}