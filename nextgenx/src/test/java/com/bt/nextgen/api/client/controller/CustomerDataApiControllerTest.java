package com.bt.nextgen.api.client.controller;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.service.CustomerDataDto;
import com.bt.nextgen.api.client.service.CustomerDataDtoService;
import com.bt.nextgen.api.client.service.DirectInvestorDataDtoService;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.core.security.SamlAuthenticationDetailsSource;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.saml.SamlToken;
import org.apache.ws.security.saml.SAMLUtil;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.service.ClientKeyDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.web.bind.WebDataBinder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by F030695 on 16/12/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerDataApiControllerTest {

    @InjectMocks
    private CustomerDataApiController customerDataApiController;

    @Mock
    private FeatureTogglesService togglesService;

    @Mock
    private ClientKeyDtoService clientKeyDtoService;

    @Mock
    private DirectInvestorDataDtoService directInvestorDataDtoService;

    @Mock
    private SamlAuthenticationDetailsSource samlSource;

    @Mock
    private CustomerDataDtoService customerDataDtoService;



    @Test
    public void test_getCustomerDataByOperation() {
        String type  = "tin";
        String cisKey = "12345678";

        final ClientUpdateKey id = new ClientUpdateKey("", type, cisKey, "INDIVIDUAL");

        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setKey(id);
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class),any(ServiceErrors.class))).thenReturn(customerDataDto);
        ApiResponse response = customerDataApiController.getCustomerDataByOperation("12345678","");
        assertNotNull(response.getData());
        assertNotNull(((CustomerDataDto)response.getData()).getKey().getCisId(),is("12345678"));

    }

    @Test
    public void test_globalUpdate() {
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setCisKey("12345678");
        customerDataDto.setUpdatedAttribute("TIN");
        when(customerDataDtoService.update(any(CustomerDataDto.class),any(ServiceErrors.class))).thenReturn(customerDataDto);
        ApiResponse response = customerDataApiController.globalUpdate(customerDataDto);
        assertNotNull(((CustomerDataDto)response.getData()).getKey().getCisId(),is("12345678"));
    }

    @Test
    public void test_getClientKey_success() {
        FeatureToggles toggleResponse = new FeatureToggles();
        toggleResponse.setFeatureToggle("wplLiveIntegration", false);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(toggleResponse);

        ClientIdentificationDto clientKeyResponse = new ClientIdentificationDto();
        ClientKey key = new ClientKey("112233");
        clientKeyResponse.setKey(key);
        when(clientKeyDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientKeyResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ApiResponse response = customerDataApiController.getClientKeyForDirectCustomer("123456789", request);
        assertNotNull(response.getData());
        assertThat(((ClientIdentificationDto) response.getData()).getKey(), is(new ClientKey("112233")));
    }

    @Test
    public void test_getClientKey_noResult() {
        FeatureToggles toggleResponse = new FeatureToggles();
        toggleResponse.setFeatureToggle("wplLiveIntegration", false);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(toggleResponse);

        when(clientKeyDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(new ClientIdentificationDto());

        MockHttpServletRequest request = new MockHttpServletRequest();
        ApiResponse response = customerDataApiController.getClientKeyForDirectCustomer("123456789", request);
        assertNotNull(response.getData());
        assertNull(((ClientIdentificationDto) response.getData()).getKey());
    }

    @Mock
    WebDataBinder dataBinder;

    @Captor
     ArgumentCaptor argCaptor;

    @Test
    public void test_globalUpdateBinder(){
        customerDataApiController.globalUpdateBinder(dataBinder);
        verify(dataBinder,times(1)).setAllowedFields("taxResidenceCountries","taxResidenceCountries[*].taxResidenceCountry","taxResidenceCountries[*].tin","taxResidenceCountries[*].taxExemptionReason","taxResidenceCountries[*].startDate","taxResidenceCountries[*].endDate","taxResidenceCountries[*].versionNumber",
                "updatedAttribute", "investorTypeUpdated", "cisKey");
    }

    @Test(expected = BadRequestException.class)
    public void test_getClientKey_incorrectPanLength() {
        FeatureToggles toggleResponse = new FeatureToggles();
        toggleResponse.setFeatureToggle("wplLiveIntegration", false);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(toggleResponse);

        ClientIdentificationDto clientKeyResponse = new ClientIdentificationDto();
        ClientKey key = new ClientKey("112233");
        clientKeyResponse.setKey(key);
        when(clientKeyDtoService.find(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(clientKeyResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        customerDataApiController.getClientKeyForDirectCustomer("12345678", request);
    }

    @Test
    public void test_getDirectCustomerData_ExistingPanoramaUser() {
        setAuthenticatedProfile();
        FeatureToggles toggleResponse = new FeatureToggles();
        toggleResponse.setFeatureToggle("wplLiveIntegration", true);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(toggleResponse);

        CustomerDataDto customerDataDto = new CustomerDataDto();
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), any(ServiceErrors.class))).thenReturn(customerDataDto);
        ArgumentCaptor<ClientUpdateKey> argument = ArgumentCaptor.forClass(ClientUpdateKey.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        ApiResponse response = customerDataApiController.getDirectCustomerData("tfn", "123456789", "DUMMY_TFN", request);
        assertNotNull(response.getData());
        verify(directInvestorDataDtoService,times(1)).find(argument.capture(), any(ServiceErrors.class));
        assertEquals(argument.getValue().getClientId(),"217082760");
    }

    @Test
    public void test_getDirectCustomerData_NewPanoramaUser() {
        setWPLProfile();
        FeatureToggles toggleResponse = new FeatureToggles();
        toggleResponse.setFeatureToggle("wplLiveIntegration", true);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(toggleResponse);

        CustomerDataDto customerDataDto = new CustomerDataDto();
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), any(ServiceErrors.class))).thenReturn(customerDataDto);
        ArgumentCaptor<ClientUpdateKey> argument = ArgumentCaptor.forClass(ClientUpdateKey.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ApiResponse response = customerDataApiController.getDirectCustomerData("tfn","123456789", "DUMMY_TFN",request);
        assertNotNull(response.getData());
        verify(directInvestorDataDtoService,times(1)).find(argument.capture(), any(ServiceErrors.class));
        assertEquals(argument.getValue().getClientId(),"");
    }

    @Test
    public void test_getDirectCustomerData_NewPanoramaUser_Invalid() {
        setInvalidProfile();
        FeatureToggles toggleResponse = new FeatureToggles();
        toggleResponse.setFeatureToggle("wplLiveIntegration", true);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(toggleResponse);

        CustomerDataDto customerDataDto = new CustomerDataDto();
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), any(ServiceErrors.class))).thenReturn(customerDataDto);
        ArgumentCaptor<ClientUpdateKey> argument = ArgumentCaptor.forClass(ClientUpdateKey.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        ApiResponse response = customerDataApiController.getDirectCustomerData("tfn","123456789", "DUMMY_TFN",request);
        assertNotNull(response.getData());
        verify(directInvestorDataDtoService,times(1)).find(argument.capture(), any(ServiceErrors.class));
        assertEquals(argument.getValue().getClientId(),"");
    }

    private void setAuthenticatedProfile(){

        final String saml = SamlUtil.loadSaml();
        SamlToken samlToken = new SamlToken(saml);
        Profile profile = new Profile(samlToken);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(samlSource.buildDetails(any(HttpServletRequest.class))).thenReturn(profile);
    }

    private void setWPLProfile(){

        final String saml = SamlUtil.loadWplSamlNewPanoramaCustomer();
        SamlToken samlToken = new SamlToken(saml);
        Profile profile = new Profile(samlToken);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(samlSource.buildDetails(any(HttpServletRequest.class))).thenReturn(profile);
    }

    private void setInvalidProfile(){
        final String saml = SamlUtil.loadSamlwithoutGCMId();
        SamlToken samlToken = new SamlToken(saml);
        Profile profile = new Profile(samlToken);
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(samlSource.buildDetails(any(HttpServletRequest.class))).thenReturn(profile);
    }
}
