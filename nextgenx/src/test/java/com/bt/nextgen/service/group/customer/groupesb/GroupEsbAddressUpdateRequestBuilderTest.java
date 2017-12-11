package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.StandardPostalAddress;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.group.customer.groupesb.address.v7.GroupEsbAddressUpdateRequestV7Builder;
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
public class GroupEsbAddressUpdateRequestBuilderTest {

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
        when(cachedResponse.getIndividual().getAuditContext().isIsActive()).thenReturn(true);
        when(cachedResponse.getIndividual().getAuditContext().getVersionNumber()).thenReturn("1.0");

        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        req = new CustomerManagementRequestImpl();
        req.setCISKey(CISKey.valueOf("123456"));
        req.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        requestParam.setRequest(req);

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
        when(cachedResponse.getIndividual().getHasPostalAddressContactMethod()).thenReturn(postalList);
    }

    @Test
    public void testCreateAddressModificationRequest(){
        MaintainIPContactMethodsRequest request = GroupEsbAddressUpdateRequestV7Builder.createUpdateIPContactMethods(requestParam, cachedResponse);
        assertNotNull(request);

        assertThat(request.getInvolvedPartyType().value(), Is.is("Individual"));
        assertThat(request.getInvolvedPartyIdentifier().get(0).getInvolvedPartyId(), Is.is("123456"));
        assertThat(request.getInvolvedPartyIdentifier().get(0).getSourceSystem(), Is.is("UCM"));
        assertThat(request.getInvolvedPartyIdentifier().get(0).getIdentificationScheme().value(), Is.is("CISKey"));

        assertThat(request.getHasPostalAddressContactMethod().size(), Is.is(1));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getCity(), Is.is("CITY"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getCountry(), Is.is("AU"));
        //assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getState(), Is.is("NSW"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getFloorNumber(), Is.is("10"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getStreetType(), Is.is("ST"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getStreetName(), Is.is("STREET NAME"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasAddress()).getBuildingName(), Is.is("BUILDING"));
        assertThat((request.getHasPostalAddressContactMethod().get(0).getAuditContext()).isIsActive(), Is.is(true));
        //assertThat((request.getHasPostalAddressContactMethod().get(0).getInternalIdentifier()).getContactMethodId(), Is.is("12123312"));
    }

    @Test
    public void testOldValues(){
        MaintainIPContactMethodsRequest request = GroupEsbAddressUpdateRequestV7Builder.createUpdateIPContactMethods(requestParam, cachedResponse);
        assertNotNull(request);

        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getCity(), Is.is("CITY"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getCountry(), Is.is("AU"));
        //assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getState(), Is.is("NSW"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getFloorNumber(), Is.is("10"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getStreetType(), Is.is("ST"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getStreetName(), Is.is("STREET NAME"));
        assertThat(((au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress)request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getHasAddress()).getBuildingName(), Is.is("BUILDING"));
        assertThat((request.getHasPostalAddressContactMethod().get(0).getHasOldValues().getAuditContext().isIsActive()), Is.is(false));
        //assertNotNull(request.getHasPostalAddressContactMethod().get(0).getEndDate());
    }

}
