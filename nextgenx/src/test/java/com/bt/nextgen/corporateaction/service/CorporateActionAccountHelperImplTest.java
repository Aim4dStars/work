package com.bt.nextgen.corporateaction.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAccountHelperImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAccountHelperImplTest {
    @InjectMocks
    private CorporateActionAccountHelperImpl corporateActionAccountHelper;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Before
    public void setup() {
        CorporateActionResponseConverterService converterService = mock(CorporateActionResponseConverterService.class);

        when(converterService
                .toSavedAccountElectionsDto(any(CorporateActionContext.class), anyString(), any(CorporateActionSavedDetails.class)))
                .thenReturn(mock(CorporateActionAccountElectionsDto.class));

        when(converterService.toSubmittedAccountElectionsDto(any(CorporateActionContext.class), any(CorporateActionAccount.class)))
                .thenReturn(mock(CorporateActionAccountElectionsDto.class));

        when(corporateActionConverterFactory.getResponseConverterService(any(CorporateActionDetails.class))).thenReturn(converterService);
    }

    @Test
    public void testGetAdviserName() {
        BrokerUser brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFullName()).thenReturn("Mr Broker");

        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);

        assertEquals("Mr Broker", corporateActionAccountHelper.getAdviserName(null, null));
    }

    @Test
    public void testGetPortfolioName() {
        Product product = mock(Product.class);
        when(product.getProductName()).thenReturn("My Product");

        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(product);

        assertEquals("My Product", corporateActionAccountHelper.getPortfolioName(null, null));
    }

    @Test
    public void testGetSubmittedElections() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);
        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);

        assertNotNull(corporateActionAccountHelper.getSubmittedElections(context, corporateActionAccount));

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.NOT_SUBMITTED);

        assertNull(corporateActionAccountHelper.getSubmittedElections(context, corporateActionAccount));
    }

    @Test
    public void testGetSavedElections() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getResponseCode()).thenReturn(CorporateActionResponseCode.SUCCESS);

        assertNotNull(corporateActionAccountHelper.getSavedElections(context, null, savedDetails));

        when(savedDetails.getResponseCode()).thenReturn(CorporateActionResponseCode.ERROR);

        assertNull(corporateActionAccountHelper.getSavedElections(context, null, savedDetails));

        assertNull(corporateActionAccountHelper.getSavedElections(context, null, null));
    }

    @Test
    public void testGetPreferredPhone() {
        Phone phone = mock(Phone.class);
        when(phone.getAreaCode()).thenReturn("08");
        when(phone.getNumber()).thenReturn("123456789");

        List<Phone> phones = Arrays.asList(phone);

        assertEquals("(08)1 2345 6789", corporateActionAccountHelper.getPreferredPhone(phones));

        when(phone.getAreaCode()).thenReturn(null);

        assertEquals("+123 456 789", corporateActionAccountHelper.getPreferredPhone(phones));

        assertNull(corporateActionAccountHelper.getPreferredPhone(new ArrayList<Phone>()));
        assertNull(corporateActionAccountHelper.getPreferredPhone(null));
    }

    @Test
    public void testGetPreferredEmail() {
        Email email = mock(Email.class);
        when(email.getEmail()).thenReturn("me@gmail.com");

        List<Email> emails = Arrays.asList(email);

        assertEquals("me@gmail.com", corporateActionAccountHelper.getPreferredEmail(emails));

        assertNull(corporateActionAccountHelper.getPreferredEmail(new ArrayList<Email>()));
        assertNull(corporateActionAccountHelper.getPreferredEmail(null));
    }

    @Test
    public void testGetPreferredAddress() {
        Address address = mock(Address.class);

        List<Address> addresses = Arrays.asList(address);

        when(address.isDomicile()).thenReturn(Boolean.TRUE);

        when(address.getSuburb()).thenReturn("Canning Vale");
        when(address.getStateAbbr()).thenReturn("WA");
        when(address.getPostCode()).thenReturn("6155");

        assertEquals("Canning Vale, WA 6155", corporateActionAccountHelper.getPreferredAddress(addresses));

        when(address.getUnit()).thenReturn("1");
        when(address.getStreetNumber()).thenReturn("40");
        when(address.getStreetName()).thenReturn("Bramdean");
        when(address.getStreetType()).thenReturn("Crescent");

        assertEquals("1/40 Bramdean Crescent, Canning Vale, WA 6155", corporateActionAccountHelper.getPreferredAddress(addresses));

        when(address.isDomicile()).thenReturn(Boolean.FALSE);
        assertNull(corporateActionAccountHelper.getPreferredAddress(addresses));

        assertNull(corporateActionAccountHelper.getPreferredAddress(new ArrayList<Address>()));
        assertNull(corporateActionAccountHelper.getPreferredAddress(null));
    }
}
