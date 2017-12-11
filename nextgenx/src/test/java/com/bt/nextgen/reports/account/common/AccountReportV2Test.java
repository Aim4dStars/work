package com.bt.nextgen.reports.account.common;

import com.bt.nextgen.badge.model.Badge;
import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.client.ClientIntegrationServiceFactory;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountReportV2Test {
    @InjectMocks
    private final AccountReportV2 report = new AccountReportV2() {

        @Override
        public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
            return "test report type";
        }
    };

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productService;

    @Mock
    private ClientIntegrationService clientService;

    @Mock
    private OptionsService optionsService;

    @Mock
    private CmsService cmsService;

    @Mock
    private BadgingService badgingService;

    @Mock
    private Configuration configuration;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Mock
    private ClientIntegrationServiceFactory clientIntegrationServiceFactory;

    private final HashMap<String, Object> params = new HashMap<>();

    private WrapAccountDetail account;

    private Broker adviser;

    private Broker dealer;

    private Product product;

    private ClientDetail client;

    @Before
    public void setup() {
        params.put("account-id", "786CB50B45706219B075DF0521600EE8098D1A881047FF71");

        BrokerKey dealerKey = BrokerKey.valueOf("dealerId");
        dealer = mock(Broker.class);
        when(dealer.getKey()).thenReturn(dealerKey);
        when(dealer.getPositionName()).thenReturn("dealerName");

        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        adviser = mock(Broker.class);
        when(adviser.getKey()).thenReturn(adviserKey);
        when(adviser.getDealerKey()).thenReturn(dealerKey);

        BrokerUser user = mock(BrokerUser.class);
        when(user.getFirstName()).thenReturn("adviserFirstName");
        when(user.getLastName()).thenReturn("adviserLastName");
        Phone phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn("123456789");
        when(phone.isPreferred()).thenReturn(true);
        when(user.getPhones()).thenReturn(Collections.singletonList(phone));

        when(brokerService.getAdviserBrokerUser(eq(adviserKey), any(ServiceErrors.class))).thenReturn(user);

        when(brokerService.getBroker(eq(dealerKey), any(ServiceErrors.class))).thenReturn(dealer);
        when(brokerService.getBroker(eq(adviserKey), any(ServiceErrors.class))).thenReturn(adviser);

        ProductKey productKey = ProductKey.valueOf("productId");
        product = mock(Product.class);
        when(product.getProductName()).thenReturn("productName");
        when(productService.getProductDetail(eq(productKey), any(ServiceErrors.class))).thenReturn(product);


        Address address = mock(Address.class);
        when(address.getUnit()).thenReturn("unit");
        when(address.getStreetNumber()).thenReturn("streetNumber");
        when(address.getStreetName()).thenReturn("streetName");
        when(address.getStreetType()).thenReturn("streetType");
        when(address.getBuilding()).thenReturn("building");
        when(address.getSuburb()).thenReturn("suburb");
        when(address.getState()).thenReturn("state");
        when(address.getPostCode()).thenReturn("postcode");
        when(address.getAddressType()).thenReturn(AddressMedium.POSTAL);

        ClientKey clientKey = ClientKey.valueOf("clientId");
        client = mock(ClientDetail.class);
        when(client.getFullName()).thenReturn("fullName");
        phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn("0419555555");
        when(phone.isPreferred()).thenReturn(true);
        when(client.getPhones()).thenReturn(Collections.singletonList(phone));
        when(client.getAddresses()).thenReturn(Collections.singletonList(address));
        when(clientService.loadClientDetails(eq(clientKey), any(ServiceErrors.class))).thenReturn(client);
        when(clientIntegrationServiceFactory.getInstance(anyString())).thenReturn(clientService);

        PersonRelation relation = Mockito.mock(PersonRelation.class);
        when(relation.getClientKey()).thenReturn(clientKey);
        when(relation.isPrimaryContact()).thenReturn(true);
        Map<ClientKey, PersonRelation> relations = new HashMap<>();
        relations.put(clientKey, relation);

        account = mock(WrapAccountDetail.class);
        when(account.getAdviserKey()).thenReturn(adviserKey);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(account.getAccountNumber()).thenReturn("accountNumber");
        when(account.getAccountName()).thenReturn("accountName");
        when(account.getBsb()).thenReturn("bsb");
        when(account.getProductKey()).thenReturn(productKey);
        when(account.getAssociatedPersons()).thenReturn(relations);

        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        when(accountIntegrationServiceFactory.getInstance(anyString())).thenReturn(accountService);

        Badge badge = mock(Badge.class);
        when(badge.getReportLogoV2()).thenReturn("cms/vectorImage.svg");
        when(badgingService.getBadgeForCurrentUser(any(ServiceErrors.class))).thenReturn(badge);
        when(configuration.getString(Mockito.anyString())).thenReturn("classpath:/");
        when(cmsService.getContent(anyString())).thenReturn("cms/rasterImage.png");

        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
    }

    @Test
    public void testGetAccountFooter_whenProvidedValidDetails_accountDetailsIsPopulated() throws ParseException {
        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        footer.setDisplayBsbAndAccountNumber(true);
        assertTrue(footer.getAccountDetails().contains("accountName"));
        assertTrue(footer.getAccountDetails().contains("bsb"));
        assertTrue(footer.getAccountDetails().contains("accountNumber"));
        assertTrue(footer.getAccountDetails().contains("productName"));
        assertTrue(footer.getAccountDetails().contains("Individual"));
    }

    @Test
    public void testGetAccountFooter_whenProvidedValidDetails_primaryContactIsPopulated() throws ParseException {
        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        assertTrue(footer.getPrimaryContact().contains("fullName"));
        assertTrue(footer.getPrimaryContact().contains("04 195 555 55"));
        assertTrue(footer.getPrimaryContact().contains("unit"));
        assertTrue(footer.getPrimaryContact().contains("streetNumber"));
        assertTrue(footer.getPrimaryContact().contains("streetName"));
        assertTrue(footer.getPrimaryContact().contains("streetType"));
        assertTrue(footer.getPrimaryContact().contains("building"));
        assertTrue(footer.getPrimaryContact().contains("suburb"));
        assertTrue(footer.getPrimaryContact().contains("state"));
        assertTrue(footer.getPrimaryContact().contains("postcode"));
    }

    @Test
    public void testGetAccountFooter_whenProvidedValidDetails_adviserDetailsIsPopulated() throws ParseException {
        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        footer.setDisplayBsbAndAccountNumber(true);
        assertTrue(footer.getAdviserDetails().contains("adviserFirstName"));
        assertTrue(footer.getAccountDetails().contains("bsb"));
        assertTrue(footer.getAdviserDetails().contains("adviserLastName"));
        assertTrue(footer.getAdviserDetails().contains("+1234 567 89"));
        assertTrue(footer.getAdviserDetails().contains("dealerName"));
    }

    @Test
    public void testGetAccountFooter_whenProvidedValidDetails_adviserDetailsIsPopulated_forDirect() throws ParseException {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(UserExperience.DIRECT);
        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        footer.setDisplayBsbAndAccountNumber(true);
        assertEquals(footer.getAdviserDetails(), "");
        assertNull(footer.getIconAdviser());
    }

    @Test
    public void testGetAccountFooter_whenProvidedValidDetails_corporateAdviserDetailsIsPopulated() throws ParseException {
        BrokerUser user = mock(BrokerUser.class);
        when(user.getFirstName()).thenReturn("adviserFirstName");
        when(user.getLastName()).thenReturn("adviserLastName");
        when(user.getCorporateName()).thenReturn("corporateAdviserName");
        Phone phone = mock(Phone.class);
        when(phone.getNumber()).thenReturn("123456789");
        when(phone.isPreferred()).thenReturn(true);
        when(user.getPhones()).thenReturn(Collections.singletonList(phone));

        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        when(brokerService.getAdviserBrokerUser(eq(adviserKey), any(ServiceErrors.class))).thenReturn(user);

        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        footer.setDisplayBsbAndAccountNumber(true);
        assertTrue(footer.getAdviserDetails().contains("corporateAdviserName"));
        assertTrue(footer.getAccountDetails().contains("bsb"));
        assertTrue(footer.getAdviserDetails().contains("+1234 567 89"));
        assertTrue(footer.getAdviserDetails().contains("dealerName"));
        assertFalse(footer.getAdviserDetails().contains("adviserFirstName"));
        assertFalse(footer.getAdviserDetails().contains("adviserLastName"));
    }


    @Test
    public void testGetAccountHeader() throws ParseException {
        Map<String, Object> data = new HashMap<>();
        AccountHeaderReportData header = report.getAccountHeader(params, data);
        header.setDisplayBsbAndAccountNumber(true);
        assertEquals("accountName", header.getAccountName());
        assertEquals("accountNumber", header.getAccountNumber());
        assertEquals("BSB bsb Account number accountNumber", header.getBsbAccountNumber());
        assertEquals("Individual", header.getAccountStructure());
    }


    @Test
    public void testGetAccountHeader_SuperAccountStructureType() throws ParseException {
        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        ProductKey productKey = ProductKey.valueOf("productId");
        account = mock(WrapAccountDetail.class);
        when(account.getAdviserKey()).thenReturn(adviserKey);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getAccountNumber()).thenReturn("accountNumber");
        when(account.getAccountName()).thenReturn("accountName");
        when(account.getProductKey()).thenReturn(productKey);
        when(account.getAssociatedPersons()).thenReturn(getRelations());
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
        Map<String, Object> data = new HashMap<>();
        AccountHeaderReportData header = report.getAccountHeader(params, data);
        assertEquals("Super", header.getAccountStructure());
    }

    @Test
    public void testGetAccountHeader_PensionStandardAccountStructureType() throws ParseException {
        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        ProductKey productKey = ProductKey.valueOf("productId");
        PensionAccountDetail account = mock(PensionAccountDetailImpl.class);
        when(account.getPensionType()).thenReturn(PensionType.STANDARD);
        when(((WrapAccountDetail) account).getAdviserKey()).thenReturn(adviserKey);
        when(((WrapAccountDetail) account).getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(((WrapAccountDetail) account).getAccountNumber()).thenReturn("accountNumber");
        when(((WrapAccountDetail) account).getAccountName()).thenReturn("accountName");
        when(((WrapAccountDetail) account).getProductKey()).thenReturn(productKey);
        when(((WrapAccountDetail) account).getAssociatedPersons()).thenReturn(getRelations());
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(((WrapAccountDetail) account));
        Map<String, Object> data = new HashMap<>();
        AccountHeaderReportData header = report.getAccountHeader(params, data);
        assertEquals("Pension", header.getAccountStructure());
    }

    @Test
    public void testGetAccountHeader_PensionTTRAccountStructureType() throws ParseException {
        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        ProductKey productKey = ProductKey.valueOf("productId");
        PensionAccountDetail account = mock(PensionAccountDetailImpl.class);
        when(account.getPensionType()).thenReturn(PensionType.TTR);
        when(((WrapAccountDetail) account).getAdviserKey()).thenReturn(adviserKey);
        when(((WrapAccountDetail) account).getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(((WrapAccountDetail) account).getAccountNumber()).thenReturn("accountNumber");
        when(((WrapAccountDetail) account).getAccountName()).thenReturn("accountName");
        when(((WrapAccountDetail) account).getProductKey()).thenReturn(productKey);
        when(((WrapAccountDetail) account).getAssociatedPersons()).thenReturn(getRelations());
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(((WrapAccountDetail) account));
        Map<String, Object> data = new HashMap<>();

        // //If PensionType is Pension TTR -taxed
        AccountHeaderReportData header = report.getAccountHeader(params, data);
        assertEquals(PensionType.TTR.getLabel(), header.getAccountStructure());

        //If PensionType is Pension TTR -Retirement
        when(account.getPensionType()).thenReturn(PensionType.TTR_RETIR_PHASE);
        header = report.getAccountHeader(params, data);
        assertEquals(PensionType.TTR_RETIR_PHASE.getLabel(), header.getAccountStructure());
    }

    private Map<ClientKey, PersonRelation> getRelations() {
        ClientKey clientKey = ClientKey.valueOf("clientId");
        PersonRelation relation = Mockito.mock(PersonRelation.class);
        Map<ClientKey, PersonRelation> relations = new HashMap<>();
        relations.put(clientKey, relation);
        return relations;
    }

    @Test
    public void testGetAccountFooter_SuperAccountStructureType() throws ParseException {
        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        ProductKey productKey = ProductKey.valueOf("productId");
        account = mock(WrapAccountDetail.class);
        when(account.getAdviserKey()).thenReturn(adviserKey);
        when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(account.getAccountNumber()).thenReturn("accountNumber");
        when(account.getAccountName()).thenReturn("accountName");
        when(account.getBsb()).thenReturn("bsb");
        when(account.getProductKey()).thenReturn(productKey);
        when(account.getAssociatedPersons()).thenReturn(getRelations());
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);

        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        assertTrue(footer.getAccountDetails().contains("Super"));
    }


    @Test
    public void testGetAccountFooter_PensionStandardAccountStructureType() throws ParseException {
        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        ProductKey productKey = ProductKey.valueOf("productId");
        PensionAccountDetail account = mock(PensionAccountDetailImpl.class);
        when(account.getPensionType()).thenReturn(PensionType.STANDARD);
        when(((WrapAccountDetail) account).getAdviserKey()).thenReturn(adviserKey);
        when(((WrapAccountDetail) account).getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(((WrapAccountDetail) account).getAccountNumber()).thenReturn("accountNumber");
        when(((WrapAccountDetail) account).getAccountName()).thenReturn("accountName");
        when(((WrapAccountDetail) account).getBsb()).thenReturn("bsb");
        when(((WrapAccountDetail) account).getProductKey()).thenReturn(productKey);
        when(((WrapAccountDetail) account).getAssociatedPersons()).thenReturn(getRelations());
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(((WrapAccountDetail) account));

        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        assertTrue(footer.getAccountDetails().contains("Pension"));
    }

    @Test
    public void testGetAccountFooter_PensionTTRAccountStructureType() throws ParseException {
        BrokerKey adviserKey = BrokerKey.valueOf("adviserId");
        ProductKey productKey = ProductKey.valueOf("productId");
        PensionAccountDetail account = mock(PensionAccountDetailImpl.class);
        when(account.getPensionType()).thenReturn(PensionType.TTR);
        when(((WrapAccountDetail) account).getAdviserKey()).thenReturn(adviserKey);
        when(((WrapAccountDetail) account).getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        when(((WrapAccountDetail) account).getAccountNumber()).thenReturn("accountNumber");
        when(((WrapAccountDetail) account).getAccountName()).thenReturn("accountName");
        when(((WrapAccountDetail) account).getBsb()).thenReturn("bsb");
        when(((WrapAccountDetail) account).getProductKey()).thenReturn(productKey);
        when(((WrapAccountDetail) account).getAssociatedPersons()).thenReturn(getRelations());
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(((WrapAccountDetail) account));

        Map<String, Object> data = new HashMap<>();
        AccountFooterReportData footer = report.getAccountFooter(params, data);
        assertTrue(footer.getAccountDetails().contains(PensionType.TTR.getLabel()));
    }

    @Test
    public void testGetIsTrusteeDisclaimerRequired_whenHasFeature_thenTrue() {
        when(optionsService.hasFeature(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn(true);
        assertTrue(report.getIsTrusteeDisclaimerRequired(params));
    }

    @Test
    public void testGetTrusteeDisclaimer_whenRequired_thenContentReturned() {
        when(optionsService.hasFeature(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn(true);
        when(cmsService.getContent(anyString())).thenReturn("DS-IP-0146");
        String content = report.getTrusteeDisclaimer(params);
        assertEquals("DS-IP-0146", content);
    }

    @Test
    public void testGetTrusteeDisclaimer_whenNotRequired_thenNullReturned() {
        when(optionsService.hasFeature(any(OptionKey.class), any(AccountKey.class), any(ServiceErrors.class))).thenReturn(false);
        String content = report.getTrusteeDisclaimer(params);
        assertNull(content);
    }

    @Test
    public void testGetSummaryDescription_returnsValue() {
        String value = report.getSummaryDescription(params, params);
        assertEquals("", value);
    }

    @Test
    public void testGetSummaryValue_returnsValue() {
        String value = report.getSummaryValue(params, params);
        assertEquals("", value);
    }

    @Test
    public void testGetReportFileName_returnsValue() {
        String value = report.getReportFileName(params, params);
        assertEquals(account.getAccountNumber() + " - " + "test report type", value);
    }

    @Test
    public void testGetAccountEncodedId() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "1");

        assertEquals("1", report.getAccountEncodedId(params));
    }

    public void getUserExperience() {
        assertEquals(report.getUserExperience(params, params), UserExperience.ADVISED);
    }

    @Test
    public void getUserExperience_existingData() {
        final String accountId = EncodedString.toPlainText((String) params.get("account-id"));
        params.put("AccountReportV2.userExperienceData." + accountId, UserExperience.DIRECT);
        assertEquals(report.getUserExperience(params, params), UserExperience.DIRECT);
    }

    @Test
    public void getUserExperience_noUserExperience() {
        when(brokerHelperService.getUserExperience(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(null);
        assertNull(report.getUserExperience(params, params));
    }
}
