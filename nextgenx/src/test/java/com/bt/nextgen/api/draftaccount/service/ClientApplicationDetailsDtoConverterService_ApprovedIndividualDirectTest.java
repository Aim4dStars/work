package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientListDtoServiceImpl;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.IndividualDirectApplicationsDetailsDto;
import com.bt.nextgen.api.draftaccount.model.InvestmentChoiceDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.accountactivation.AccountStructure;
import com.bt.nextgen.service.avaloq.accountactivation.AssetInfo;
import com.bt.nextgen.service.avaloq.accountactivation.AssetInfoImpl;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetailsImpl;
import com.bt.nextgen.service.avaloq.accountactivation.RegisteredAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoConverterService_ApprovedIndividualDirectTest extends AbstractJsonReaderTest {
    public static final String ASSET_ID = "assetId";
    public static final String BT_MODERATE_PORTFOLIO = "BT Moderate Portfolio";
    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private InvestorDtoConverterForPersonDetail investorDtoConverterForPersonDetail;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private PersonMapperService personMapperService;

    @Mock
    private OrganisationMapper organisationMapper;

    @Mock
    FeatureTogglesService featureTogglesService;

    @Mock
    ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    @InjectMocks
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    private BrokerUser brokerUser;

    private BrokerKey mockAdviserKey;

    private IndividualDirectApplicationsDetailsDto individualDirectApplicationsDetailsDto;

    @Before
    public void setUp() throws IOException {

        brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFirstName()).thenReturn("John");
        when(brokerUser.getMiddleName()).thenReturn("Alan");
        when(brokerUser.getLastName()).thenReturn("Smith");

        AddressImpl address = new AddressImpl();
        address.setUnit("10");
        address.setStreetName("Pitt");
        address.setSuburb("Sydney");
        address.setState("NSW");
        address.setAddressKey(AddressKey.valueOf("ADDRESS"));
        when(brokerUser.getAddresses()).thenReturn(Arrays.<Address>asList(address));

        mockAdviserKey = BrokerKey.valueOf("adviserKey");
        when(brokerIntegrationService.getAdviserBrokerUser(eq(mockAdviserKey), any(ServiceErrorsImpl.class))).thenReturn(brokerUser);
        
        Broker broker = new BrokerAnnotationHolder();
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrorsImpl.class))).thenReturn(broker);
        
        final AssetImpl asset = new AssetImpl();
        asset.setAssetId(ASSET_ID);
        asset.setAssetName(BT_MODERATE_PORTFOLIO);
        when(assetIntegrationService.loadAsset(eq(ASSET_ID), any(ServiceErrorsImpl.class))).thenReturn(asset);

        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setFullPhoneNumber("0404040404");

        IndividualDto investorDto = new IndividualDto();
        investorDto.setFirstName("Dennis");
        investorDto.setMiddleName("R");
        investorDto.setLastName("Smith");
        investorDto.setPersonRoles(singletonList(InvestorRole.Primary_Contact));
        investorDto.setPhones(Arrays.asList(phoneDto));

        ApplicationDocumentDetail applicationDocument = createApplicationDocumentDetail();


        when(investorDtoConverterForPersonDetail.convertFromPersonDetail(any(PersonDetail.class),any(AccountSubType.class), anyMap())).thenReturn(investorDto);

        Product product = mock(Product.class);
        when(product.getProductName()).thenReturn("product name");
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrorsImpl.class))).thenReturn(product);
        individualDirectApplicationsDetailsDto = (IndividualDirectApplicationsDetailsDto) clientApplicationDetailsDtoConverterService.convert(applicationDocument, new ServiceErrorsImpl(), UserExperience.DIRECT );
    }

    @Test
    public void convert_shouldReturnDtoWithInvestorDetails() {
        List<InvestorDto> investors = individualDirectApplicationsDetailsDto.getInvestors();
        assertThat(investors.size(), is(1));
        PhoneDto phoneDto = investors.get(0).getPhones().get(0);
        assertThat(phoneDto.getFullPhoneNumber(), is("0404040404"));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convert_shouldReturnDtoWithAccountTypeDetails() {
        String accountType = individualDirectApplicationsDetailsDto.getInvestorAccountType();
        assertThat(accountType, is(IClientApplicationForm.AccountType.INDIVIDUAL.value()));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    @Test
    public void convert_shouldReturnDtoWithInvestmentChoiceDetails() {
        InvestmentChoiceDto investmentChoice = individualDirectApplicationsDetailsDto.getInvestmentChoice();
        assertThat(investmentChoice.getManagedPortfolio(), is(BT_MODERATE_PORTFOLIO));
        assertThat(investmentChoice.getInitialInvestmentAmount(), is(new BigDecimal("12345")));
        verify(clientApplicationDetailsDtoHelperService,times(1)).getExistingPersonsByCISKey(anyString(),any(List.class), any(ServiceErrors.class));
    }

    private ApplicationDocumentDetail createApplicationDocumentDetail() {
        PersonDetail personDetail = new PersonDetailImpl();

        AssetInfo assetInfo = new AssetInfoImpl();
        assetInfo.setAssetId(ASSET_ID);

        LinkedPortfolioDetails linkedPortfolioDetails = new LinkedPortfolioDetailsImpl();
        linkedPortfolioDetails.setAccountNumber("AccountNumber");
        linkedPortfolioDetails.setProductId("productId");
        linkedPortfolioDetails.setAccountType(AccountStructure.I);
        linkedPortfolioDetails.setAssetInfoList(Arrays.asList(assetInfo));

        RegisteredAccountImpl linkedAccountDto = new RegisteredAccountImpl();
        linkedAccountDto.setAccountNumber("123455");
        linkedAccountDto.setBsb("062000");
        linkedAccountDto.setPrimary(true);
        linkedAccountDto.setInitialDeposit(new BigDecimal(12345));


        ApplicationDocumentDetail appDoc = mock(ApplicationDocumentDetail.class);
        when(appDoc.getPersons()).thenReturn(Arrays.asList(personDetail));
        when(appDoc.getPortfolio()).thenReturn(Arrays.asList(linkedPortfolioDetails));
        when(appDoc.getLinkedAccounts()).thenReturn(Arrays.asList(linkedAccountDto));
        when(appDoc.getAdviserKey()).thenReturn(mockAdviserKey);
        when(appDoc.getAccountNumber()).thenReturn("12345");
        return appDoc;
    }

}
