package com.bt.nextgen.service.group.customer.groupesb.taxresidencecountry.v11;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationip.v5.svc0339.ModifyOrganisationIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyorganisationip.v5.svc0339.RegistrationArrangement;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.integration.user.CISKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class GroupEsbOrganisationTaxResidenceUpdateRequestBuilderTest {

    @Test
    public void createModifyOrganisationIPRequestTest() {
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
        request.setCISKey(CISKey.valueOf("87458125478"));
        List<CustomerManagementOperation> operations = new ArrayList<>();
        operations.add(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
        request.setOperationTypes(operations);

        List<TaxResidenceCountry> taxResidenceCountries = new ArrayList<>();
        TaxResidenceCountry taxResidenceCountry1 = new TaxResidenceCountry();
        TaxResidenceCountry taxResidenceCountry2 = new TaxResidenceCountry();
        TaxResidenceCountry taxResidenceCountry3 = new TaxResidenceCountry();
        TaxResidenceCountry taxResidenceCountry4 = new TaxResidenceCountry();
        TaxResidenceCountry taxResidenceCountry5 = new TaxResidenceCountry();

        taxResidenceCountry1.setResidenceCountry("AF");
        taxResidenceCountry1.setTin("236598457");
        taxResidenceCountry1.setVersionNumber("1");
        taxResidenceCountry1.setStartDate(new DateTime());

        taxResidenceCountry2.setResidenceCountry("NZ");
        taxResidenceCountry2.setExemptionReason("RC000004");
        taxResidenceCountry2.setVersionNumber("1");
        taxResidenceCountry2.setStartDate(new DateTime());

        taxResidenceCountry3.setResidenceCountry("FOREIGN");
        taxResidenceCountry3.setTin("Y");
        taxResidenceCountry3.setStartDate(new DateTime());
        taxResidenceCountry3.setEndDate(new DateTime());

        taxResidenceCountry4.setResidenceCountry("DZ");
        taxResidenceCountry4.setExemptionReason("RC000001");
        taxResidenceCountry4.setVersionNumber("1");
        taxResidenceCountry4.setStartDate(new DateTime());
        taxResidenceCountry4.setEndDate(new DateTime());

        taxResidenceCountry5.setResidenceCountry("FOREIGN");
        taxResidenceCountry5.setTin("N");
        taxResidenceCountry5.setStartDate(new DateTime());

        taxResidenceCountries.add(taxResidenceCountry1);
        taxResidenceCountries.add(taxResidenceCountry2);
        taxResidenceCountries.add(taxResidenceCountry3);
        taxResidenceCountries.add(taxResidenceCountry4);
        taxResidenceCountries.add(taxResidenceCountry5);

        CustomerDataImpl customerData = new CustomerDataImpl();
        customerData.setTaxResidenceCountries(taxResidenceCountries);
        customerData.setRequest(request);

        ModifyOrganisationIPRequest modifyOrganisationIPRequest = GroupEsbOrganisationTaxResidenceUpdateRequestBuilder.createModifyOrganisationIPRequest(customerData);
        Assert.assertNotNull(modifyOrganisationIPRequest);
        Assert.assertEquals("87458125478", modifyOrganisationIPRequest.getOrganisation().getInvolvedPartyIdentifier().get(0).getInvolvedPartyId());
        Assert.assertEquals("N", modifyOrganisationIPRequest.getOrganisation().getIsForeignRegistered());

        Assert.assertNotNull(modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration());
        Assert.assertThat("Country list size is equal to 5", 5, equalTo(modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration().size()));
        RegistrationArrangement registrationArrangement = modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration().get(0);
        Assert.assertEquals("AF", registrationArrangement.getIsIssuedAt().getCountry());
        Assert.assertEquals("236598457", registrationArrangement.getRegistrationIdentifier().getRegistrationNumber());
        Assert.assertTrue(registrationArrangement.getAuditContext().isIsActive());

        registrationArrangement = modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration().get(1);
        Assert.assertEquals("NZ", registrationArrangement.getIsIssuedAt().getCountry());
        Assert.assertEquals("RC000004", registrationArrangement.getNoteText());
        Assert.assertTrue(registrationArrangement.getAuditContext().isIsActive());

        registrationArrangement = modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration().get(2);
        Assert.assertEquals("FOREIGN", registrationArrangement.getRegistrationIdentifier().getRegistrationNumberType().value());
        Assert.assertEquals("Y", registrationArrangement.getRegistrationIdentifier().getRegistrationNumber());
        Assert.assertNull(registrationArrangement.getIsIssuedAt());
        Assert.assertNull(registrationArrangement.getNoteText());
        Assert.assertFalse(registrationArrangement.getAuditContext().isIsActive());

        registrationArrangement = modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration().get(3);
        Assert.assertEquals("DZ", registrationArrangement.getIsIssuedAt().getCountry());
        Assert.assertEquals("RC000001", registrationArrangement.getNoteText());
        Assert.assertFalse(registrationArrangement.getAuditContext().isIsActive());

        registrationArrangement = modifyOrganisationIPRequest.getOrganisation().getHasForeignRegistration().get(4);
        Assert.assertThat("Registraiton type should be FOREIGN", "FOREIGN", equalTo(registrationArrangement.getRegistrationIdentifier().getRegistrationNumberType().FOREIGN.toString()));
        Assert.assertThat("Registration value should be N", "N", equalTo(registrationArrangement.getRegistrationIdentifier().getRegistrationNumber()));
        Assert.assertTrue(registrationArrangement.getAuditContext().isIsActive());
    }
}
