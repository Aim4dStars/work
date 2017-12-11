/**
 * 
 */
package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;
import com.bt.nextgen.service.groupesb.createorganisationip.v5.CreateOrganisationIPIntegrationService;
import com.bt.nextgen.service.groupesb.createorganisationip.v5.CreateOrganisationIPReq;
import com.bt.nextgen.service.groupesb.iptoiprelationships.v4.IPToIPRelationshipsIntegrationService;
import com.bt.nextgen.serviceops.model.CreateOraganisationIPReqModel;
import com.bt.nextgen.serviceops.model.RetriveIpToIpRelationshipReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateOrganisationIPDataDtoServiceImplTest {
    @InjectMocks
    private CreateOrganisationIPDataDtoServiceImpl createOrganisationIPDataDtoServiceImpl;

    @Mock
    private CustomerRawData customerRawData;

    @Mock
    private CreateOrganisationIPIntegrationService createOrganisationIPIntegrationService;
    @Test
    public void testRetitriveIPToIpRelationShip() throws JsonProcessingException {
        CreateOraganisationIPReqModel req = new CreateOraganisationIPReqModel();
        req.setAddresseeNameText("Test");
        req.setAddressType("test");
        req.setAddrspriorityLevel("Primary");
        req.setCharacteristicCode("test");
        req.setCharacteristicType("test");
        req.setCharacteristicValue("value");
        req.setCity("test");
        req.setCountry("test");
        req.setFrn("123343");
        req.setFullName("test");
        req.setFrntype("ACN");
        req.setIndustryCode("wegyfy");
        req.setIsIssuedAtC("test");
        req.setIsForeignRegistered("123444");
        req.setIsIssuedAtS("ghfjjifj");
        req.setOrganisationLegalStructureValue("tyrwq");
        req.setPersonType("ettyry");
        req.setPostCode("123343");
        req.setPriorityLevel("Primary");
        req.setPurposeOfBusinessRelationship("asdf");
        req.setRegistrationNumber("123445455");
        req.setRegistrationNumberType("ACN");
        req.setSilo("WPAC");
        req.setSourceOfFunds("qweer");
        req.setSourceOfWealth("qwerrr");
        req.setStartDate("2017-12-12");
        req.setState("NSW");
        req.setStreetName("werttysy");
        req.setStreetNumber("r266376");
        req.setStreetType("werty");
        req.setUsage("erettey");
        ServiceErrors serviceError = new ServiceErrorsImpl();
        when(
                createOrganisationIPIntegrationService.createorganisationIP(
                        any(CreateOrganisationIPReq.class), any(ServiceErrors.class))).thenReturn(customerRawData);

        CustomerRawData customerRawData = createOrganisationIPDataDtoServiceImpl.create(req, serviceError);
        assertNotNull(customerRawData);
    }
}
