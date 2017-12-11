package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.StandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.TelephoneAddress;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.group.customer.groupesb.address.v7.CustomerAddressUpdateV7Converter;
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
public class CustomerAddressUpdateConverterTest {

    @Mock
    RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse;
    @Mock
    Individual individual;
    @Mock
    MaintenanceAuditContext auditContext;
    @Mock
    PostalAddressContactMethod cachedPostalAddressContactMethod;

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


        ContactMethodIdentifier identifier = new ContactMethodIdentifier();
        identifier.setContactMethodId("12123312");
        List<PostalAddressContactMethod> postalList = new ArrayList<>();

        StandardPostalAddress standardPostalAddress = new StandardPostalAddress();
        standardPostalAddress.setBuildingName("building");
        standardPostalAddress.setCity("city");
        standardPostalAddress.setState("NSW");
        standardPostalAddress.setCountry("AU");
        standardPostalAddress.setFloorNumber("10");
        standardPostalAddress.setStreetType("ST");
        standardPostalAddress.setStreetName("street name");
        standardPostalAddress.setPostCode("2000");

        PostalAddressContactMethod postalAddressContactMethod = new PostalAddressContactMethod();
        postalAddressContactMethod.setAuditContext(auditContext);
        postalAddressContactMethod.setContactMethodIdentifier(identifier);
        postalAddressContactMethod.setUsage(ServiceConstants.INDIVIDUAL_ADDRESS_USAGE);
        postalAddressContactMethod.setHasAddress(standardPostalAddress);
        postalList.add(postalAddressContactMethod);

        when(cachedResponse.getIndividual().getHasPostalAddressContactMethod()).thenReturn(postalList);
        requestParam.setRequest(req);

        CustomerAddress address = new CustomerAddress();
        address.setCountryName("AU");
        address.setStateName("NSW");
        address.setFloorNumber("10");
        address.setCity("city");
        address.setStreetName("street name");
        address.setPostCode("2000");
        address.setStreetType("ST");
        address.setBuildingName("building");

        requestParam.setAddress(address);

        req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("123456"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        requestParam.setRequest(req);
    }

    @Test
    public void testConvertResponseInPhone() {
        when(cachedPostalAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPostalAddressContactMethod.getAuditContext()).thenReturn(auditContext);
        when(cachedPostalAddressContactMethod.getAuditContext().getVersionNumber()).thenReturn("1.0");
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod postalAddressContactMethod = CustomerAddressUpdateV7Converter.createPostalAddressToBeSentForUpdate(cachedResponse.getIndividual().getHasPostalAddressContactMethod().get(0), requestParam);
        assertNotNull(postalAddressContactMethod);
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getCity(), Is.is("CITY"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getCountry(), Is.is("AU"));
        //assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getState(), Is.is("NSW"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getFloorNumber(), Is.is("10"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getStreetType(), Is.is("ST"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getStreetName(), Is.is("STREET NAME"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)postalAddressContactMethod.getHasAddress()).getBuildingName(), Is.is("BUILDING"));
    }

}
