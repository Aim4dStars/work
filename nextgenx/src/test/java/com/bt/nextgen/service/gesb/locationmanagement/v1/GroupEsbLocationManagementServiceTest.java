package com.bt.nextgen.service.gesb.locationmanagement.v1;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.AddressType;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.PostalAddressContactMethod;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.Provider;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.ProviderAttribute;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressRequest;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.RetrievePostalAddressResponse;
import au.com.westpac.gn.locationmanagement.services.locationmanagement.xsd.retrievepostaladdress.v1.svc0454.StandardPostalAddress;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.WebServiceProviderConfig;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by F030695 on 26/10/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbLocationManagementServiceTest {

    @InjectMocks
    private GroupEsbLocationManagementServiceImpl addressService;

    @Mock
    private WebServiceProvider webServiceProvider;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private CmsService cmsService;

    @Test
    public void test_retrievePostalAddress_success() {
        List<ProviderAttribute> providerAttributesForBuildingNameAndLevel = new ArrayList<>();
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForBuildingNameAndLevel, getAddress("Pitt"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertNull(address.getUnit());
        assertThat(address.getStreetNumber(), is("33"));
        assertThat(address.getStreetName(), is("Pitt"));
        assertThat(address.getStreetType(), is("St"));
        assertThat(address.getSuburb(), is("Sydney"));
        assertThat(address.getPostCode(), is("2000"));
        assertThat(address.getStateAbbr(), is("NSW"));
        assertThat(address.getCountryCode(), is("AU"));
    }

    @Test
    public void test_retrievePostalAddress_withPAFBuildingLevelAndName() {
        List<ProviderAttribute> providerAttributesForBuildingNameAndLevel = getProviderAttributesForBuildingNameAndLevel("Lennys Place", StringUtils.EMPTY, "28", StringUtils.EMPTY);
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForBuildingNameAndLevel, getAddress("Pitt"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getBuilding(), is("Lennys Place"));
        assertThat(address.getFloor(), is("28"));
    }

    @Test
    public void test_retrievePostalAddress_withGNAFBuildingLevelAndName() {
        List<ProviderAttribute> providerAttributesForBuildingNameAndLevel = getProviderAttributesForBuildingNameAndLevel(StringUtils.EMPTY, "Lennys Place", StringUtils.EMPTY, "28");
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForBuildingNameAndLevel, getAddress("Pitt"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getBuilding(), is("Lennys Place"));
        assertThat(address.getFloor(), is("28"));
    }

    @Test
    public void test_retrievePostalAddress_withGNAFUnitNumber() {
        List<ProviderAttribute> providerAttributesForUnitNumber = getProviderAttributesForUnitNumber("180", "180");
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForUnitNumber, getAddress("Pitt"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getUnit(), is("180"));
    }

    @Test
    public void test_retrievePostalAddress_withGNAFUnitNumber_Invalid_Fomat_1() {
        ProviderAttribute providerAttributesForUnitNumber = getProviderAttribute(StringUtils.EMPTY, "Unit 500");
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(Arrays.asList(providerAttributesForUnitNumber), getInvalidStandardPostalAddress_Invalid_UnitFormat_1("Pitt", "Unit 500,"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertNull(address.getUnit());
    }

    @Test
    public void test_retrievePostalAddress_withGNAFUnitNumber_ValidUnitNumber_Pretext_1() {
        List<ProviderAttribute> providerAttributesUnitNumbers = getProviderAttributesForUnitNumber("180","180");
        ProviderAttribute providerAttributesForUnitNumber = getProviderAttribute(StringUtils.EMPTY, "abc def");
        providerAttributesUnitNumbers.add(providerAttributesForUnitNumber);
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesUnitNumbers, getInvalidStandardPostalAddress_Invalid_UnitFormat_1("Pitt", "abc def, Unit 180"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getUnit(),is("180"));
        assertThat(address.getBuilding(), is("abc def"));
    }

    @Test
    public void test_retrievePostalAddress_withGNAFUnitNumber_ValidUnitNumber_Pretext_2() {
        List<ProviderAttribute> providerAttributesForBuildingNameAndLevel = getProviderAttributesForBuildingNameAndLevel("Lennys Place", StringUtils.EMPTY, "28", StringUtils.EMPTY);
        ProviderAttribute providerAttributesForUnitNumber = getProviderAttribute(StringUtils.EMPTY, "abc def");
        providerAttributesForBuildingNameAndLevel.add(providerAttributesForUnitNumber);
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForBuildingNameAndLevel, getInvalidStandardPostalAddress_Invalid_UnitFormat_1("Pitt", "abc def, Unit 180"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getBuilding(), is("abc def Lennys Place"));
    }

    @Test
    public void test_retrievePostalAddress_withGNAFUnitNumber_ValidUnitNumber_Pretext_3() {
        List<ProviderAttribute> providerAttributesForBuildingNameAndLevel = getProviderAttributesForBuildingNameAndLevel("Lennys Place", StringUtils.EMPTY, "28", StringUtils.EMPTY);
        ProviderAttribute providerAttributesForUnitNumber = getProviderAttribute(StringUtils.EMPTY, "abc def");
        ProviderAttribute providerAttributesForUnitNumberTwo = getProviderAttribute(StringUtils.EMPTY, "pqr xyz test");
        providerAttributesForBuildingNameAndLevel.add(providerAttributesForUnitNumber);
        providerAttributesForBuildingNameAndLevel.add(providerAttributesForUnitNumberTwo);
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForBuildingNameAndLevel, getInvalidStandardPostalAddress_Invalid_UnitFormat_1("Pitt", "abc def, Unit 180"));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getBuilding(), is("abc def pqr xyz test Lennys Place"));
    }

    @Test
    public void test_retrievePostalAddress_withPoBox() {
        List<ProviderAttribute> providerAttributesForBuildingNameAndLevel = getProviderAttributesForPoBox("PO Box", "1111");
        RetrievePostalAddressResponse responseObj = getRetrievePostalAddressSuccessResponse(providerAttributesForBuildingNameAndLevel, getAddress(StringUtils.EMPTY));
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertThat(address.getStreetName(), is("PO Box 1111"));
    }

    @Test
    public void test_retrievePostalAddress_webservice_Params() {
        ArgumentCaptor<SamlToken> argument1 = ArgumentCaptor.forClass(SamlToken.class);
        ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RetrievePostalAddressRequest> argument3 = ArgumentCaptor.forClass(RetrievePostalAddressRequest.class);
        ArgumentCaptor<ServiceErrors> argument4 = ArgumentCaptor.forClass(ServiceErrors.class);
        RetrievePostalAddressResponse responseObj = new RetrievePostalAddressResponse();
        responseObj.setHasProviderAddress(new Provider());
        PostalAddressContactMethod address1 = new PostalAddressContactMethod();
        address1.setHasAddress(getAddress("Pitt"));
        responseObj.getHasPostalAddressContactMethod().add(address1);
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.SUCCESS);
        serviceStatus.getStatusInfo().add(statusInfo);
        responseObj.setServiceStatus(serviceStatus);
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);

        addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        verify(webServiceProvider, times(1)).sendWebServiceWithSecurityHeaderAndResponseCallback(argument1.capture(), argument2.capture(),
                argument3.capture(), argument4.capture());
        assertEquals(argument2.getValue(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_POSTAL_ADDRESS.getConfigName());
    }

    @Test
    public void test_retrievePostalAddress_error() {
        RetrievePostalAddressResponse responseObj = new RetrievePostalAddressResponse();
        Provider providerAddress = new Provider();
        responseObj.setHasProviderAddress(providerAddress);
        PostalAddressContactMethod address1 = new PostalAddressContactMethod();
        address1.setHasAddress(getAddress("Pitt"));
        responseObj.getHasPostalAddressContactMethod().add(address1);
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.ERROR);
        serviceStatus.getStatusInfo().add(statusInfo);
        responseObj.setServiceStatus(serviceStatus);
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        PostalAddress address = addressService.retrievePostalAddress("abc123", new ServiceErrorsImpl());
        assertNull(address.getBuilding());
        assertNull(address.getUnit());
        assertNull(address.getStreetNumber());
        assertNull(address.getStreetName());
        assertNull(address.getStreetType());
        assertNull(address.getSuburb());
        assertNull(address.getPostCode());
        assertNull(address.getStateAbbr());
        assertEquals(address.getCountryCode(), "AU");
    }

    @Test
    public void test_retrievePostalAddressGCM_Success() {
        ArgumentCaptor<SamlToken> argument1 = ArgumentCaptor.forClass(SamlToken.class);
        ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RetrievePostalAddressRequest> argument3 = ArgumentCaptor.forClass(RetrievePostalAddressRequest.class);
        ArgumentCaptor<ServiceErrors> argument4 = ArgumentCaptor.forClass(ServiceErrors.class);
        RetrievePostalAddressResponse responseObj = new RetrievePostalAddressResponse();
        responseObj.setHasProviderAddress(new Provider());
        PostalAddressContactMethod address1 = new PostalAddressContactMethod();
        address1.setHasAddress(getAddress("Pitt"));
        responseObj.getHasPostalAddressContactMethod().add(address1);
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.SUCCESS);
        serviceStatus.getStatusInfo().add(statusInfo);
        responseObj.setServiceStatus(serviceStatus);
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        
        RetrievePostalAddressRequest req = new RetrievePostalAddressRequest();
        req.setKey("abc123");
        req.setAddressType(AddressType.D);
        addressService.retrievePostalAddressForGCM(req, new ServiceErrorsImpl());
        verify(webServiceProvider, times(1)).sendWebServiceWithSecurityHeaderAndResponseCallback(argument1.capture(), argument2.capture(),
                argument3.capture(), argument4.capture());
        assertEquals(argument2.getValue(), WebServiceProviderConfig.GROUP_ESB_RETRIEVE_POSTAL_ADDRESS.getConfigName());
    }
    
    @Test
    public void test_retrievePostalAddressGCM_Error() throws JsonParseException, JsonMappingException, IOException {
        RetrievePostalAddressResponse responseObj = new RetrievePostalAddressResponse();
        responseObj.setHasProviderAddress(new Provider());
        PostalAddressContactMethod address1 = new PostalAddressContactMethod();
        address1.setHasAddress(getAddress("Pitt"));
        responseObj.getHasPostalAddressContactMethod().add(address1);
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.ERROR);
        serviceStatus.getStatusInfo().add(statusInfo);
        responseObj.setServiceStatus(serviceStatus);
        CorrelatedResponse response = new CorrelatedResponse(new CorrelationIdWrapper(), responseObj);
        when(webServiceProvider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                any(RetrievePostalAddressRequest.class), any(ServiceErrors.class))).thenReturn(response);
        
        RetrievePostalAddressRequest req = new RetrievePostalAddressRequest();
        req.setKey("abc123");
        req.setAddressType(AddressType.D);
        CustomerRawData rawData = addressService.retrievePostalAddressForGCM(req, new ServiceErrorsImpl());
        
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		RetrievePostalAddressResponse res = objectMapper.readValue(rawData.getRawResponse(), RetrievePostalAddressResponse.class);
        
        Assert.assertTrue(res.getHasPostalAddressContactMethod().isEmpty());
        assertNull(res.getHasProviderAddress());
    }
    
    private StandardPostalAddress getAddress(String streetName) {
        StandardPostalAddress address = new StandardPostalAddress();
        address.setUnitNumber("Unit 100");
        address.setStreetNumber("33");
        address.setStreetName(streetName);
        address.setStreetType("St");
        address.setCity("Sydney");
        address.setPostCode("2000");
        address.setState("NSW");
        return address;
    }

    private StandardPostalAddress getInvalidStandardPostalAddress_Invalid_UnitFormat_1(String streetName, String unitNumber) {
        StandardPostalAddress address = new StandardPostalAddress();
        address.setUnitNumber(unitNumber);
        address.setStreetNumber("33");
        address.setStreetName(streetName);
        address.setStreetType("St");
        address.setCity("Sydney");
        address.setPostCode("2000");
        address.setState("NSW");
        return address;
    }
    private List<ProviderAttribute> getProviderAttributesForBuildingNameAndLevel(String pafBuildingName, String gnafBuildingName, String pafBuildingLevel, String gnafBuildingLevel) {
        List<ProviderAttribute> providerAttributesList = new ArrayList<ProviderAttribute>();
        ProviderAttribute providerAttribute1 = getProviderAttribute("PAF Building name", pafBuildingName);
        ProviderAttribute providerAttribute2 = getProviderAttribute("G-NAF Building name", gnafBuildingName);
        ProviderAttribute providerAttribute3 = getProviderAttribute("PAF Building level (Number)", pafBuildingLevel);
        ProviderAttribute providerAttribute4 = getProviderAttribute("G-NAF Building level (Number)", gnafBuildingLevel);
        ProviderAttribute providerAttribute5 = getProviderAttribute("PAF Postcode", "");
        providerAttributesList.add(providerAttribute1);
        providerAttributesList.add(providerAttribute2);
        providerAttributesList.add(providerAttribute3);
        providerAttributesList.add(providerAttribute4);
        return providerAttributesList;
    }

    private List<ProviderAttribute> getProviderAttributesForPoBox(String poBoxType, String poBoxNumber) {
        ProviderAttribute providerAttribute1 = getProviderAttribute("All postal delivery types (Type)", poBoxType);
        ProviderAttribute providerAttribute2 = getProviderAttribute("All postal delivery types (Number)", poBoxNumber);

        return Arrays.asList(providerAttribute1, providerAttribute2);
    }

    private List<ProviderAttribute> getProviderAttributesForUnitNumber(String pafUnitNumber, String gnafUnitNumber){
        List<ProviderAttribute> providerAttributesList = new ArrayList<ProviderAttribute>();
        ProviderAttribute providerAttribute1 = getProviderAttribute("G-NAF Flat/Unit (Number)", gnafUnitNumber);
        ProviderAttribute providerAttribute2 = getProviderAttribute("PAF Flat/Unit (Number)", pafUnitNumber);
        providerAttributesList.add(providerAttribute1);
        providerAttributesList.add(providerAttribute2);
        return providerAttributesList;
    }

    private ProviderAttribute getProviderAttribute(String attributeName, String value) {
        ProviderAttribute providerAttribute1 = new ProviderAttribute();
        providerAttribute1.setAttributeName(attributeName);
        providerAttribute1.setAttributeValue(value);
        return providerAttribute1;
    }

    private RetrievePostalAddressResponse getRetrievePostalAddressSuccessResponse(List<ProviderAttribute> providerAttributes, StandardPostalAddress address) {
        RetrievePostalAddressResponse responseObj = new RetrievePostalAddressResponse();
        PostalAddressContactMethod address1 = new PostalAddressContactMethod();
        Provider providerAddress = new Provider();
        providerAddress.getHasProviderAddressAttribute().addAll(providerAttributes);
        responseObj.setHasProviderAddress(providerAddress);
        address1.setHasAddress(address);
        responseObj.getHasPostalAddressContactMethod().add(address1);
        ServiceStatus serviceStatus = new ServiceStatus();
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setLevel(Level.SUCCESS);
        serviceStatus.getStatusInfo().add(statusInfo);
        responseObj.setServiceStatus(serviceStatus);
        return responseObj;
    }
}
